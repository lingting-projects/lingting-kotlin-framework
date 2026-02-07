package live.lingting.kotlin.framework.value

import live.lingting.kotlin.framework.util.ArrayUtils
import live.lingting.kotlin.framework.value.multi.ListMultiValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * @author lingting 2026/2/6 17:49
 */
class MultiValueTest {

    @Test
    fun list() {
        val value = ListMultiValue<Int, Int>()
        assertTrue(value.isEmpty())
        assertNull(value.first(1))
        assertFalse(value.isEmpty())
        assertTrue(value.isEmpty(1))
        value.add(1, 1)
        assertFalse(value.isEmpty(1))
        value.clear()
        assertTrue(value.isEmpty(1))
        value.add(1, 1)
        value.add(1, 2)
        value.add(1, 3)
        assertFalse(value.isEmpty())
        assertEquals(1, value.keys().size)
        assertEquals(3, value.get(1).size)
        assertTrue(ArrayUtils.arrayEquals(arrayOf(1, 2, 3), value[1].sorted().toTypedArray()))
        assertEquals(1, value.first(1))
        assertTrue(value.remove(1, 1))
        assertFalse(value.remove(2, 1))
        val r2 = value.remove(2)
        assertTrue(r2.isNullOrEmpty())
        val unmodifiable = value.unmodifiable()

        assertFailsWith<UnsupportedOperationException> { unmodifiable.add(1, 4) }
        assertFailsWith<UnsupportedOperationException> { unmodifiable.clear() }
        assertFailsWith<UnsupportedOperationException> { unmodifiable.ifAbsent(2) }
        assertFailsWith<UnsupportedOperationException> { unmodifiable.remove(2) }
        assertFailsWith<UnsupportedOperationException> { unmodifiable.remove(1, 1) }
        assertNotNull(unmodifiable[2])
        assertEquals(2, unmodifiable.first(1))
        assertNull(unmodifiable.first(2))
        assertFalse(unmodifiable.isEmpty())
        assertFalse(unmodifiable.isEmpty(1))
        assertTrue(unmodifiable.isEmpty(2))
        assertNotNull(unmodifiable.iterator(1))
        assertNotNull(unmodifiable.iterator(2))
    }

}
