package com.example.byespy.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// replace ip with correct one
private const val BASE_URL = "http://192.168.8.109:4000/api/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

// interceptor + client are for debugging requests - can be removed for release
val interceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

val client = OkHttpClient
    .Builder()
    .addInterceptor(interceptor)
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .client(client)
    .build()

interface ApiService {
    @POST("oauth/token")
    suspend fun signIn(@Body request: LoginRequest): LoginResponse
//    @POST("sign_up")
//    suspend fun signUp(@Body request: RegistrationRequest): RegistrationResponse
//    @POST("register_keys")
//    suspend fun registerKeys(@Body request: KeyRegistrationRequest): KeyRegistrationResponse
//    @POST("refresh_token")
//    suspend fun refreshToken(@Body request: RefreshTokenRequest): RefreshTokenResponse
}

object Api {
    val retrofitService: ApiService by lazy { retrofit.create(ApiService::class.java) }
}