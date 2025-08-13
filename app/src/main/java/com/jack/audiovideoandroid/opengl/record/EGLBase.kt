package com.jack.audiovideoandroid.opengl.record

import android.content.Context
import android.opengl.EGL14
import android.opengl.EGLConfig
import android.opengl.EGLContext
import android.opengl.EGLDisplay
import android.opengl.EGLSurface

/**
 * author：jie.wang
 * desc:
 */
class EGLBase(
    context: Context,
    val path: String,
    var width: Int,
    var height: Int,
    var eglContext: EGLContext
) {
    private var mEglDisplay: EGLDisplay? = null
    private var mEglConfig: EGLConfig? = null
    private var mEglContext: EGLContext? = null
    private var mEglSurface: EGLSurface? = null

    init {
        createEGL(eglContext)
    }








    private fun createEGL(eglContext: EGLContext) {
        mEglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
        if (mEglDisplay == EGL14.EGL_NO_DISPLAY) {
            throw RuntimeException("eglGetDisplay failed")
        }
        val version = IntArray(2)

        if (!EGL14.eglInitialize(mEglDisplay, version, 0, version, 1)) {
            throw RuntimeException("eglInitialize failed")
        }

        // egl 根据我们配置的属性 选择一个配置
        val attrib_list = intArrayOf(
            EGL14.EGL_RED_SIZE, 8,
            EGL14.EGL_GREEN_SIZE, 8,
            EGL14.EGL_BLUE_SIZE, 8,
            EGL14.EGL_ALPHA_SIZE, 8,
            EGL14.EGL_RENDERABLE_TYPE,
            EGL14.EGL_OPENGL_ES2_BIT,  //egl版本 2
            EGL14.EGL_NONE
        )
        val configs = arrayOfNulls<EGLConfig>(1)
        val num_config = IntArray(1)
        if (!EGL14.eglChooseConfig(mEglDisplay,attrib_list,0,configs,0,configs.size,num_config,0)) {
            throw RuntimeException("eglChooseConfig failed")
        }
        mEglConfig= configs[0]
        val ctx_attrib_list = intArrayOf(
            EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,  //egl版本 2
            EGL14.EGL_NONE
        )
        mEglContext = EGL14.eglCreateContext(mEglDisplay,mEglConfig,eglContext,ctx_attrib_list,0)
        if (eglContext == EGL14.EGL_NO_CONTEXT) {
            throw RuntimeException("eglCreateContext failed")
        }
    }
}