package com.sk.fliplearn

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer


//
// Created by SK(Sk) on 01/06/20.
// Copyright (c) 2020 Sktech. All rights reserved.

class MainViewModel : AndroidViewModel {

    private var mApplication: Application
    private var repo: Repo

    val data = MutableLiveData<ArrayList<Product>>()

    constructor(application: Application) : super(application) {
        this.mApplication = application
        repo = Repo(mApplication.applicationContext)
    }

    fun getData() {
        val repo = Repo(mApplication.applicationContext)
        repo.loadJson()
        if (!repo.liveData.hasActiveObservers())
            repo.liveData.observeForever(observeData())
    }

    private fun observeData(): Observer<in ArrayList<Product>> {
        return Observer {
            data.value = it
        }
    }

    override fun onCleared() {
        super.onCleared()
        repo.liveData.removeObserver(observeData())

    }

}