package com.jack.audiovideoandroid.opengl

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.jack.audiovideoandroid.databinding.ActivityOpenglBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * OpenGL ES相机预览和滤镜应用
 * 使用CameraX提供相机预览，OpenGL ES实现滤镜效果
 */
class OpenglActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOpenglBinding
    
    private lateinit var cameraExecutor: ExecutorService
    
    // 请求相机权限的请求码
    private val REQUEST_CODE_PERMISSIONS = 10
    // 需要的权限
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpenglBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // 初始化相机执行器
        cameraExecutor = Executors.newSingleThreadExecutor()
        
        // 检查相机权限
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
        
        // 设置切换滤镜按钮点击事件
        binding.btnSwitchFilter.setOnClickListener {
            binding.cameraView.switchFilter()
        }
    }
    
    /**
     * 启动相机
     */
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        
        cameraProviderFuture.addListener({
            // 获取相机提供者
            val cameraProvider = cameraProviderFuture.get()
            
            // 创建预览用例
            val preview = Preview.Builder().build()
            
            // 选择后置相机
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            
            try {
                // 解绑所有用例
                cameraProvider.unbindAll()
                
                // 将相机与生命周期绑定
                val camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview)
                
                // 获取CameraView的SurfaceTexture
                val surfaceTexture = binding.cameraView.getSurfaceTexture()
                
                // 设置预览的SurfaceProvider
                surfaceTexture?.let { texture ->
                    val surface = Preview.SurfaceProvider { request ->
                        texture.setDefaultBufferSize(
                            request.resolution.width,
                            request.resolution.height
                        )
                        val surface = android.view.Surface(texture)
                        request.provideSurface(surface, cameraExecutor) { }
                    }
                    preview.setSurfaceProvider(surface)
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "相机绑定失败", e)
                Toast.makeText(this, "相机启动失败: ${e.message}", Toast.LENGTH_SHORT).show()
            }
            
        }, ContextCompat.getMainExecutor(this))
    }
    
    /**
     * 检查是否已授予所有权限
     */
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * 处理权限请求结果
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this, "未授予相机权限，无法使用相机功能", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // 释放相机资源
        cameraExecutor.shutdown()
        // 释放CameraView资源
        binding.cameraView.release()
    }
    
    companion object {
        private const val TAG = "OpenglActivity"
    }
}