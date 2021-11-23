package com.example.byespy.network

import android.content.Context
import com.example.byespy.network.requests.LoginRequest
import com.example.byespy.network.requests.RefreshTokenRequest
import com.example.byespy.network.requests.RegistrationRequest
import com.example.byespy.network.requests.SaveMessageRequest
import com.example.byespy.network.response.AuthorizeUserResponse
import com.example.byespy.network.response.LoginResponse
import com.example.byespy.network.response.ProfileResponse
import com.example.byespy.network.response.RefreshTokenResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.util.*

// replace ip with correct one
private const val BASE_URL = "http://192.168.8.109:4000/api/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .add(Date::class.java, Rfc3339DateJsonAdapter())
    .build()

interface ApiService {
    @POST("oauth/token")
    suspend fun signIn(@Body request: LoginRequest): LoginResponse
    @GET("authorize_user_first_step")
    suspend fun authorizeUser(@Query("email") email: String, @Query("password") password: String): Response<AuthorizeUserResponse>
    @GET("profile")
    suspend fun getProfile(): ProfileResponse
    @POST("sign_up")
    suspend fun signUp(@Body request: RegistrationRequest): Response<Unit>
//    @POST("register_keys")
//    suspend fun registerKeys(@Body request: KeyRegistrationRequest): KeyRegistrationResponse
    @POST("oauth/revoke")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): RefreshTokenResponse
    @POST("messages/save_message")
    suspend fun saveMessage(@Body request: SaveMessageRequest): Response<Unit>
}

object Api {
    private lateinit var retrofitService: ApiService

    fun getApiService(context: Context) : ApiService {
        if (!::retrofitService.isInitialized) {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .baseUrl(BASE_URL)
                .client(okHttpClient(context))
                .build()

            retrofitService = retrofit.create(ApiService::class.java)
        }

        return retrofitService
    }

    fun okHttpClient(context: Context): OkHttpClient {
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient
            .Builder()
            .addInterceptor(interceptor)
            .addInterceptor(AuthInterceptor(context))
            .build()
    }
}