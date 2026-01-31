package live.lingting.kotlin.framework.value

import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import live.lingting.kotlin.framework.exception.TimeoutException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * @author lingting 2026/1/30 17:19
 */
class WaitValueTest {

    @Test
    fun `test initial value`() {
        val wv = WaitValue.of("init")
        assertEquals("init", wv.value)
    }

    @Test
    fun `test await notNull success`() = runTest {
        val wv = WaitValue<String>()

        // 模拟后台异步赋值
        launch {
            delay(100.milliseconds)
            wv.value = "resolved"
        }

        // 挂起直到拿到非空值
        val result = wv.notNull(1.seconds)
        assertEquals("resolved", result)
    }

    @Test
    fun `test await timeout`() = runTest {
        val wv = WaitValue<String>()

        // 期待在 50ms 后抛出 TimeoutException
        assertFailsWith<TimeoutException> {
            wv.notNull(50.milliseconds)
        }
    }

    @Test
    fun `test predicate await`() = runTest {
        val wv = WaitValue(1)

        launch {
            delay(50.milliseconds)
            wv.value = 5
            delay(50.milliseconds)
            wv.value = 10 // 这个值才会触发下面的 predicate
        }

        // 等待直到值大于 8
        val result = wv.await(500.milliseconds) {
            (it ?: 0) > 8
        }
        assertEquals(10, result)
    }

    @Test
    fun `test compute concurrency`() = runTest {
        val wv = WaitValue(0)

        // 同时开启 10 个协程进行累加
        val jobs = List(10) {
            launch {
                wv.compute { (it ?: 0) + 1 }
            }
        }

        jobs.joinAll()
        assertEquals(10, wv.value)
    }

    @Test
    fun `test replay mechanism`() = runTest {
        val wv = WaitValue.of("immediate")

        // 即使没有 delay，因为 SharedFlow replay=1，也能立即拿到当前值
        val result = wv.notNull(1.seconds)
        assertEquals("immediate", result)
    }

}
