package parser

abstract class ASTNode {
    val children = mutableListOf<ASTNode>()
}

class Program(val classes: List<Class>) : ASTNode() {

}

class Class(val type: String, val inheritsType: String?, val features: List<ASTNode>) : ASTNode() {

}

class Feature(val id: String, val formals: List<ASTNode>?, val returnType: String, val expr: ASTNode?) : ASTNode() {

}
//class Feature_2(val id: String,val returnType: String, val expr: ASTNode?): ASTNode(){
//
//}

class Formal(val id: String, val type: String) : ASTNode() {


}


class IntegerNode(var value: Int) : ASTNode() {

}

class StringNode(var value: String) : ASTNode() {
}

class BooleanNode(var value: Boolean) : ASTNode() {
}

class IdentifierNode(var value: String) : ASTNode() {

}

class Expr_Assignment(val identifier: String, val child: ASTNode) : ASTNode() {

}

class Expr_FunctionCall(val caller: ASTNode, val type: String?, val id: String, val ops: List<ASTNode>) : ASTNode() {

}

//class Expr_FunctionCall_2(val caller: ASTNode, val id: String, val ops: List<ASTNode>) : ASTNode() {
//
//}

class Expr_If(val condition: ASTNode, val trueBranch: ASTNode, val falseBranch: ASTNode) : ASTNode() {

}

class Expr_While(val condition: ASTNode, val body: ASTNode) : ASTNode() {

}

class Expr_Exprs(val exprs: List<ASTNode>) : ASTNode() {

}

data class AST_Let_Options(val id: String, val type: String, val body: ASTNode?) {

}

class Expr_Let(
    val id: String,
    val type: String,
    val init: ASTNode?,
    val ops: List<AST_Let_Options>,
    val body: ASTNode
) : ASTNode() {

}

data class AST_Case_Options(val id: String, val type: String, val body: ASTNode)
class Expr_Case(val expr: ASTNode, val ops: List<AST_Case_Options>) : ASTNode() {

}

class Expr_New(val type: String) : ASTNode() {

}

class Expr_Isvoid(val expr: ASTNode) : ASTNode() {

}

class Expr_Add(val expr1: ASTNode, val expr2: ASTNode) : ASTNode() {

}

class Expr_Less(val expr1: ASTNode, val expr2: ASTNode) : ASTNode() {

}

class Expr_Multiply(val expr1: ASTNode, val expr2: ASTNode) : ASTNode() {

}

class Expr_Division(val expr1: ASTNode, val expr2: ASTNode) : ASTNode() {

}

//补码 ~
class Expr_Complem(val expr: ASTNode) : ASTNode() {

}

class Expr_LessThan(val expr1: ASTNode, val expr2: ASTNode) : ASTNode() {

}

class Expr_LessOrEquals(val expr1: ASTNode, val expr2: ASTNode) : ASTNode() {

}

class Expr_Equals(val expr1: ASTNode, val expr2: ASTNode) : ASTNode() {

}

class Expr_Not(val expr: ASTNode) : ASTNode() {

}

class Expr_Brackets(val expr: ASTNode) : ASTNode() {

}
