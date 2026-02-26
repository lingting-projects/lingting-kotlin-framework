package live.lingting.kotlin.framework.concurrent

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import live.lingting.kotlin.framework.exception.TimeoutException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * @author Gemini
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AwaitTest {

    @Test
    fun testWaitTrue() = runTest {
        var count = 0
        // 模拟第3次 poll 成功
        Await.waitTrue(timeout = 1.seconds) {
            count++
            count >= 3
        }
        assertEquals(3, count, "应该在尝试3次后成功")
    }

    @Test
    fun testWaitNotNull() = runTest {
        var data: String? = null

        // 异步在 200ms 后赋值
        launch {
            delay(200.milliseconds)
            data = "Hello Gemini"
        }

        val result = Await.waitNotNull(timeout = 1.seconds) {
            data
        }

        assertEquals("Hello Gemini", result)
    }

    @Test
    fun testTimeoutException() = runTest {
        // 验证超时抛出指定的自定义异常
        assertFailsWith<TimeoutException> {
            Await.waitTrue(timeout = 100.milliseconds) {
                false // 永远不满足条件
            }
        }
    }

    @Test
    fun testWaitFalse() = runTest {
        var stop = true
        launch {
            delay(100.milliseconds)
            stop = false
        }

        Await.waitFalse(timeout = 500.milliseconds) {
            stop
        }
        assertEquals(false, stop)
    }

    @Test
    fun testCustomPredicate() = runTest {
        val result = Await.wait<Int>(
            timeout = 1.seconds,
            predicate = { it != null && it > 10 }
        ) {
            11
        }
        assertEquals(11, result)
    }

    @Test
    fun testWorkerExceptionPropagation() = runTest {
        // 验证 worker 内部抛出的异常能够正常向上透传，而不是被 Awaiter 吞掉
        assertFailsWith<IllegalStateException> {
            Await.waitNotNull(timeout = 1.seconds) {
                throw IllegalStateException("Worker crashed")
            }
        }
    }
}
