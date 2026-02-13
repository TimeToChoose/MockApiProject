package com.shineofeidos.mockapiproject.learning

import org.junit.Test

class ServiceMethodCacheTest {

    @Test
    fun testCachePerformance() {
        val method = CacheTestApi::class.java.methods.find { it.name == "getUser" }!!

        println("=== 第一次调用 (冷启动) ===")
        val start1 = System.currentTimeMillis()
        RetrofitCacheSystem.loadServiceMethod(method)
        val time1 = System.currentTimeMillis() - start1
        println("耗时: ${time1}ms")

        println("\n=== 第二次调用 (命中缓存) ===")
        val start2 = System.currentTimeMillis()
        RetrofitCacheSystem.loadServiceMethod(method)
        val time2 = System.currentTimeMillis() - start2
        println("耗时: ${time2}ms")

        println("\n=== 第三次调用 (命中缓存) ===")
        val start3 = System.currentTimeMillis()
        RetrofitCacheSystem.loadServiceMethod(method)
        val time3 = System.currentTimeMillis() - start3
        println("耗时: ${time3}ms")

        // 简单的性能验证
        assert(time1 >= 100) // 第一次应该包含耗时操作
        assert(time2 < 10)   // 第二次应该极快
        assert(time3 < 10)   // 第三次
    }
}
