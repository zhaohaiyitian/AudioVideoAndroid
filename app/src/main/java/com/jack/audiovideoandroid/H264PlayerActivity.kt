package com.jack.audiovideoandroid

import android.os.Bundle
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import com.jack.audiovideoandroid.databinding.ActivityH264PlayerBinding

class H264PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityH264PlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityH264PlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
                override fun surfaceCreated(holder: SurfaceHolder) {
                    val surface = holder.surface
                    val player = H264Player(surface,this@H264PlayerActivity)
                    player.play()
                }

                override fun surfaceChanged(
                    holder: SurfaceHolder,
                    format: Int,
                    width: Int,
                    height: Int
                ) {

                }

                override fun surfaceDestroyed(holder: SurfaceHolder) {

                }

            })

        }

    }
}