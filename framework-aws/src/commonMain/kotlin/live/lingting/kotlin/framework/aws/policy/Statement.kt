package live.lingting.kotlin.framework.aws.policy

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * @author lingting 2024-09-12 20:31
 */
@Serializable
open class Statement @JvmOverloads constructor(
    @SerialName("Effect")
    protected val effect: String,
    @SerialName("Action")
    protected val actions: LinkedHashSet<String> = LinkedHashSet(),
    @SerialName("Resource")
    protected val resources: LinkedHashSet<String> = LinkedHashSet(),
    @SerialName("Condition")
    protected val conditions: LinkedHashMap<String, LinkedHashMap<String, LinkedHashSet<String>>> = LinkedHashMap(),
) {

    companion object {

        @JvmStatic
        fun allow(): Statement {
            return Statement(true)
        }

        @JvmStatic
        fun deny(): Statement {
            return Statement(false)
        }

    }

    constructor(allow: Boolean) : this(if (allow) "Allow" else "Deny")

    open fun addAction(action: String) {
        actions.add(action)
    }

    open fun addAction(vararg actions: String) {
        addAction(actions.toList())
    }

    open fun addAction(actions: Collection<String>) {
        for (action in actions) {
            addAction(action)
        }
    }

    open fun addResource(resource: String) {
        resources.add(resource)
    }

    open fun addResource(vararg resources: String) {
        addResource(resources.toList())
    }

    open fun addResource(resources: Collection<String>) {
        for (resource in resources) {
            addResource(resource)
        }
    }

    open fun putCondition(operator: String, value: Map<String, Collection<String>>) {
        val map = LinkedHashMap<String, LinkedHashSet<String>>()
        for ((key, value1) in value) {
            map[key] = LinkedHashSet(value1)
        }
        conditions[operator] = map
    }

    open fun map(): MutableMap<String, Any> {
        val map: MutableMap<String, Any> = HashMap(4)
        map["Effect"] = effect
        map["Action"] = LinkedHashSet(actions)
        map["Resource"] = LinkedHashSet(resources)
        if (conditions.isNotEmpty()) {
            map["Condition"] = conditions
        }
        return map
    }

}
