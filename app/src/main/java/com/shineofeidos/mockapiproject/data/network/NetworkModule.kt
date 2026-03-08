package com.shineofeidos.mockapiproject.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {
    // 注意：Base URL 必须以 / 结尾
    // 根据你的运行环境切换对应的 BASE_URL

    // 1. Genymotion 虚拟机专用（推荐在 Genymotion 中使用）
    // Genymotion 通过 10.0.3.2 访问宿主机
    private const val BASE_URL_GENYMOTION = "http://10.0.3.2:4523/m1/7815393-7563142-default/"

    // 2. 真机调试专用（确保手机和电脑在同一 Wi-Fi 下）
    // 当前检测到的局域网 IP 为: 192.168.2.52
    private const val BASE_URL_REAL_DEVICE = "http://192.168.2.52:4523/m1/7815393-7563142-default/"

    // 3. Android 官方模拟器专用
    // Android Emulator 通过 10.0.2.2 访问宿主机
    private const val BASE_URL_EMULATOR = "http://10.0.2.2:4523/m1/7815393-7563142-default/"

    // 当前使用的 BASE_URL，根据你的环境切换
    private const val BASE_URL = BASE_URL_GENYMOTION

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        // BODY 级别会打印请求头、请求体、响应头、响应体，最详细，适合调试
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor()) // 添加我们自定义的拦截器
        .authenticator(TokenAuthenticator()) // 添加 401 自动刷新 Token 机制
        .addInterceptor(loggingInterceptor) // 日志拦截器通常放在最后，以便打印出所有添加的 Header
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
