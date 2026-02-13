package com.shineofeidos.mockapiproject.data.network

import com.shineofeidos.mockapiproject.data.model.User
import retrofit2.http.GET

interface ApiService {
    // 这里的路径需要根据你在 Apifox 中定义的接口路径进行修改
    // 例如：如果你的 Mock 地址是 https://mock.apifox.cn/m1/123456-0-default/users
    // 那么 Base URL 是 https://mock.apifox.cn/m1/123456-0-default/
    // GET 注解里的路径就是 "users"
    @GET("users")
    suspend fun getUsers(): List<User>
}
