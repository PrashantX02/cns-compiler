package com.example.cnscompiler

import com.example.cnscompiler.api.JDoodleApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api.jdoodle.com/v1/"

    val instance: JDoodleApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(JDoodleApi::class.java)
    }
}
