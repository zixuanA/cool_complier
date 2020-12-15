import semantics.*

object TypeTable {
    private val types = mutableMapOf<String, Type>()
    init {
        addType(OBJECT)
        addType(SELF_TYPE)
//        addType(STRING)
//        addType(IO)
    }
    fun addType(type: Type) {
        types[type.type] = type
    }

    fun getType(type: String): Type = types[type]?:throw ClassNotFoundException(type)

    //返回t1和t2的最小公共父类
    fun lub(t1: Type, t2: Type): Type? {
        var target1: Type? = t1
        var target2: Type? = t2

        while (target1 != null) {
            while (target2 != null) {
                if (target1 == target2) {
                    return target1
                }


                target2 = target2.parent
            }
            target1 = target1.parent
            target2 = t2
        }
        return null
    }
}