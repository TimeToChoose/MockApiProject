package com.shineofeidos.mockapiproject.learning

import kotlinx.coroutines.test.runTest
import org.junit.Test

/**
 * 单元测试：测试手写的 suspend 支持
 */
class MiniRetrofitSuspendTest {

    @Test
    fun testSuspendSupport() = runTest {
        println("=== 开始测试 Suspend 支持 ===")

        // 1. 创建实例
        val service = MiniRetrofitSuspend.create(SuspendApiService::class.java)

        println("=== 准备调用 suspend 方法 ===")
        
        // 2. 调用 suspend 方法
        // 此时代码会在这里“暂停” 1秒钟，等待子线程 resume
        val result = service.getUsers()

        println("=== 收到结果 ===")
        println(result)

        assert(result.contains("Async Mock Data"))
    }
}
