package com.jack.audiovideoandroid.opengl

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * 相机预览视图
 * 使用OpenGL ES渲染相机预览，并支持滤镜切换
 */
class CameraView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
): GLSurfaceView(context, attrs), GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    private var mCameraTexture: SurfaceTexture? = null
    private val filterManager = FilterManager(context)
    private var currentFilter: ScreenFilter? = null
    
    // 纹理ID
    private var textureId = 0
    
    // 是否初始化完成
    private var isInitialized = false

    init {
        // 设置OpenGL ES版本号
        setEGLContextClientVersion(2)
        setRenderer(this)
        //设置手动渲染
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // 向OpenGL申请创建一个新的、空的纹理对象，并获取这个纹理对象的唯一ID
        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        textureId = textures[0]
        
        // 创建SurfaceTexture
        mCameraTexture = SurfaceTexture(textureId)
        mCameraTexture?.setOnFrameAvailableListener(this)
        
        // 获取当前滤镜
        currentFilter = filterManager.getFilter()
        
        isInitialized = true
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        //这行代码本身并不执行任何绘制或清除操作，它只是告诉OpenGL：“下一次当你被命令去清除颜色缓冲区时，请使用这个我现在指定的颜色
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        // 执行清除操作。这行代码是一个执行命令。它命令OpenGL：现在，请使用你当前存储的‘清除颜色’（也就是我们上一步设置的黑色），去填充（擦除）指定的缓冲区
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT) // 这指定了颜色缓冲区。颜色缓冲区是最终显示在屏幕上的像素颜色信息的存储位置。所以清除它，就等于擦除了屏幕上的画面
        
        mCameraTexture?.let {
            // 捕获数据流中最新到达的一帧图像，并将其更新到 SurfaceTexture 所管理的那个OpenGL纹理上
            // 将 SurfaceTexture 缓冲区（共享内存）中最新的那帧图像数据，正式绑定并更新到它所管理的OpenGL纹理（即 textureId）上。
            // 只有在这之后，GPU才能在着色器中通过采样器访问到这帧新图像。
            // SurfaceTexture的缓冲区巧妙地避开了CPU，实现了数据从生产者到GPU的直接通路
            it.updateTexImage()
            val mtx = FloatArray(16)
            // 根据从相机那里接收到的画面的实际情况，计算出一个修正矩阵，并把结果填满准备好的mtx里
            it.getTransformMatrix(mtx)
            // 使用更新后的纹理ID和变换矩阵将图像绘制到屏幕上
            currentFilter?.onDraw(width, height, mtx, textureId)
        }
    }

    // SurfaceTexture 在接收到新的一帧数据后，会立即在其指定的监听器上回调 onFrameAvailable() 方法
    // onFrameAvailable() 方法并不在你的主UI线程或OpenGL渲染线程上执行。它通常是在一个由Android系统框架管理的特定线程（例如Binder线程）上被调用的。
    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        // 通知 GLSurfaceView 渲染器（Renderer）的数据已经“变脏”（dirty），需要进行一次重绘
        // requestRender() 不会立即调用 onDrawFrame()。它只是向 GLSurfaceView 的渲染线程发送一个请求，告诉它在下一个合适的时机执行一次渲染。
        requestRender()
    }
    
    /**
     * 切换滤镜
     */
    fun switchFilter() {
        queueEvent {
            filterManager.switchToNextFilter()
            currentFilter = filterManager.getFilter()
        }
    }
    
    /**
     * 获取SurfaceTexture，用于CameraX预览
     */
    fun getSurfaceTexture(): SurfaceTexture? {
        return mCameraTexture
    }
    
    /**
     * 释放资源
     */
    fun release() {
        filterManager.release()
        mCameraTexture?.release()
        mCameraTexture = null
    }
}