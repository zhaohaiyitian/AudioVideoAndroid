package com.jack.audiovideoandroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jack.audiovideoandroid.databinding.ActivityMainBinding
import com.jack.audiovideoandroid.opengl.OpenglActivity
import com.jack.audiovideoandroid.opengl.TestActivity

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
            mediaProjection.setOnClickListener {
                startActivity(Intent(this@MainActivity, MediaProjectionActivity::class.java))
            }
            screenCast.setOnClickListener {
                startActivity(Intent(this@MainActivity, ScreenCastActivity::class.java))
            }
            communicate.setOnClickListener {
                startActivity(Intent(this@MainActivity, CommunicateActivity::class.java))
            }
            opengl.setOnClickListener {
                startActivity(Intent(this@MainActivity, OpenglActivity::class.java))
            }
            test.setOnClickListener {
                startActivity(Intent(this@MainActivity, TestActivity::class.java))
            }
        }
    }
}