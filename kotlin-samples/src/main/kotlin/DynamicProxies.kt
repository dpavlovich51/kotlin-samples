import java.lang.reflect.Method
import java.lang.reflect.Proxy

fun main(args: Array<String>) {
    val proxyMap = DynamicProxies.newProxy(mutableMapOf<String, Any>())

    proxyMap["1"] = 1
    println(proxyMap["1"])

}

class DynamicProxies {

    companion object {
        fun <T : Map<*, *>> newProxy(target: T): T {
            val methods = getAllMethods(target::class.java, mutableMapOf())

            val newProxyInstance = Proxy.newProxyInstance(
                DynamicProxies::class.java.classLoader,
                arrayOf(Map::class.java)
            ) { _, method, args ->
                println("Yo! This is proxy class. It invokes -> ${method.name}")
                return@newProxyInstance methods[method.name]!!.invoke(target, *args)
            }
            return newProxyInstance as T
        }

        private fun getAllMethods(clazz: Class<out Any>, methods: MutableMap<String, Method>): Map<String, Method> {
            clazz.declaredMethods.forEach { methods[it.name] = it }

            val superclass = clazz.superclass ?: return methods
            return getAllMethods(superclass, methods)
        }
    }
}