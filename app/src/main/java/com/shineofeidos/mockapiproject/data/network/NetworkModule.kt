package com.shineofeidos.mockapiproject.data.network

import com.shineofeidos.mockapiproject.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {
    // 注意：Base URL 必须以 / 结尾
    // 使用 BuildConfig 中的 BASE_URL，根据构建变体自动切换
    // 可在 build.gradle.kts 中配置不同环境的地址
    private const val BASE_URL = BuildConfig.BASE_URL

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
