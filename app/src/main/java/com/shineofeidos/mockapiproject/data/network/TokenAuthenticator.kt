package com.shineofeidos.mockapiproject.data.network

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

/**
 * 学习点：Token 自动刷新 (Authenticator)
 *
 * 当服务端返回 401 Unauthorized 时，OkHttp 会自动回调这个接口。
 * 你可以在这里：
 * 1. 同步请求刷新 Token 的接口。
 * 2. 如果刷新成功，使用新 Token 重新发起刚才失败的请求。
 * 3. 如果刷新失败，跳转到登录页。
 *
 * 优点：对上层业务代码（ViewModel/Repository）完全透明，它们不需要关心 Token 是否过期。
 */
class TokenAuthenticator : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        // 防止无限循环：如果新 Token 也 401 了，就放弃
        if (response.request.header("Authorization")?.contains("new-token") == true) {
            return null
        }

        // 模拟同步刷新 Token 的过程
        val newToken = refreshTokenSync()

        return if (newToken != null) {
            // 刷新成功，使用新 Token 重试原请求
            response.request.newBuilder()
                .header("Authorization", "Bearer $newToken")
                .build()
        } else {
            // 刷新失败，放弃重试（此时上层会收到 401 错误）
            null
        }
    }

    private fun refreshTokenSync(): String? {
        // 这里应该调用真实的刷新接口
        // 注意：这里必须是同步调用 (execute)，不能是异步 (enqueue)
        return "mock-new-token-987654"
    }
}
