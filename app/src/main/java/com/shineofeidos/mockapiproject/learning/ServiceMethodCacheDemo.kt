package com.shineofeidos.mockapiproject.learning

import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

/**
 * =========================================================================================
 * 源码深度解析：Retrofit 的缓存机制 (ServiceMethod Cache)
 * =========================================================================================
 *
 * 你贴出的源码中，最关键的一句是：
 * loadServiceMethod(method).invoke(args);
 *
 * 为什么不直接解析注解然后请求？因为反射解析注解是非常耗时的！
 * 如果每次调用 API 都要重新遍历一遍注解，APP 界面可能会卡顿。
 *
 * Retrofit 的解决方案：
 * 使用一个 ConcurrentHashMap 缓存已经解析过的方法。
 * 下次再调用同一个 API 时，直接从 Map 里取，速度提升 100 倍以上。
 */

// 1. 模拟一个复杂的解析过程 (解析注解、查找 Adapter 等)
fun parseMethodAnnotations(method: Method): String {
    // 模拟耗时操作 (比如遍历几百个注解，查找 Converter)
    Thread.sleep(100) 
    return "Parsed: ${method.name}"
}

object RetrofitCacheSystem {
    // 这就是源码中的 serviceMethodCache
    private val serviceMethodCache = ConcurrentHashMap<Method, String>()

    fun loadServiceMethod(method: Method): String {
        // 1. 先尝试从缓存拿
        var result = serviceMethodCache.get(method)
        if (result != null) {
            return result
        }

        // 2. 双重检查锁 (Double Check Lock) 保证线程安全
        synchronized(serviceMethodCache) {
            result = serviceMethodCache.get(method)
            if (result == null) {
                println(">> [CacheSystem] 首次调用，开始耗时解析: ${method.name}")
                // 3. 解析并存入缓存
                result = parseMethodAnnotations(method)
                serviceMethodCache[method] = result!!
            }
        }
        return result!!
    }
}

// 模拟的接口
interface CacheTestApi {
    fun getUser()
    fun getPosts()
}

// 运行测试
fun main() {
    val method = CacheTestApi::class.java.methods.find { it.name == "getUser" }!!

    println("=== 第一次调用 (冷启动) ===")
    val start1 = System.currentTimeMillis()
    RetrofitCacheSystem.loadServiceMethod(method)
    println("耗时: ${System.currentTimeMillis() - start1}ms")

    println("\n=== 第二次调用 (命中缓存) ===")
    val start2 = System.currentTimeMillis()
    RetrofitCacheSystem.loadServiceMethod(method)
    println("耗时: ${System.currentTimeMillis() - start2}ms")
    
    println("\n=== 第三次调用 (命中缓存) ===")
    val start3 = System.currentTimeMillis()
    RetrofitCacheSystem.loadServiceMethod(method)
    println("耗时: ${System.currentTimeMillis() - start3}ms")
}
