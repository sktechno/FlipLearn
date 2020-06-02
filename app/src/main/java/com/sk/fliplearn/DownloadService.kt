package com.sk.fliplearn

import android.R
import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import java.io.*


//
// Created by SK(Sk) on 01/06/20.
// Copyright (c) 2020 Sktech. All rights reserved.

class DownloadService : IntentService("Download") {

    private lateinit var notificationBuilder: NotificationCompat.Builder
    private lateinit var notificationManager: NotificationManager

    override fun onHandleIntent(intent: Intent?) {
        notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel("id", "an", NotificationManager.IMPORTANCE_LOW)
            notificationChannel.description = "no sound"
            notificationChannel.setSound(null, null)
            notificationChannel.enableLights(false)
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)
        }


        notificationBuilder = NotificationCompat.Builder(this, "id")
            .setSmallIcon(R.drawable.stat_sys_download)
            .setContentTitle("Download")
            .setContentText("Downloading File")
            .setDefaults(0)
            .setAutoCancel(true)
        notificationManager.notify(0, notificationBuilder.build())

        initRetrofit();

    }

    private fun initRetrofit() {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(Repo.fileBaseUrl)
            .build()
        val service = retrofit.create(Service::class.java)
        val request: Call<ResponseBody> =
            service.downloadFile(Repo.fileUrl)
        try {
            downloadFile(request.execute().body())
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(IOException::class)
    private fun downloadFile(body: ResponseBody?) {
        var count: Int = 0;
        val data = ByteArray(1024 * 4)
        val fileSize = body!!.contentLength()
        val inputStream: InputStream = BufferedInputStream(body.byteStream(), 1024 * 8)
        val outputFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            Repo.fileName
        )
        val outputStream: OutputStream = FileOutputStream(outputFile)
        var total: Long = 0
        var downloadComplete = false
        //int totalFileSize = (int) (fileSize / (Math.pow(1024, 2)));

        //int totalFileSize = (int) (fileSize / (Math.pow(1024, 2)));
        while (inputStream.read(data).also { count = it } != -1) {
            total += count.toLong()
            val progress = ((total * 100).toDouble() / fileSize.toDouble()).toInt()
            updateNotification(progress)
            outputStream.write(data, 0, count)
            downloadComplete = true
        }
        onDownloadComplete(downloadComplete)
        outputStream.flush()
        outputStream.close()
        inputStream.close()

    }

    private fun updateNotification(currentProgress: Int) {
        notificationBuilder.setProgress(100, currentProgress, false)
        notificationBuilder.setContentText("Downloaded: $currentProgress%")
        notificationManager.notify(0, notificationBuilder.build())
    }

    private fun sendProgressUpdate(downloadComplete: Boolean) {
        val intent = Intent(MainActivity.PROGRESS_UPDATE)
        intent.putExtra("downloadComplete", downloadComplete)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun onDownloadComplete(downloadComplete: Boolean) {
        sendProgressUpdate(downloadComplete)
        notificationManager.cancel(0)
        notificationBuilder.setProgress(0, 0, false)
        notificationBuilder.setContentText("File Download Complete")
        notificationManager.notify(0, notificationBuilder.build())
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        notificationManager.cancel(0);
    }
}