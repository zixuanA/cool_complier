package semantics

val OBJECT = Type("Object", null)
val SELF_TYPE = Type("SELF_TYPE")
class Type(val type: String, val parent: Type? = OBJECT) {
    fun isSubType(target: Type):Boolean {
        return target == parent || parent?.isSubType(target) ?: false
    }
}

fun String.toType() = TypeTable.getType(this)
