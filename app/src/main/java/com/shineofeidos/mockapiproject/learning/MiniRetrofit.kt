package com.shineofeidos.mockapiproject.learning

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * =========================================================================================
 * 核心原理实战：手写一个迷你版 Retrofit
 * =========================================================================================
 *
 * 面试官常问：
 * "Retrofit 是如何将一个 Interface 变成可以调用的对象的？"
 * 答案：Java 动态代理 (Dynamic Proxy)。
 *
 * 下面我们用最简单的代码还原这个过程。
 */

// 1. 定义注解 (模拟 Retrofit 的 @GET)
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class GET(val value: String)

// 2. 定义接口 (模拟我们的 ApiService)
interface MiniApiService {
    @GET("/users")
    fun getUsers(): String // 为了简化，我们直接返回 String
}

// 3. 核心实现 (模拟 Retrofit.create)
object MiniRetrofit {
    @Suppress("UNCHECKED_CAST")
    fun <T> create(service: Class<T>): T {
        // Proxy.newProxyInstance 是 JDK 提供的动态代理机制
        // 它能在运行时动态生成一个实现了 service 接口的对象
        return Proxy.newProxyInstance(
            service.classLoader,
            arrayOf(service),
            object : InvocationHandler {
                override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any {
                    // === 当你调用接口方法时，代码会走到这里 ===
                    
                    println(">> [MiniRetrofit] 拦截到了方法调用: ${method.name}")

                    // 1. 解析注解 (Reflection)
                    val getAnnotation = method.getAnnotation(GET::class.java)
                    if (getAnnotation != null) {
                        val url = getAnnotation.value
                        println(">> [MiniRetrofit] 解析到 GET 注解，URL: $url")
                        
                        // 2. 模拟网络请求 (OkHttp 的工作)
                        println(">> [MiniRetrofit] 正使用 OkHttp 发起请求...")
                        return "{ \"code\": 200, \"data\": \"Mock Data\" }"
                    }

                    return "Unknown Method"
                }
            }
        ) as T
    }
}

// 4. 运行测试
fun main() {
    println("=== 开始测试 MiniRetrofit ===")
    
    // 创建实例 (这里并没有写实现类，而是由动态代理生成)
    val service = MiniRetrofit.create(MiniApiService::class.java)
    
    // 调用方法
    val result = service.getUsers()
    
    println("=== 调用结果 ===")
    println(result)
}
