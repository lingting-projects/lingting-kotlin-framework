package live.lingting.framework.crypto.pbkdf2

/**
 * @author lingting 2026/3/17 15:43
 */
class Pbkdf2Sha256(salt: ByteArray?) : Pbkdf2(salt) {

    override fun useSalt(k: ByteArray): Pbkdf2 = Pbkdf2Sha256(k).also {
        it.iterations = iterations
        it.keyLength = keyLength
    }

}
