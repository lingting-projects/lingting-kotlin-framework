package live.lingting.framework.crypto.pbkdf2

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

/**
 * @author lingting 2026/3/18 10:07
 */
class Pbkdf2Test {

    @Test
    fun test() {
        test(Pbkdf2Sha1("salt"), "pwd", "9f1c955d04240a27a45b733aa8de5ea0c4a4010e28a97ada0fef3ef98aa051bd")
        test(Pbkdf2Sha1("salt2"), "pwd", "114b58e6681eda85e6685668582abd71d06e3584e579e6c65c30b5da1fb604c1")
        test(Pbkdf2Sha256("salt"), "pwd", "86a967fe7c3415a3e12125a88d0a4d01c34d6629a36e3c996aeb6d3fb349d8ee")
        test(Pbkdf2Sha256("salt2"), "pwd", "0913c03c37f3daaf857b46581444a3933fceec54979da2d62bb0f32f8a9b3881")
        test(Pbkdf2Sha512("salt"), "pwd", "b6494095b228c2ce1535d23fe8189b12005d7f7741448a6d6e523cde47105213")
        test(Pbkdf2Sha512("salt2"), "pwd", "00edb4b990e3029bde6cf62c3eeebd10edf68a2bfd86a189bf3831927b318e1e")

        test(
            Pbkdf2Sha1("salt2").apply { iterations = 2000 },
            "pwd",
            "e6e013d0434399c62f41e3042723398209ef4417872eb1c06ed537d474e2387d"
        )
        test(
            Pbkdf2Sha1("salt2").apply { keyLength = 512 },
            "pwd",
            "114b58e6681eda85e6685668582abd71d06e3584e579e6c65c30b5da1fb604c1edc151b53feeb40e510d305ffefa5ac42233952153257b867cc5388be9b39f31"
        )
    }

    fun test(pbkdf2: Pbkdf2, source: String, hex: String) {
        val target = pbkdf2.calculateHex(source)
        assertEquals(target, hex)
        pbkdf2.iterations /= 2
        assertNotEquals(pbkdf2.calculateHex(source), hex)
    }

}
