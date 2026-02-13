package com.shineofeidos.mockapiproject.ui.screens

/**
 * 一个模拟的单例管理类，故意不提供解注册方法，导致内存泄漏
 */
object LeakingManager {
    private val listeners = mutableListOf<(String) -> Unit>()
    
    // 为了让泄漏更明显，我们让每个 Listener 都背负一点“内存重量”
    private val heavyData = mutableListOf<ByteArray>()

    fun registerListener(listener: (String) -> Unit) {
        listeners.add(listener)
        // 增加泄漏量：每次泄漏 100MB，且使用更密集的写入确保物理内存分配
        val size = 100 * 1024 * 1024
        val data = ByteArray(size)
        
        // 🔥 关键改进：每 1MB 写入一个字节，确保这 100MB 真正占用物理内存 (PSS)
        // 这样在 Profiler 的 Java 堆内存中会看到非常明显的阶梯式上升
        for (i in 0 until 100) {
            data[i * 1024 * 1024] = (i % 256).toByte()
        }
        
        heavyData.add(data)
        println("LeakingManager: 注册成功！当前监听器: ${listeners.size}, 已累计泄漏约 ${heavyData.size * 100}MB")
        println("LeakingManager: 提示 - 多次进入退出该页面，Java 堆内存将持续飙升直至 OOM！")
    }

    fun unregisterListener(listener: (String) -> Unit) {
        listeners.remove(listener)
        // 💀 实验期间注释掉清理逻辑，确保泄漏百分之百发生
        // if (listeners.isEmpty()) {
        //     heavyData.clear()
        // }
        println("LeakingManager: 已解除注册，剩余监听器: ${listeners.size}, 但 heavyData 依然保留！")
    }
}
