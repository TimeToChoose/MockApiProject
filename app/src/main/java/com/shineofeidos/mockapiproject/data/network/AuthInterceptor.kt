package com.shineofeidos.mockapiproject.data.network

import okhttp3.Interceptor
import okhttp3.Response

/**
 * 学习点：OkHttp 拦截器 (Interceptor)
 *
 * 拦截器是 OkHttp 最核心的机制，它采用了“责任链模式”。
 * 所有的请求都会经过拦截器链，你可以在这里：
 * 1. 在请求发出前：统一添加 Header（如 Token, User-Agent）、加密参数。
 * 2. 在响应返回后：统一解密数据、处理 Token 失效、打印日志。
 */
class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // 1. 获取原始请求
        val originalRequest = chain.request()

        // 2. 创建新请求（Builder模式），添加公共参数
        val newRequest = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer mock-token-123456") // 模拟 Token
            .addHeader("Device-Type", "Android")
            .addHeader("App-Version", "1.0.0")
            .method(originalRequest.method, originalRequest.body)
            .build()

        // 3. 继续执行责任链，发起网络请求
        // chain.proceed() 是连接下一个拦截器的关键
        return chain.proceed(newRequest)
    }
}
