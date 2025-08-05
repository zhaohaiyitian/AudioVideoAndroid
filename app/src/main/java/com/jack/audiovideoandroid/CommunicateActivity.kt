package com.jack.audiovideoandroid

import android.os.Bundle
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import com.jack.audiovideoandroid.databinding.ActivityCommunicateBinding
import com.jack.audiovideoandroid.databinding.ActivityH264PlayerBinding
import com.jack.audiovideoandroid.databinding.ActivityScreenCastBinding

/**
 * 音视频通话
 */
class CommunicateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommunicateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunicateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {


        }

    }
}