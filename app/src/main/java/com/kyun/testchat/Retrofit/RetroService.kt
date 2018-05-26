package com.kyun.testchat.Retrofit

import com.google.gson.JsonElement
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RetroService {

    @GET("/test")
    fun test(@Query("data") data : String) : Call<JsonElement>

    @POST("/test2")
    fun test2(@Field("data") data : String) : Call<ResponseBody>

}