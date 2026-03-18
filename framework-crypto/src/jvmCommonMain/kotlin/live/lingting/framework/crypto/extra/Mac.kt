package live.lingting.framework.crypto.extra

import javax.crypto.Mac
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * @author lingting 2026/3/17 16:40
 */
fun mac(algorithm: String, key: SecretKeySpec? = null, iv: IvParameterSpec? = null): Mac {
    val i = Mac.getInstance(algorithm)
    if (key != null && iv != null) {
        i.init(key, iv)
    } else if (key != null) {
        i.init(key)
    }
    return i
}

fun mac(algorithm: String, key: ByteArray? = null, iv: ByteArray? = null): Mac {
    return mac(algorithm, key?.let { SecretKeySpec(key, algorithm) }, iv?.let { IvParameterSpec(iv) })
}

fun mac(algorithm: String, key: String? = null, iv: String? = null): Mac {
    return mac(algorithm, key?.toByteArray(), iv?.toByteArray())
}
