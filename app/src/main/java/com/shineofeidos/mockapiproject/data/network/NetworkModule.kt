package com.shineofeidos.mockapiproject.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {
    // TODO: 请将此处替换为你的 Apifox 云端 Mock 地址或者本地 Mock 地址
    // 注意：Base URL 必须以 / 结尾
    // 真机调试：请确保手机和电脑在同一 Wi-Fi 下，并使用电脑的局域网 IP
    // 当前检测到的局域网 IP 为: 192.168.2.52
    private const val BASE_URL = "http://192.168.2.52:4523/m1/7815393-7563142-default/"

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
