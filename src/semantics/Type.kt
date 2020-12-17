package semantics

import parser.Expr_Identifier

val OBJECT = Type("Object", null)
val SELF_TYPE = Type("SELF_TYPE")
//val STRING = Type("String")
val IO = Type("IO")
class Type(val type: String, val parent: Type? = OBJECT) {
    private val attrs = mutableMapOf<String, Type>()
    fun isSubType(target: Type):Boolean {
        return target == parent || parent?.isSubTypeOrSelf(target) ?: false
    }
    fun isSubTypeOrSelf(target: Type): Boolean {
        return target == this || target == parent || parent?.isSubType(target) ?: false
    }
    fun addAttr(identifier: String, type: Type) {
        attrs[identifier]?.let {
            throw Exception("conflict args in class $type, $identifier")
        }
        attrs[identifier] = type
    }
    fun find(identifier: String): Type? {
        return attrs[identifier]
    }
}

fun String.toType() = TypeTable.getType(this)
