package com.jack.audiovideoandroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceHolder
import com.jack.audiovideoandroid.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        // Used to load the 'audiovideoandroid' library on application startup.
        init {
            System.loadLibrary("audiovideoandroid")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            h264Player.setOnClickListener {
                startActivity(Intent(this@MainActivity, H264PlayerActivity::class.java))
            }
        }
    }
}