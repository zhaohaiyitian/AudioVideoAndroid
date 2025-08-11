package com.jack.audiovideoandroid.opengl

import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.jack.audiovideoandroid.R
import com.jack.audiovideoandroid.databinding.ActivityTestBinding
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * author：jie.wang
 * desc:
 */
class TestActivity: AppCompatActivity() {

    private lateinit var binding: ActivityTestBinding
    private var mTextureId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            surfaceView.setEGLContextClientVersion(2)
            val bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.demo)
            val imageFilter = ImageFilter(this@TestActivity)
            surfaceView.setRenderer(object : GLSurfaceView.Renderer {
                override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
                    Log.d("wangjie","onSurfaceCreated")
                    // 设置清屏颜色
                    GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
                    mTextureId = imageFilter.initTexture(bitmap)
                    Log.d("wangjie","mTextureId: $mTextureId")
                }

                override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
                    GLES20.glViewport(0, 0, width, height)
                }

                override fun onDrawFrame(gl: GL10?) {
                    Log.d("wangjie","onDrawFrame")
                    imageFilter.drawFrame(mTextureId)
                }

            })
        }
    }
}