package com.jack.audiovideoandroid.opengl

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * author：jie.wang
 * desc:  创建EGL显示连接、配置EGL表面、创建EGL上下文（Context），并将上下文绑定到线程和表面。
 * 1.自动化线程管理：它会自动为你创建一个独立的渲染线程 (Render Thread)，所有的OpenGL API调用（如 GLES20.glClear、GLES20.glDrawArrays 等）都会在这个独立的线程上执行，从而完全不会阻塞主UI线程，保证了应用的流畅性
 * 2.管理EGL上下文和渲染表面，它完全自动化了EGL的管理
 * 3.提供简洁的渲染生命周期
 * 4.与Activity生命周期无缝集成
 */
class MyGlSurfaceView(context: Context?, attrs: AttributeSet?) : GLSurfaceView(context, attrs),GLSurfaceView.Renderer {

    init {
        setEGLContextClientVersion(2)
        setRenderer(this) // 在该函数中会启动一个新的线程来创建EGL环境
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    // 在GL线程上，EGL上下文和渲染表面(Surface)成功创建后仅被调用一次
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        Log.d("wangjie","onSurfaceCreated")
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        Log.d("wangjie","onSurfaceChanged")
        GLES20.glViewport(0, 0, width, height)
    }

    // GL线程会进入一个无限循环，在这个循环里反复地调用 onDrawFrame
    // onDrawFrame() 被调用（至少一次）
    override fun onDrawFrame(gl: GL10?) {
        Log.d("wangjie","onDrawFrame")
        //这行代码本身并不执行任何绘制或清除操作，它只是告诉OpenGL：“下一次当你被命令去清除颜色缓冲区时，请使用这个我现在指定的颜色
        GLES20.glClearColor(1.0f, 0f, 0f, 1f)
        // 执行清除操作。这行代码是一个执行命令。它命令OpenGL：现在，请使用你当前存储的‘清除颜色’（也就是我们上一步设置的黑色），去填充（擦除）指定的缓冲区
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT) // 这指定了颜色缓冲区。颜色缓冲区是最终显示在屏幕上的像素颜色信息的存储位置。所以清除它，就等于擦除了屏幕上的画面
    }
}