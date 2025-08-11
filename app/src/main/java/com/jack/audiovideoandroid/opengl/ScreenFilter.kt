package com.jack.audiovideoandroid.opengl

import android.content.Context
import android.opengl.GLES20
import android.util.Log
import com.jack.audiovideoandroid.R
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

open class ScreenFilter(context: Context) {

    // 世界坐标系
    var VERTEX = floatArrayOf(
        -1.0f, -1.0f,
        1.0f, -1.0f,
        -1.0f, 1.0f,
        1.0f, 1.0f
    )

    // 纹理坐标系
    var TEXTURE = floatArrayOf(
        0.0f, 0.0f,
        1.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 1.0f
    )

    var program: Int
    var vertexBuffer: FloatBuffer
    var textureBuffer: FloatBuffer // 纹理坐标

     var vPosition = 0
     var vCoord = 0

     var vTexture = 0
     var vMatrix = 0

    var vboIds = IntArray(1)


    init {
        val vert = readRawTextFile(context, R.raw.camera_vert)
        // 创建顶点程序
        val vShader = createShader(SHADER_TYPE_VERTEX, vert)
        
        val frag = readRawTextFile(context, R.raw.camera_frag)
        // 创建片段程序
        val fragShader = createShader(SHADER_TYPE_FRAGMENT, frag)
        
        // 创建并链接程序
        program = createAndLinkProgram(vShader, fragShader)

        // 实际分配的是主内存 它是一种特殊的内存，称为DMA内存，GPU可以直接从该内存中读取数据，不需要CPU参与   设置字节序order(ByteOrder.nativeOrder())
        // 这个内存与VBO内存不同，VBO内存才是GPU中的内存
        vertexBuffer = ByteBuffer.allocateDirect(4 * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        vertexBuffer.clear()
        vertexBuffer.put(VERTEX)

        textureBuffer = ByteBuffer.allocateDirect(4 * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        textureBuffer.clear()
        textureBuffer.put(TEXTURE)

        // 将数据先拷贝到GPU中
//        GLES20.glGenBuffers(1,vboIds,0)
//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,vboIds[0])
//        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,vertexBuffer.capacity(),vertexBuffer,GLES20.GL_STATIC_DRAW)
//
//        // 解绑vbo
//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,0)
    }

    fun onDraw(width: Int, height: Int,mtx:FloatArray,textures: Int) {
        GLES20.glViewport(0,0,width,height)
        GLES20.glUseProgram(program)

//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,vboIds[0])

        // 读取指针拨回到最开始的位置
        vertexBuffer.position(0)
        textureBuffer.position(0)

        //定位到GPU的变量地址
        vPosition = GLES20.glGetAttribLocation(program,"vPosition")
        vCoord = GLES20.glGetAttribLocation(program,"vCoord")
        vTexture = GLES20.glGetUniformLocation(program, "vTexture")
        vMatrix = GLES20.glGetUniformLocation(program, "vMatrix")

        // 这两行代码必须成对出现。只有先详细地描述了数据，然后才能启用它
        GLES20.glVertexAttribPointer(vPosition,2,GLES20.GL_FLOAT,false,0,vertexBuffer)// 这行代码是描述性的
//        GLES20.glVertexAttribPointer(vPosition,2,GLES20.GL_FLOAT,false,0,0)// 这行代码是描述性的 offset设置为0表示从gpu中读取数据
        GLES20.glEnableVertexAttribArray(vPosition) // 这行代码是执行性的

        GLES20.glVertexAttribPointer(vCoord,2,GLES20.GL_FLOAT,false,0,textureBuffer)
        GLES20.glEnableVertexAttribArray(vCoord)

        // 激活0号纹理单元
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        // 把摄像头画面（身份证号为textures的那个纹理）绑定到当前激活的0号纹理单元上。”
        GLES20.glBindTexture(GLES20.GL_TEXTURE0,textures)

        // 告诉片元着色器里的vTexture这个采样器（sampler2D），让它去0号纹理单元采样颜色
        GLES20.glUniform1i(vTexture,0)
        // 把CPU内存里的这个mtx矩阵的值，传递给你顶点着色器里的vMatrix这个uniform变量
        GLES20.glUniformMatrix4fv(vMatrix,1,false,mtx,0)

        // 通知GPU渲染
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4)

//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,0)

        // 释放资源
        GLES20.glDisableVertexAttribArray(vPosition)
        GLES20.glDisableVertexAttribArray(vCoord)
    }

    fun readRawTextFile(context: Context, rawId: Int): String {
        val inputStream = context.resources.openRawResource(rawId)
        val br = BufferedReader(InputStreamReader(inputStream))
        var line: String?
        val sb = StringBuilder()
        try {
            while ((br.readLine().also { line = it }) != null) {
                sb.append(line)
                sb.append("\n")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            br.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return sb.toString()
    }
    
    /**
     * 创建着色器
     */
    protected fun createShader(type: Int, shaderCode: String): Int {
        // 创建Shader对象
        val shader = when (type) {
            SHADER_TYPE_VERTEX -> GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)
            SHADER_TYPE_FRAGMENT -> GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)
            else -> throw IllegalArgumentException("Unknown shader type")
        }
        
        // 加载着色器源码
        GLES20.glShaderSource(shader, shaderCode) // 指定Shader源码
        // 编译着色器
        GLES20.glCompileShader(shader)
        
        // 检查编译状态
        val compiled = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)
        if (compiled[0] == 0) {
            val info = GLES20.glGetShaderInfoLog(shader)
            Log.e("OpenGL", "Could not compile shader $type: $info")
            GLES20.glDeleteShader(shader)
            return 0
        }
        
        return shader
    }
    
    /**
     * 创建并链接程序
     */
    protected fun createAndLinkProgram(vertexShader: Int, fragmentShader: Int): Int {
        // 创建程序
        val program = GLES20.glCreateProgram()
        if (program == 0) {
            Log.e("OpenGL", "Could not create program")
            return 0
        }
        
        // 绑定Shader到OpenGL程序
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        
        // 链接OpenGL程序
        GLES20.glLinkProgram(program)
        
        // 检查链接状态
        val linked = IntArray(1)
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linked, 0)
        if (linked[0] == 0) {
            val info = GLES20.glGetProgramInfoLog(program)
            Log.e("OpenGL", "Could not link program: $info")
            GLES20.glDeleteProgram(program)
            return 0
        }
        
        return program
    }
    
    companion object {
        const val SHADER_TYPE_VERTEX = 0
        const val SHADER_TYPE_FRAGMENT = 1
    }
}