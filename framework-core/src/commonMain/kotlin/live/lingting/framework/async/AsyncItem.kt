package live.lingting.framework.async

import live.lingting.framework.time.DateTime
import live.lingting.framework.util.ValueUtils

/**
 * @author lingting 2026/2/25 15:17
 */
data class AsyncItem(
    val name: String? = null,
    internal val block: suspend AsyncItem.() -> Unit,
    val id: String = ValueUtils.uuid(),
) {

    var status: Status = Status.WAIT
        private set

    var runTimestamp: Long = 0
        private set

    var abortTimestamp: Long = 0
        private set

    var finishTimestamp: Long = 0
        private set

    val createTimestamp: Long = DateTime.millis()

    val isFinish
        get() = status == Status.SUCCESS || status == Status.FAIL || status == Status.ABORT

    internal fun toRun() {
        status = Status.RUNNING
        runTimestamp = DateTime.millis()
        finishTimestamp = 0
    }

    internal fun toAbort() {
        if (isFinish) {
            return
        }
        status = Status.ABORT
        abortTimestamp = DateTime.millis()
    }

    internal fun toFinish(status: Status) {
        require(status == Status.SUCCESS || status == Status.FAIL) { "完成状态异常!" }
        if (status != Status.ABORT) {
            this.status = status
        }
        this.finishTimestamp = DateTime.millis()
    }

    internal fun toSuccess() = toFinish(Status.SUCCESS)

    internal fun toFail() = toFinish(Status.FAIL)

    enum class Status {

        WAIT,

        RUNNING,

        SUCCESS,

        FAIL,

        ABORT,

    }

}
