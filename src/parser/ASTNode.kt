package parser

import semantics.Type

abstract class ASTNode {
    val children = mutableListOf<ASTNode>()
    abstract fun typeCheck(): Type?
    abstract fun codeGen()
}


class Program(val classes: List<Class>) : ASTNode() {
    override fun typeCheck(): Type? {
        TODO("Not yet implemented")
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}

class Class(val type: String, val inheritsType: String?, val features: List<ASTNode>) : ASTNode() {
    override fun typeCheck(): Type? {
        TODO("Not yet implemented")
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}

class Feature(val id: String, val formals: List<ASTNode>, val returnType: String, val expr: ASTNode?) : ASTNode() {
    override fun typeCheck(): Type? {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }


}


abstract class Expr():ASTNode()
class Expr_Assignment(val identifier: String, val child: ASTNode) : Expr() {
    override fun typeCheck(): Type? {
        TODO("Not yet implemented")
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}

class Expr_StaticFunctionCall(val caller: ASTNode, val type: String, val id: String, val ops: List<ASTNode>) : Expr() {
    override fun typeCheck(): Type? {
        TODO("Not yet implemented")
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}
class Expr_FunctionCall(val caller: ASTNode, val id: String, val ops: List<ASTNode>) : Expr() {
    override fun typeCheck(): Type? {
        TODO("Not yet implemented")
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}
class Expr_Local_FunctionCall(val id: String, val ops: List<ASTNode>) : Expr(){
    override fun typeCheck(): Type? {
    }

    override fun codeGen() {
    }
}
//class Expr_FunctionCall_2(val caller: ASTNode, val id: String, val ops: List<ASTNode>) : Expr() {
//
//}

class Expr_If(val condition: ASTNode, val trueBranch: ASTNode, val falseBranch: ASTNode) : Expr() {
    override fun typeCheck(): Type? {
        TODO("Not yet implemented")
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}

class Expr_While(val condition: ASTNode, val body: ASTNode) : Expr() {
    override fun typeCheck(): Type? {
        TODO("Not yet implemented")
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}

class Expr_Exprs(val exprs: MutableList<ASTNode>) : Expr() {
    override fun typeCheck(): Type? {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}

data class AST_Case_Options(val id: String, val type: String, val body: ASTNode)
class Expr_Case(val expr: ASTNode, val ops: List<AST_Case_Options>) : Expr() {
    override fun typeCheck(): Type? {
        TODO("Not yet implemented")
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}

class Expr_New(val type: String) : Expr() {
    override fun typeCheck(): Type? {
        TODO("Not yet implemented")
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}

class Expr_Isvoid(val expr: ASTNode) : Expr() {
    override fun typeCheck(): Type? {
        TODO("Not yet implemented")
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}

class Expr_Add(val expr1: ASTNode, val expr2: ASTNode) : Expr() {
    override fun typeCheck(): Type? {
        TODO("Not yet implemented")
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}

class Expr_Less(val expr1: ASTNode, val expr2: ASTNode) : Expr() {
    override fun typeCheck(): Type? {
        TODO("Not yet implemented")
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}

class Expr_Multiply(val expr1: ASTNode, val expr2: ASTNode) : Expr() {
    override fun typeCheck(): Type? {
        TODO("Not yet implemented")
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}

class Expr_Division(val expr1: ASTNode, val expr2: ASTNode) : Expr() {
    override fun typeCheck(): Type? {
        TODO("Not yet implemented")
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}

//补码 ~
class Expr_Complem(val expr: ASTNode) : Expr() {
    override fun typeCheck(): Type? {
        TODO("Not yet implemented")
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}

class Expr_LessThan(val expr1: ASTNode, val expr2: ASTNode) : Expr() {
    override fun typeCheck(): Type? {
        TODO("Not yet implemented")
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}

class Expr_LessOrEquals(val expr1: ASTNode, val expr2: ASTNode) : Expr() {
    override fun typeCheck(): Type? {
        TODO("Not yet implemented")
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}

class Expr_Equals(val expr1: ASTNode, val expr2: ASTNode) : Expr() {
    override fun typeCheck(): Type? {
        TODO("Not yet implemented")
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}

class Expr_Not(val expr: ASTNode) : Expr() {
    override fun typeCheck(): Type? {
        TODO("Not yet implemented")
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}

//()
class Expr_Brackets(val expr: ASTNode) : Expr() {
    override fun typeCheck(): Type? {
        TODO("Not yet implemented")
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}


class Expr_Integer(var value: Int) : Expr() {
    override fun typeCheck(): Type? {
        TODO("Not yet implemented")
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}

class Expr_String(var value: String) : Expr() {
    override fun typeCheck(): Type? {
        TODO("Not yet implemented")
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }
}

class Expr_Boolean(var value: Boolean) : Expr() {
    override fun typeCheck(): Type? {
        TODO("Not yet implemented")
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }
}

class Expr_Identifier(var value: String) : Expr() {
    override fun typeCheck(): Type? {
        TODO("Not yet implemented")
    }

    override fun codeGen() {
        TODO("Not yet implemented")
    }

}