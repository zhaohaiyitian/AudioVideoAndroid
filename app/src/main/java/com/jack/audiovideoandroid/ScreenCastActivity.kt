package com.jack.audiovideoandroid

import android.os.Bundle
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import com.jack.audiovideoandroid.databinding.ActivityH264PlayerBinding
import com.jack.audiovideoandroid.databinding.ActivityScreenCastBinding

/**
 * 投屏
 */
class ScreenCastActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScreenCastBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScreenCastBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {


        }

    }
}