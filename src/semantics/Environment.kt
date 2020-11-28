package semantics

object Environment {

    fun findSymbol(identifier: String): Type {

    }
    fun getFunctionResultType(type: Type, identifier: String): List<Type> {

    }
    fun add(identifier: String, type: Type){

    }

    fun parent(source: Type, target: Type): Boolean {

    }
    fun subType(source: Type, target: Type): Boolean{

    }
    //返回t1和t2的最小公共父类
    fun lub(t1: Type, t2: Type): Type {

    }
    fun checkScope(identifier: String): Boolean {

    }
    fun enterScope() {

    }

    fun exitScope() {

    }
}