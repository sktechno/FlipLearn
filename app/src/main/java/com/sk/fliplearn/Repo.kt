package com.sk.fliplearn

import android.content.Context
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.FileReader


//
// Created by SK(Sk) on 01/06/20.
// Copyright (c) 2020 Sktech. All rights reserved.

class Repo(private val context: Context) {

    companion object {
        const val fileName = "search.json"
        const val fileBaseUrl = "https://d28g620bxwut2m.cloudfront.net/"
        const val fileUrl = "assignments/search.json"
    }

    private val TAG = Repo::class.java.name
    val liveData = MutableLiveData<ArrayList<Product>>()

    fun loadJson() {

        GlobalScope.launch {
            val model = loadModel()
            model?.let {
                liveData.postValue(it)
            }
        }
    }

    private suspend fun getJson(): String {

        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            fileName
        )

        try {
            val fileReader = FileReader(file)
            val bufferedReader = BufferedReader(fileReader)
            val stringBuilder = StringBuilder()
            var line = bufferedReader.readLine()
            while (line != null) {
                stringBuilder.append(line).append("\n")
                line = bufferedReader.readLine()
            }
            bufferedReader.close()
// This responce will have Json Format String
            val responce = stringBuilder.toString()
            Log.e(TAG, responce)
            return responce

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    private suspend fun loadModel(): ArrayList<Product>? {
        val json = getJson()
        if (TextUtils.isEmpty(json))
            return ArrayList<Product>()

        val gson = GsonBuilder().create()
        val type = object : TypeToken<ArrayList<Product>>() {}.type
        return gson.fromJson(json, type)
    }

}