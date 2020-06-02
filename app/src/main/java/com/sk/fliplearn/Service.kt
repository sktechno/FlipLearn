package com.sk.fliplearn

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url


//
// Created by SK(Sk) on 01/06/20.
// Copyright (c) 2020 Sktech. All rights reserved.

interface Service {

    @GET
    @Streaming
    fun downloadFile(@Url fileUrl: String?): Call<ResponseBody>

}