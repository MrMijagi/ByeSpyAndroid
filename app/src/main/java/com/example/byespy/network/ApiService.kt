package com.example.byespy.network

import android.content.Context
import android.provider.ContactsContract
import com.example.byespy.network.requests.*
import com.example.byespy.network.response.*
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
    // login + registration
    @POST("oauth/token")
    suspend fun signIn(@Body request: LoginRequest): LoginResponse
    @POST("oauth/revoke")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): RefreshTokenResponse
    @GET("authorize_user_first_step")
    suspend fun authorizeUser(@Query("email") email: String, @Query("password") password: String): AuthorizeUserResponse
    @POST("sign_up")
    suspend fun signUp(@Body request: RegistrationRequest): Response<Unit>

    // profile
    @GET("profile")
    suspend fun getProfile(): ProfileResponse
    @GET("profile/validate_password")
    suspend fun validatePassword(@Query("id") id: Int, @Query("value") value: String): Response<Boolean>
    @PUT("profile")
    suspend fun editUsername(@Body request: ChangeUsernameRequest): Response<ProfileResponse>
    @PUT("profile")
    suspend fun editPassword(@Body request: ChangePasswordRequest): Response<ProfileResponse>

    // messages
    @POST("messages/save_message")
    suspend fun saveMessage(@Body request: SaveMessageRequest): Response<Unit>

    // invitations
    @GET("invitations")
    suspend fun getInvitations(): InvitationsResponse
    @POST("invitations")
    suspend fun sendInvitation(@Body request: InvitationRequest): Response<Unit>
    @DELETE("invitations/{id}")
    suspend fun cancelInvitation(@Path("id") id: Int): Response<Unit>
    @PUT("invitations/{id}")
    suspend fun acceptOrRejectInvitation(@Path("id") id: Int, @Body request: InvitationStatusRequest): Response<Unit>
    @GET("invitations/check")
    suspend fun checkEmail(@Query("value") value: String): Response<Unit>
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