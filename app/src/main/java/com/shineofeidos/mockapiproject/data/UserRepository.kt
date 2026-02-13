package com.shineofeidos.mockapiproject.data

import com.shineofeidos.mockapiproject.data.model.User
import com.shineofeidos.mockapiproject.data.network.NetworkModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class UserRepository {
    private val apiService = NetworkModule.apiService

    /**
     * 学习点：Repository 模式与异常处理
     *
     * Repository 的职责是将底层的数据（网络、数据库）转换为上层（ViewModel）能理解的业务数据。
     * 这里我们展示了如何优雅地捕获 Retrofit 抛出的异常，并将其转换为 Result 类型。
     */
    suspend fun getUsers(): Result<List<User>> {
        return withContext(Dispatchers.IO) {
            try {
                // 发起网络请求
                val users = apiService.getUsers()
                
                // 如果成功，返回 Success
                Result.success(users)
            } catch (e: Exception) {
                // 统一处理异常
                val error = handleException(e)
                Result.failure(error)
            }
        }
    }

    private fun handleException(e: Exception): Exception {
        return when (e) {
            is IOException -> Exception("网络连接失败，请检查您的网络设置", e)
            is HttpException -> {
                val code = e.code()
                val message = e.message()
                Exception("服务器错误 ($code): $message", e)
            }
            else -> Exception("未知错误: ${e.message}", e)
        }
    }
}
