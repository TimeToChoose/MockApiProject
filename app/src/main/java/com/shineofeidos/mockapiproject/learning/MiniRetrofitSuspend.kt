package com.shineofeidos.mockapiproject.learning

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.resume

/**
 * =========================================================================================
 * 进阶原理实战：Retrofit 是如何支持 suspend 函数的？
 * =========================================================================================
 *
 * 核心揭秘：
 * Kotlin 的 suspend 函数在编译成字节码时，会发生 "CPS 变换" (Continuation-Passing Style)。
 *
 * 你的代码：
 * suspend fun getUsers(): String
 *
 * 编译后的 Java 字节码：
 * Object getUsers(Continuation<String> continuation)
 *
 * 所以，当我们在动态代理的 invoke 方法中拦截时，args 数组的最后一个参数就是 Continuation！
 */

// 1. 定义一个带 suspend 方法的接口
interface SuspendApiService {
    @GET("/async-users")
    suspend fun getUsers(): String
}

object MiniRetrofitSuspend {
    @Suppress("UNCHECKED_CAST")
    fun <T> create(service: Class<T>): T {
        return Proxy.newProxyInstance(
            service.classLoader,
            arrayOf(service),
            object : InvocationHandler {
                override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any {
                    // 1. 检查是否是 suspend 函数
                    // 只要看最后一个参数是不是 Continuation 类型
                    val lastArg = args?.lastOrNull()
                    if (lastArg is Continuation<*>) {
                        println(">> [MiniRetrofit] 发现 suspend 函数！")
                        
                        // 2. 强转为 Continuation
                        val continuation = lastArg as Continuation<String>

                        // 3. 模拟异步网络请求
                        println(">> [MiniRetrofit] 开启子线程模拟网络耗时...")
                        Thread {
                            try {
                                Thread.sleep(1000) // 模拟网络延迟
                                println(">> [MiniRetrofit] 网络请求完成，准备恢复协程")
                                
                                // 4. 关键点：手动恢复协程！
                                // 这相当于告诉 Kotlin："你的数据准备好了，继续往下执行吧"
                                continuation.resume("{ \"code\": 200, \"data\": \"Async Mock Data\" }")
                            } catch (e: Exception) {
                                continuation.resumeWith(Result.failure(e))
                            }
                        }.start()

                        // 5. 必须返回 COROUTINE_SUSPENDED
                        // 这告诉调用方："我还没算完，你先挂起（暂停）吧"
                        return COROUTINE_SUSPENDED
                    }

                    // 普通方法的处理逻辑...
                    return "Sync Result"
                }
            }
        ) as T
    }
}
