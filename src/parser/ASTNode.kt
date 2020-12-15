package parser

import FunctionTable
import TypeTable
import semantics.*

abstract class ASTNode {
    abstract fun typeCheck(): Type?
    open fun codeGen() {}

    fun wrongType(): Type {
        throw Exception("Wrong type")
    }
}


class Program(val classes: List<Class>) : ASTNode() {
    override fun typeCheck(): Type? {
        classes.forEach {
            it.typeCheck()
        }
        return null
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

    fun init() {
        val list = classes.toMutableList()
        var lastSize = list.size + 1

        while (list.isNotEmpty()) {
            if (lastSize == list.size) {
                throw Exception("illegal class define")
            }
            lastSize = list.size
            for (i in list.size - 1 downTo 0) {
                list[i].apply {
                    try {
                        val parent = list[i].inheritsType!!.toType()
                        TypeTable.addType(Type(type, parent))
                        list.removeAt(i)
                    } catch (e: ClassNotFoundException) {

                    } catch (e: NullPointerException) {
                        val parent = OBJECT
                        TypeTable.addType(Type(type, parent))
                        list.removeAt(i)
                    }
                }

            }

        }
        classes.forEach {
            it.init()
        }
    }
}

class Class(val type: String, val inheritsType: String?, val features: List<Feature>) : ASTNode() {
    override fun typeCheck(): Type? {
        Environment.enterClass(type.toType())
        features.filterIsInstance<Feature_Attributes>().forEach { attr ->
            Environment.add(attr.id, attr.type.toType())
        }
        Environment.add("self", SELF_TYPE)
        features.forEach {

            it.typeCheck()

        }
        return null
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

    fun init() {
        features.filterIsInstance<Feature_Function>().forEach {
            FunctionTable.addFunction(
                type.toType(),
                it.id,
                it.formals.map { formal -> formal.type.toType() },
                it.returnType.toType()
            )
        }
    }
}

abstract class Feature() : ASTNode()
class Feature_Function(val id: String, val formals: List<Formal>, val returnType: String, val expr: ASTNode) :
    Feature() {
    override fun typeCheck(): Type? {

        val resultType =
            Environment.getFunctionResult(Environment.currentClassType(), id, formals.map { it.type.toType() })
        if (resultType != returnType.toType()) throw Exception("return type ")
        Environment.enterScope()
        Environment.add("self", Environment.currentClassType())
        formals.forEach {
            Environment.add(it.id, it.type.toType())
        }
        val t = expr.typeCheck() ?: wrongType()
        if (returnType.toType() == SELF_TYPE) {
            if (!t.isSubTypeOrSelf(Environment.currentClassType())) wrongType()
        } else {
            if (!t.isSubTypeOrSelf(returnType.toType())) wrongType()
        }
        Environment.exitScope()

        return resultType
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}

class Feature_Attributes(val id: String, val type: String, val init: ASTNode?) : Feature() {
    override fun typeCheck(): Type? {
        type.toType().let {

            val initType = init?.typeCheck()


            return if (initType == null) {
                null
            } else {
                if (initType.isSubTypeOrSelf(Type(type))) null
                else wrongType()
            }
        }

    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}
//class Feature_2(val id: String,val returnType: String, val expr: ASTNode?): ASTNode(){
//
//}

class Formal(val id: String, val type: String) : ASTNode() {
    override fun typeCheck(): Type? {
        return null
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }


}


abstract class Expr() : ASTNode()
class Expr_Assignment(val identifier: String, val child: ASTNode) : Expr() {
    override fun typeCheck(): Type? {

        val t = Environment.findSymbol(identifier)
        child.typeCheck()?.let {
            return if (it.isSubTypeOrSelf(t)) t else wrongType()
        }
        return wrongType()
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}

class Expr_StaticFunctionCall(val caller: ASTNode, val type: String, val id: String, val exprs: List<Expr>) : Expr() {
    override fun typeCheck(): Type? {
        caller.typeCheck()?.let { t0 ->
            if (!t0.isSubTypeOrSelf(type.toType())) wrongType()

            val t = Environment.getFunctionResult(type.toType(), id, exprs.map { it.typeCheck()!! })

            if (t == SELF_TYPE) {
                return t0
            } else {
                t
            }
        }
        return wrongType()
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}

class Expr_FunctionCall(val caller: ASTNode, val id: String, val exprs: List<Expr>) : Expr() {
    override fun typeCheck(): Type? {
        caller.typeCheck()?.let { t0 ->
            val callerType = if (t0 == SELF_TYPE) Environment.currentClassType() else t0
            val t = Environment.getFunctionResult(callerType, id, exprs.map { it.typeCheck()!! })

            return if (t == SELF_TYPE) {
                t0
            } else {
                t
            }
        }
        return wrongType()
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}

class Expr_Local_FunctionCall(val id: String, val ops: List<Expr>) : Expr() {
    override fun typeCheck(): Type? {
        Environment.currentClassType().let { t0 ->
            val types = mutableListOf<Type>()
            ops.forEach {
                types.add(it.typeCheck()!!)
            }
            val t = Environment.getFunctionResult(t0, id, types)

            return if (t == SELF_TYPE) {
                t0
            } else {
                t
            }
        }
    }

    override fun codeGen() {
    }
}
//class Expr_FunctionCall_2(val caller: ASTNode, val id: String, val ops: List<ASTNode>) : Expr() {
//
//}

class Expr_If(val condition: ASTNode, val trueBranch: ASTNode, val falseBranch: ASTNode) : Expr() {
    override fun typeCheck(): Type? {
        val t1 = condition.typeCheck()
        val t2 = trueBranch.typeCheck() ?: wrongType()
        val t3 = falseBranch.typeCheck() ?: wrongType()
        return TypeTable.lub(t2, t3)
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}

class Expr_While(val condition: ASTNode, val body: ASTNode) : Expr() {
    override fun typeCheck(): Type? {
        condition.typeCheck()?.let { e1 ->
            body.typeCheck()?.let { e2 ->
                if (e1 == "Bool".toType()) return "Object".toType()
            }
        }
        return wrongType()
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}

class Expr_Exprs(val exprs: MutableList<ASTNode>) : Expr() {
    override fun typeCheck(): Type? {
        var result: Type? = null
        exprs.forEach {
            result = it.typeCheck()
        }
        return result
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}

data class AST_Let_Options(val id: String, val type: String, val body: ASTNode?) {

}

class Expr_Let(
    val id: String,
    val type: String,
    val init: ASTNode?,
    val ops: List<AST_Let_Options>,
    val body: ASTNode
) : Expr() {
    override fun typeCheck(): Type? {
        var result: Type? = null
        val t = if (type.toType() == SELF_TYPE) Environment.currentClassType() else type.toType()
        if (init == null || init.typeCheck()?.isSubTypeOrSelf(t) == true) {
            Environment.enterScope()
            Environment.add(id, t)
            ops.forEach {
                val t = if (it.type.toType() == SELF_TYPE) Environment.currentClassType() else it.type.toType()
                if(it.body == null || it.body.typeCheck()?.isSubTypeOrSelf(t) == true) {
                    Environment.add(it.id, t)
                }
            }
            result = body.typeCheck()
            Environment.exitScope()
            return result
        }
        return wrongType()
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}

data class AST_Case_Options(val id: String, val type: String, val body: ASTNode)
class Expr_Case(val expr: ASTNode, val ops: List<AST_Case_Options>) : Expr() {
    override fun typeCheck(): Type? {
        expr.typeCheck()?.let { t ->
            var result: Type? = null
            ops.forEach {
                Environment.enterScope()
                Environment.add(it.id, it.type.toType())
                it.body.typeCheck()?.let {
                    result = if (result == null) it else TypeTable.lub(it, result!!)
                }
                Environment.exitScope()
            }
            return result
        }
        return wrongType()
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}

class Expr_New(val type: String) : Expr() {
    override fun typeCheck(): Type? {
        return type.toType()
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}

class Expr_Isvoid(val expr: ASTNode) : Expr() {
    override fun typeCheck(): Type? {
        expr.typeCheck()
        return "Bool".toType()
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}

class Expr_Add(val expr1: ASTNode, val expr2: ASTNode) : Expr() {
    override fun typeCheck(): Type? {
        return if (expr1.typeCheck() == "Int".toType() && expr2.typeCheck() == "Int".toType()) "Int".toType()
        else wrongType()
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}

class Expr_Less(val expr1: ASTNode, val expr2: ASTNode) : Expr() {
    override fun typeCheck(): Type? {
        return if (expr1.typeCheck() == "Int".toType() && expr2.typeCheck() == "Int".toType()) "Int".toType()
        else wrongType()
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}

class Expr_Multiply(val expr1: ASTNode, val expr2: ASTNode) : Expr() {
    override fun typeCheck(): Type? {
        return if (expr1.typeCheck() == "Int".toType() && expr2.typeCheck() == "Int".toType()) "Int".toType()
        else wrongType()
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}

class Expr_Division(val expr1: ASTNode, val expr2: ASTNode) : Expr() {
    override fun typeCheck(): Type? {
        return if (expr1.typeCheck() == "Int".toType() && expr2.typeCheck() == "Int".toType()) "Int".toType()
        else wrongType()
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}

//补码 ~
class Expr_Neg(val expr: ASTNode) : Expr() {
    override fun typeCheck(): Type? {
        expr.typeCheck().let {
            return if (it == "Int".toType()) it
            else wrongType()
        }
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}

class Expr_LessThan(val expr1: ASTNode, val expr2: ASTNode) : Expr() {
    override fun typeCheck(): Type? {
        return if (expr1.typeCheck() == "Int".toType() && expr2.typeCheck() == "Int".toType()) {
            "Bool".toType()
        } else {
            wrongType()
        }
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}

class Expr_LessOrEquals(val expr1: ASTNode, val expr2: ASTNode) : Expr() {
    override fun typeCheck(): Type? {
        return if (expr1.typeCheck() == "Int".toType() && expr2.typeCheck() == "Int".toType()) {
            "Bool".toType()
        } else {
            wrongType()
        }
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}

class Expr_Equals(val expr1: ASTNode, val expr2: ASTNode) : Expr() {
    override fun typeCheck(): Type? {
        val e1 = expr1.typeCheck()
        val e2 = expr2.typeCheck()
        return if (e1 == e2 && (e1 == "Int".toType() || e1 == "String".toType() || e1 == "Bool".toType())) {
            "Bool".toType()
        } else {
            wrongType()
        }
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}

class Expr_Not(val expr: ASTNode) : Expr() {
    override fun typeCheck(): Type? {
        return if (expr.typeCheck() == "Bool".toType()) "Bool".toType() else wrongType()
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}

//()
//class Expr_Brackets(val expr: ASTNode) : Expr() {
//    override fun typeCheck(): Type? {
//    }
//
//    override fun codeGen() {
//    }
//
//}


class Expr_Integer(var value: Int) : Expr() {
    override fun typeCheck(): Type? {
        return "Int".toType()
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}

class Expr_String(var value: String) : Expr() {
    override fun typeCheck(): Type? {
        return "String".toType()
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }
}

class Expr_Boolean(var value: Boolean) : Expr() {
    override fun typeCheck(): Type? {
        return "Bool".toType()
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }
}

class Expr_Identifier(var value: String) : Expr() {
    override fun typeCheck(): Type? {
        return Environment.findSymbol(value)
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}