package com.sk.fliplearn
//
// Created by SK(Sk) on 01/06/20.
// Copyright (c) 2020 Sktech. All rights reserved.

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sk.fliplearn.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.toolbar.view.*
import java.io.File


class MainActivity : AppCompatActivity() {
    companion object {

        const val PROGRESS_UPDATE = "progress_update"
        private const val PERMISSION_REQUEST_CODE = 1

    }

    lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MainViewModel
    lateinit var adapter: Adapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        setToolbar()
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        binding.viewModel = viewModel

        setUpRecyclerView()
        initObserver()

        if (!checkFile()) {
            if (checkPermission()) {
                startImageDownload();
            } else {
                requestPermission();
            }
            registerReceiver()
        } else {
            viewModel.getData()
        }

    }

    private fun setToolbar() {
        setSupportActionBar(binding.include.toolbar)
        title = ""

    }

    private fun setSearchView(searchView: SearchView) {

        searchView.imeOptions = EditorInfo.IME_ACTION_DONE
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)

        val search_Item = menu?.findItem(R.id.action_search)

        val searchView = search_Item?.actionView as SearchView

        setSearchView(searchView)


        return true
    }

    private fun setUpRecyclerView() {

        binding.recycler.setHasFixedSize(true)
        binding.recycler.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )
        val layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recycler.layoutManager = layoutManager

        adapter = Adapter()

        binding.recycler.adapter = adapter


    }

    private fun initObserver() {
        viewModel.data.observe(this, Observer {
            adapter.addList(it)

        })
    }

    private fun checkPermission(): Boolean {

        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun registerReceiver() {
        val bManager: LocalBroadcastManager = LocalBroadcastManager.getInstance(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction(PROGRESS_UPDATE)
        bManager.registerReceiver(mBroadcastReceiver, intentFilter)
    }

    private fun startImageDownload() {
        val intent = Intent(this, DownloadService::class.java)
        startService(intent)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )
    }

    private fun checkFile(): Boolean {
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .path + File.separator.toString() +
                    Repo.fileName
        )


        return file.exists()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startImageDownload()
            } else {
                Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private val mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action == PROGRESS_UPDATE) {
                val downloadComplete =
                    intent.getBooleanExtra("downloadComplete", false)
                //Log.d("API123", download.getProgress() + " current progress");
                if (downloadComplete) {
                    Toast.makeText(
                        applicationContext,
                        "File download completed",
                        Toast.LENGTH_SHORT
                    ).show()

                    viewModel.getData()

                }
            }
        }
    }
}
