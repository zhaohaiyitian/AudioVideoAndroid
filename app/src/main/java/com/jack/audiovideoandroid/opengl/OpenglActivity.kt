package com.jack.audiovideoandroid.opengl

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jack.audiovideoandroid.databinding.ActivityCommunicateBinding
import com.jack.audiovideoandroid.databinding.ActivityOpenglBinding

/**
 * opengl es
 */
class OpenglActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOpenglBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpenglBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {


        }
    }
}