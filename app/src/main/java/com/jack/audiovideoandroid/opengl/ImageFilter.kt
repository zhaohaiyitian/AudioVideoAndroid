package com.jack.audiovideoandroid.opengl

import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLUtils
import android.util.Log
import com.jack.audiovideoandroid.R
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class ImageFilter(context: Context) {

    // 世界坐标系
    var VERTEX = floatArrayOf(
        -1.0f, -1.0f,
        1.0f, -1.0f,
        -1.0f, 1.0f,
        1.0f, 1.0f
    )

    // 纹理坐标系
    var TEXTURE = floatArrayOf(
        0.0f, 1.0f, // 左下
        1.0f, 1.0f, // 右下
        0.0f, 0.0f, // 左上
        1.0f, 0.0f  // 右上
    )

    var program: Int
    var positionBuffer: FloatBuffer
    var coordBuffer: FloatBuffer // 纹理坐标

    var vPosition = 0
    var vCoord = 0
    var inputTexture = 0

    init {
        Log.d("wangjie","init....")
        val vert = readRawTextFile(context, R.raw.image_vert)
        // 创建顶点程序
        val vShader = createShader(SHADER_TYPE_VERTEX, vert)

        val frag = readRawTextFile(context, R.raw.image_frag)
        // 创建片段程序
        val fragShader = createShader(SHADER_TYPE_FRAGMENT, frag)

        // 创建并链接程序
        program = createAndLinkProgram(vShader, fragShader)

        // 实际分配的是主内存 它是一种特殊的内存，称为DMA内存，GPU可以直接从该内存中读取数据，不需要CPU参与   设置字节序order(ByteOrder.nativeOrder())
        // 这个内存与VBO内存不同，VBO内存才是GPU中的内存
        positionBuffer = ByteBuffer.allocateDirect(VERTEX.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        positionBuffer.clear()
        positionBuffer.put(VERTEX)

        coordBuffer = ByteBuffer.allocateDirect(TEXTURE.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        coordBuffer.clear()
        coordBuffer.put(TEXTURE)
    }


    fun initTexture(bitmap: Bitmap): Int {
        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textures[0])

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST.toFloat())
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR.toFloat())
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE.toFloat())
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE.toFloat())

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        return textures[0]
    }


    fun drawFrame(textures: Int) {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glUseProgram(program)

        // 读取指针拨回到最开始的位置
        positionBuffer.position(0)
        coordBuffer.position(0)

        //定位到GPU的变量地址
        vPosition = GLES20.glGetAttribLocation(program,"vPosition")
        vCoord = GLES20.glGetAttribLocation(program,"vCoord")

        inputTexture = GLES20.glGetUniformLocation(program,"inputImageTexture")


        // 这两行代码必须成对出现。只有先详细地描述了数据，然后才能启用它
        GLES20.glVertexAttribPointer(vPosition,2,GLES20.GL_FLOAT,false,0,positionBuffer)// 这行代码是描述性的
//        GLES20.glVertexAttribPointer(vPosition,2,GLES20.GL_FLOAT,false,0,0)// 这行代码是描述性的 offset设置为0表示从gpu中读取数据
        GLES20.glEnableVertexAttribArray(vPosition) // 这行代码是执行性的

        GLES20.glVertexAttribPointer(vCoord,2,GLES20.GL_FLOAT,false,0,coordBuffer)
        GLES20.glEnableVertexAttribArray(vCoord)

        // 激活0号纹理单元
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textures)
        // 告诉片元着色器里的vTexture这个采样器（sampler2D），让它去0号纹理单元采样颜色
        GLES20.glUniform1i(inputTexture,0)

        // 通知GPU渲染
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4)

        // 释放资源
        GLES20.glDisableVertexAttribArray(vPosition)
        GLES20.glDisableVertexAttribArray(vCoord)

        // 把第0图层解绑
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
    }


    private fun readRawTextFile(context: Context, rawId: Int): String {
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
    private fun createShader(type: Int, shaderCode: String): Int {
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
    private fun createAndLinkProgram(vertexShader: Int, fragmentShader: Int): Int {
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