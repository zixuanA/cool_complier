package semantics

val SELF_TYPE = Type("SELF_TYPE")
data class Type(val type: String)

fun String.toType() = Type(this)//todo 现在的实现有问题，会拿到没有的type
fun Type.isSubType(target: Type) = Environment.subType(this,target)
