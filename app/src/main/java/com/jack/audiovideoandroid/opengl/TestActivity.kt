package com.jack.audiovideoandroid.opengl

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jack.audiovideoandroid.databinding.ActivityTestBinding

/**
 * author：jie.wang
 * desc:
 */
class TestActivity: AppCompatActivity() {

    private lateinit var binding: ActivityTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {

        }
    }
}