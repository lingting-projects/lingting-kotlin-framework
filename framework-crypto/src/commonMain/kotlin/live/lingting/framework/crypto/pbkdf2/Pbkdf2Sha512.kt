package live.lingting.framework.crypto.pbkdf2

/**
 * @author lingting 2026/3/17 15:43
 */
class Pbkdf2Sha512(salt: ByteArray) : Pbkdf2(salt, Type.SHA512) {

    constructor(salt: String) : this(salt.encodeToByteArray())

}
