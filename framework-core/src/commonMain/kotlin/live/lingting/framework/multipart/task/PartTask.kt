package live.lingting.framework.multipart.task

import kotlinx.serialization.Serializable

/**
 * @author lingting 2026/2/13 18:28
 */
class PartTask(val part: live.lingting.framework.multipart.Part) {

    var t: Throwable? = null

    var status: Status = Status.WAIT

    var retryCount: Long = 0L

    var data: Any? = null

    val isCompleted: Boolean
        get() = isSuccessful || isFailed

    val isSuccessful: Boolean
        get() = status == Status.SUCCESSFUL

    val isFailed: Boolean
        get() = status == Status.FAILED

    override fun equals(other: Any?): Boolean {
        if (other !is PartTask) return false
        return part == other.part
    }

    override fun hashCode(): Int {
        return part.hashCode()
    }

    @Serializable(with = Status.Serializer::class)
    enum class Status {

        WAIT,

        RUNNING,

        FAILED,

        SUCCESSFUL,

        ;

        class Serializer : live.lingting.framework.serializable.EnumSerializer<Status>(enumValues<Status>())

    }

}
