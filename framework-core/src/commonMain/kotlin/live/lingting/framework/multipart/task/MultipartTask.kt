package live.lingting.framework.multipart.task

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import live.lingting.framework.async.Async
import live.lingting.framework.async.async
import live.lingting.framework.multipart.Multipart
import live.lingting.framework.util.LoggerUtils.logger
import kotlin.jvm.JvmField
import kotlin.jvm.JvmOverloads
import kotlin.time.Duration

/**
 * @author lingting 2026/2/13 18:28
 */
abstract class MultipartTask @JvmOverloads protected constructor(
    val multipart: Multipart,
    @JvmField protected val async: Async = async()
) {

    protected val log = logger()

    protected val statusRef = atomic(Status.WAIT)

    val status: Status
        get() = statusRef.value

    val isStarted: Boolean
        get() = status != Status.WAIT

    val isCompleted: Boolean
        get() = status.isCompleted

    val isSuccessful: Boolean
        get() = status == Status.SUCCESSFUL

    val isFailed: Boolean
        get() = status == Status.FAILED

    val id: String
        get() = multipart.id

    protected val tasks = multipart.parts.map { PartTask(it) }

    val completedNumber: Int
        get() = successfulNumber + failedNumber

    var successfulNumber: Int = 0
        protected set

    var failedNumber: Int = 0
        protected set

    var retryMaxCount: Int = -1

    protected val lock = reentrantLock()

    fun start() {
        require(tasks.isNotEmpty()) { "无可用分片! 请检查构造参数" }
        if (isStarted || !statusRef.compareAndSet(Status.WAIT, Status.RUNNING)) {
            return
        }
        onStarted()
        tasks.forEach { task ->
            async.submit {
                try {
                    while (true) {
                        try {
                            task.status =
                                PartTask.Status.RUNNING
                            onPart(task)
                            task.t = null
                            task.status =
                                PartTask.Status.SUCCESSFUL
                            break
                        } catch (t: Throwable) {
                            task.t = t
                            task.status =
                                PartTask.Status.FAILED
                            if (!allowRetry(task, t)) {
                                break
                            }
                            task.retryCount += 1
                            delay(50)
                        }
                    }
                } finally {
                    update()
                }
            }
        }
    }

    @JvmOverloads
    suspend fun await(timeout: Duration? = null) {
        live.lingting.framework.concurrent.Await.waitTrue(timeout) { isCompleted }
    }

    protected open suspend fun update() {
        if (!isStarted || isCompleted) {
            return
        }
        lock.withLock {
            if (!isStarted || isCompleted) {
                return@withLock
            }
            var sn = 0
            var fn = 0
            var tx: Throwable? = null

            tasks.filter { it.isCompleted }
                .forEach {
                    if (it.isSuccessful) {
                        sn += 1
                    } else {
                        fn += 1
                        if (tx == null) {
                            tx = it.t
                        }
                    }
                }
            successfulNumber = sn
            failedNumber = fn

            if (successfulNumber + failedNumber != tasks.size) {
                return@withLock
            }

            if (successfulNumber == tasks.size) {
                if (statusRef.compareAndSet(Status.RUNNING, Status.MERGING)) {
                    try {
                        onMerge()
                    } catch (t: Throwable) {
                        tx = t
                    }
                }
            }
            if (failedNumber == 0) {
                statusRef.value = Status.SUCCESSFUL
                onSuccessful()
            } else {
                statusRef.value = Status.FAILED
                log.error(tx) { "[分片任务] 执行异常!" }
                onFailed(tx)
            }
        }
    }

    protected open fun onStarted() {
        //
    }

    protected abstract suspend fun onPart(task: PartTask)

    protected open suspend fun onMerge() {
        //
    }

    protected open suspend fun onSuccessful() {
        //
    }

    protected open suspend fun onFailed(t: Throwable?) {
        //
    }

    protected open fun allowRetry(task: PartTask, t: Throwable?): Boolean {
        // 已失败数量大于0, 则不再进行重试.
        if (failedNumber > 0) {
            return false
        }
        if (task.retryCount >= retryMaxCount) {
            return false
        }
        return t !is CancellationException && t !is kotlin.coroutines.cancellation.CancellationException
    }

    @Serializable(with = Status.Serializer::class)
    enum class Status {

        WAIT,

        RUNNING,

        MERGING,

        FAILED,

        SUCCESSFUL,

        ;

        class Serializer : live.lingting.framework.serializable.EnumSerializer<Status>(enumValues<Status>())

        val isCompleted
            get() = this == SUCCESSFUL || this == FAILED

    }

}
