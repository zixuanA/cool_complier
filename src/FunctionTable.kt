import semantics.Type
import semantics.toType


object FunctionTable {
    val functions = mutableMapOf<Type, MutableMap<String, MutableMap<Int, MutableList<Function>>>>()
    fun addFunction(type: Type, name: String, types: List<Type>, resultType: Type) {
        addFunction(Function(type, name, types, resultType))
    }

    fun addFunction(function: Function) {
        getDirectFunction(function.type, function.name, function.types)?.let {
            throw Exception("function declaration clash in class ${function.type} name: ${function.name} args: ${function.types.forEach { it.type }}")
        }
        function.apply {
            if (functions[type] == null) {
                functions[type] = mutableMapOf()
            }
            if (functions[type]!![name] == null) {
                functions[type]!![name] = mutableMapOf()

            }
            if (functions[type]!![name]!![types.size] == null) {
                functions[type]!![name]!![types.size] = mutableListOf()
            }
            functions[type]!![name]!![types.size]!!.add(function)
        }

    }

    private fun getDirectFunction(type: Type, name: String, types: List<Type>): Function? {
        functions[type]?.let {
            it[name]?.let {
                it[types.size]?.let { functions ->
                    functions.forEach { function ->
                        for (index in types.indices) {
                            if (function.types[index].type.toType() != types[index].type.toType()) break
                            if (index == types.size - 1) {
                                return function
                            }
                        }
                        if (types.isEmpty()) {
                            return function
                        }
                    }
                }
            }
        }
        return null
    }

    private fun getAmbiguousFunction(type: Type, name: String, types: List<Type>): Function? {
        var result: Function? = null
        functions[type]?.let {
            it[name]?.let {
                it[types.size]?.let { functions ->
                    functions.forEach { function ->
                        for (index in types.indices) {
                            if (!types[index].type.toType().isSubTypeOrSelf(function.types[index].type.toType())) break
                            if (index == types.size - 1) {
                                if (result != null) throw Exception("ambiguous functions in class ${type.type}")
                                result = function
                            }
                        }
                    }
                }
            }
        }
        return result
    }

    /*
     * 对于有精确匹配的方法就精确匹配，没有精确匹配的方法才进行模糊匹配，首先找到参数数量一致的，模糊匹配有多个符合条件的项则报错
     * 只有一个则使用这个项
     */
    fun getFunction(type: Type, name: String, types: List<Type>): Function {
        return (getDirectFunction(type, name, types) ?: getAmbiguousFunction(type, name, types))
            ?: type.parent?.let { getFunction(it, name, types) }
            ?: throw NoSuchMethodException("in class ${type.type} function $name")
    }
}

class Function(val type: Type, val name: String, val types: List<Type>, val resultType: Type) {
    val label: String by lazy {
        name.apply {
            types.forEach {
                this.plus(it.type)
            }
        }
    }
}