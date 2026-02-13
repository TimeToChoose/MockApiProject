package com.shineofeidos.mockapiproject.learning

import org.junit.Test

/**
 * 单元测试版：MiniRetrofit
 *
 * 在 Android 项目中，直接运行 main() 函数比较麻烦（需要额外的 Gradle 配置）。
 * 最简单的方式是使用单元测试 (Unit Test) 来运行纯 Java/Kotlin 代码。
 *
 * 点击下方 testMiniRetrofit() 左侧的绿色三角形即可运行。
 */
class MiniRetrofitTest {

    @Test
    fun testMiniRetrofit() {
        println("=== 开始测试 MiniRetrofit (Unit Test) ===")

        // 1. 创建实例 (动态代理)
        val service = MiniRetrofit.create(MiniApiService::class.java)

        // 2. 调用方法
        // 注意：这里调用的是接口方法，但实际上执行的是 MiniRetrofit 中 InvocationHandler 的 invoke 逻辑
        val result = service.getUsers()

        println("=== 调用结果 ===")
        println(result)

        // 简单断言验证
        assert(result.contains("Mock Data"))
    }
}
