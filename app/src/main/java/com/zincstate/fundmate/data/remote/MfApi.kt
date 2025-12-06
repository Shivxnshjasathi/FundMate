package com.zincstate.fundmate.data.remote

import com.zincstate.fundmate.data.model.FundDetailResponse
import com.zincstate.fundmate.data.model.SchemeDto
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MfApi {

    // Phase 1 Requirement: Home Screen List
    @GET("mf/latest")
    suspend fun getLatestSchemes(
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): List<SchemeDto>

    // Phase 1 Requirement: Detail Screen
    @GET("mf/{code}/latest")
    suspend fun getSchemeDetails(
        @Path("code") code: Int
    ): FundDetailResponse
}

// Simple Singleton for Phase 1 (We will move to Hilt in Phase 6)
object RetrofitClient {
    private const val BASE_URL = "https://api.mfapi.in/"

   //private const val BASE_URL = "[https://api.mfapi.in/](https://api.mfapi.in/)"

    val api: MfApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MfApi::class.java)
    }
}