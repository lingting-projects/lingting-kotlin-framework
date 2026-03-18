package live.lingting.framework.crypto.pbkdf2

/**
 * @author lingting 2026/3/17 15:43
 */
class Pbkdf2Sha1(salt: ByteArray) : Pbkdf2(salt, Type.SHA1) {

    constructor(salt: String) : this(salt.encodeToByteArray())

}
