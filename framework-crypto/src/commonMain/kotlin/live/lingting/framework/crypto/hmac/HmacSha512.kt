package live.lingting.framework.crypto.hmac

/**
 * @author lingting 2026/2/4 19:49
 */
class HmacSha512(key: ByteArray) : Hmac(key, Type.SHA512) {

    constructor(key: String) : this(key.encodeToByteArray())

}
