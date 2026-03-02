package live.lingting.kotlin.framework.multipart

import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import live.lingting.kotlin.framework.data.DataSize
import live.lingting.kotlin.framework.util.DataSizeUtils.bytes
import live.lingting.kotlin.framework.util.ValueUtils
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic


/**
 * @author lingting 2024-09-05 14:47
 */
open class Multipart(
    /**
     * 唯一标识符
     */
    @JvmField
    val id: String,
    /**
     * 原始内容大小
     */
    @JvmField
    val size: DataSize,
    /**
     * 每个分片的最大大小
     */
    @JvmField
    val partSize: DataSize,
    /**
     * 所有分片
     */
    @JvmField
    val parts: Collection<Part>,
) {

    companion object {

        @JvmStatic
        fun build(block: Builder.() -> Unit): Multipart = builder().apply(block).build()

        @JvmStatic
        fun builder(): Builder {
            return Builder()
        }

        /**
         * 计算对应大小和每个分片大小需要构造多少个分片
         * @param size     总大小
         * @param partSize 每个分片大小
         */
        @JvmStatic
        fun calculate(size: DataSize, partSize: DataSize): Long {
            val d = size / partSize
            val l = d.toLong()
            if (l.toDouble() == d) {
                return l
            }
            return l + 1
        }

        @JvmStatic
        fun split(size: DataSize, partSize: DataSize): Collection<Part> {
            val number = calculate(size, partSize)
            val parts: MutableList<Part> = ArrayList(number.toInt())
            for (i in 0 until number) {
                val start = partSize * i
                val middle = start + partSize - 1
                val end = if (middle >= size) size - 1 else middle
                val part = Part(i, start, end)
                parts.add(part)
            }
            return parts.toList()
        }

    }

    fun usePartSize(partSize: DataSize): Multipart {
        return usePartSize(partSize, id)
    }

    fun usePartSize(partSize: DataSize, id: String): Multipart {
        return Multipart(id, size, partSize, parts)
    }

    class Builder {

        var partSize: DataSize = DataSize.ZERO
            private set

        var id: String = ValueUtils.simpleUuid()
            private set

        var size: DataSize = DataSize.ZERO
            private set

        var maxPartSize: DataSize = DataSize.ZERO
            private set

        var minPartSize: DataSize = DataSize.ZERO
            private set

        var maxPartCount: Long = 0
            private set

        fun partSize(partSize: DataSize): Builder {
            this.partSize = partSize
            return this
        }

        fun id(id: String): Builder {
            this.id = id
            return this
        }

        fun size(size: DataSize): Builder {
            this.size = size
            return this
        }

        fun size(path: Path): Builder {
            val metadata = SystemFileSystem.metadataOrNull(path)
            requireNotNull(metadata) { "文件未找到! $path" }
            return size(metadata.size.bytes)
        }

        fun maxPartSize(maxPartSize: DataSize): Builder {
            this.maxPartSize = maxPartSize
            return this
        }

        fun minPartSize(minPartSize: DataSize): Builder {
            this.minPartSize = minPartSize
            return this
        }

        fun maxPartCount(maxPartCount: Long): Builder {
            this.maxPartCount = maxPartCount
            return this
        }

        fun parts(): Collection<Part> {
            if (minPartSize.bytes > 0 && partSize < minPartSize) {
                partSize(minPartSize)
            }
            val number = calculate(size, partSize)
            // 限制了最大分片数量. 超过之后重新分配每片大小
            if (maxPartCount in 1..<number) {
                partSize(partSize + (partSize / 2))
                return parts()
            }
            // 限制了分片最大大小, 超时后需要缩减
            if (maxPartSize.bytes > 0 && partSize > maxPartSize) {
                if (size < minPartSize) {
                    partSize(minPartSize)
                } else if (maxPartCount > 0) {
                    partSize((partSize / maxPartCount).bytes)
                    // 如果依旧超出, 则表示无法满足要求
                    if (partSize > maxPartSize) {
                        throw IllegalArgumentException("无法计算分片. 原始大小: $size, 最大分片数: $maxPartCount, 单分片最小大小: $minPartSize, 单分票最大大小: $maxPartSize")
                    }
                } else {
                    partSize(partSize - (partSize / 2))
                }
                return parts()
            }
            return split(size, partSize)
        }

        fun build(): Multipart {
            require(partSize.bytes > 0) { "分片每片大小至少1Bytes! 当前: $partSize" }
            val parts = parts()
            return Multipart(id, size, partSize, parts)
        }

    }

}
