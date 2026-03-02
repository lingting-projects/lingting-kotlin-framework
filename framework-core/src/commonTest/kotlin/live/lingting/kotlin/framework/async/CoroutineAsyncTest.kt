package live.lingting.kotlin.framework.async

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CoroutineAsyncTest {

    val activeTasks = atomic(0)
    val maxParallel = atomic(0)
    val finishedTasks = atomic(0)

    @Test
    fun `测试并发限流是否生效`() = runTest {
        // 限制并发数为 3
        val limit = 3L
        val async = CoroutineAsync(limit = limit, scope = this)

        // 提交 10 个任务，每个耗时 50ms
        repeat(10) {
            async.submit {
                val current = activeTasks.incrementAndGet()
                // 更新观察到的最高并发数
                if (current > maxParallel.value) {
                    maxParallel.value = current
                }

                delay(50)

                activeTasks.decrementAndGet()
                finishedTasks.incrementAndGet()
            }
        }

        // 等待所有任务完成（调用你定义的接口方法）
        async.awaitIdle()

        // 断言验证
        assertEquals(10, finishedTasks.value, "所有任务都应执行完毕")
        assertEquals(limit.toInt(), maxParallel.value, "最大并行度应严格等于 limit")
        assertEquals(0, async.running, "结束后运行中计数应为 0")
        assertEquals(10, async.completed, "已完成计数应为 10")
    }

    @Test
    fun `测试任务异常后的隔离性`() = runTest {
        val async = CoroutineAsync(limit = 1, scope = this)
        var nextTaskRun = false

        // 提交一个会抛异常的任务
        async.submit {
            throw IllegalStateException("Task Failed")
        }

        // 提交第二个正常任务
        async.submit {
            nextTaskRun = true
        }

        async.awaitIdle()

        assertTrue(nextTaskRun, "前一个任务异常不应影响后续任务调度")
        assertEquals(2, async.completed, "异常的任务也应计入已完成(FAIL)")
    }

    @Test
    fun `测试取消全部功能`() = runTest {
        // 限制为 1，确保后续任务在 waitQueue 中排队
        val async = CoroutineAsync(limit = 1, scope = this)
        val executedIds = mutableListOf<Int>()

        // 任务 1: 立即运行
        async.submit {
            delay(100)
            executedIds.add(1)
        }

        // 任务 2: 在队列中等待
        async.submit {
            executedIds.add(2)
        }

        // 执行取消
        async.cancelAll()

        // 等待空闲
        async.awaitIdle()

        // 验证
        assertTrue(executedIds.contains(1), "已经在运行的任务会执行完成")
        assertFalse(executedIds.contains(2), "在队列中等待的任务应被拦截不执行")
        assertEquals(2, async.completed, "所有任务状态应已更新（SUCCESS 或 ABORT）")
    }

}
