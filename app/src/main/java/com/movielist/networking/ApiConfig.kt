package com.movielist.networking

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object {
        fun getApiService() : ApiService {

            val authInterceptor = { chain: okhttp3.Interceptor.Chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", ACCESS_TOKEN)
                    .build()
                chain.proceed(request)
            }

            val loggingInterceptor = HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY)

            val client = OkHttpClient.Builder()
                //.addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()

            return retrofit.create(ApiService::class.java)
        }

        const val ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyZDI3YTE5M2I3YTRiZWFhMGU2ZWQ0YTYxYWM2MTJjMSIsIm5iZiI6MTczMDczNDg4OC44OTA1MDg3LCJzdWIiOiI2NzI2OTE4MTlkY2MyZGQ1MzQ3NDM1MjciLCJzY29wZXMiOlsiYXBpX3JlYWQiXSwidmVyc2lvbiI6MX0.4nnNJR9DsOrh8pSmc8OdlEXG0oxGWg408W9Nq_3_2n8"
    }
}