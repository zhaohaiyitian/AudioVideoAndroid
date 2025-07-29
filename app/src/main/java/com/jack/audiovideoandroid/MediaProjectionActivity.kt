package com.jack.audiovideoandroid

import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jack.audiovideoandroid.databinding.ActivityMediaProjectionBinding

class MediaProjectionActivity : AppCompatActivity() {

    private lateinit var mMediaProjectionManager: MediaProjectionManager
    private lateinit var binding: ActivityMediaProjectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaProjectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            captureBtn.setOnClickListener {
                start()
            }
            stopBtn.setOnClickListener {
                stopService(Intent(this@MediaProjectionActivity, ScreenCaptureService::class.java))
            }
        }

    }

    private fun start() {
        mMediaProjectionManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val captureIntent = mMediaProjectionManager.createScreenCaptureIntent()
        startActivityForResult(captureIntent,1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK || requestCode !=1) return
        data?.let {
            startScreenRecordService(resultCode, it)
        }
    }

    private fun startScreenRecordService(resultCode: Int, resultData: Intent) {
        val serviceIntent = Intent(this, ScreenCaptureService::class.java).apply {
            putExtra(ScreenCaptureService.RESULT_CODE, resultCode)
            putExtra(ScreenCaptureService.RESULT_DATA, resultData)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }
}