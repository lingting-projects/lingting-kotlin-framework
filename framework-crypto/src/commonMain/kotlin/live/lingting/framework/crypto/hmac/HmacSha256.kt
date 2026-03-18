package live.lingting.framework.crypto.hmac

/**
 * @author lingting 2026/2/4 19:49
 */
class HmacSha256(key: ByteArray) : Hmac(key, Type.SHA256) {

    constructor(key: String) : this(key.encodeToByteArray())

}
