package com.example.byespy.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// replace ip with correct one
private const val BASE_URL = "http://192.168.8.109:4000/api/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface ApiService {
    @POST("oauth/token")
    suspend fun signIn(@Body request: LoginRequest): LoginResponse
}

object Api {
    val retrofitService: ApiService by lazy { retrofit.create(ApiService::class.java) }
}