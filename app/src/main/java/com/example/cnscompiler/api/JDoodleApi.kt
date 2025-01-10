package com.example.cnscompiler.api

import com.example.cnscompiler.model.JDoodleRequest
import com.example.cnscompiler.model.JDoodleResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface JDoodleApi {
    @Headers("Content-Type: application/json")
    @POST("execute")
    fun executeCode(@Body request: JDoodleRequest): Call<JDoodleResponse>
}
