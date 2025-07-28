package com.jack.audiovideoandroid

import android.content.Intent
import android.media.projection.MediaProjectionManager
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
            val mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode,it)
            val encoder = H264Encoder(mediaProjection)
            encoder.start()
        }

    }
}