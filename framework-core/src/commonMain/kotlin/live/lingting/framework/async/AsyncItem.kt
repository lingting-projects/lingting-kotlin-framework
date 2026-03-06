package live.lingting.framework.async

/**
 * @author lingting 2026/2/25 15:17
 */
data class AsyncItem(
    val name: String? = null,
    internal val block: suspend AsyncItem.() -> Unit,
    val id: String = _root_ide_package_.live.lingting.framework.util.ValueUtils.uuid(),
) {

    var status: Status = Status.WAIT
        private set

    var runTimestamp: Long = 0
        private set

    var abortTimestamp: Long = 0
        private set

    var finishTimestamp: Long = 0
        private set

    val createTimestamp: Long = _root_ide_package_.live.lingting.framework.time.DateTime.millis()

    val isFinish
        get() = status == Status.SUCCESS || status == Status.FAIL || status == Status.ABORT

    internal fun toRun() {
        status = Status.RUNNING
        runTimestamp = _root_ide_package_.live.lingting.framework.time.DateTime.millis()
        finishTimestamp = 0
    }

    internal fun toAbort() {
        if (isFinish) {
            return
        }
        status = Status.ABORT
        abortTimestamp = _root_ide_package_.live.lingting.framework.time.DateTime.millis()
    }

    internal fun toFinish(status: Status) {
        require(status == Status.SUCCESS || status == Status.FAIL) { "完成状态异常!" }
        if (status != Status.ABORT) {
            this.status = status
        }
        this.finishTimestamp = _root_ide_package_.live.lingting.framework.time.DateTime.millis()
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
