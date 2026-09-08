package com.example.a24012011120_mad_practical4

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log

class AlarmService : Service() {
    var mp: MediaPlayer?=null
    val TAG = "AlarmService"
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand: Start Service")
        if(intent!=null){
            if(mp==null)
                mp= MediaPlayer.create(this,R.raw.alarm)
            mp?.start()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        Log.i(TAG, "onStartCommand: Stop Service")
        mp?.stop()
        super.onDestroy()
    }
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}