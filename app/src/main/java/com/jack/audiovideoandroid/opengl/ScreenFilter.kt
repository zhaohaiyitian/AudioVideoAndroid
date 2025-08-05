package com.jack.audiovideoandroid.opengl

import android.content.Context
import android.opengl.GLES20
import com.jack.audiovideoandroid.R
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.FloatBuffer

class ScreenFilter(context: Context) {

    var VERTEX = floatArrayOf(
        -1.0f, -1.0f,
        1.0f, -1.0f,
        -1.0f, 1.0f,

        1.0f, 1.0f
    )

    //    输出坐标系
    var TEXTURE = floatArrayOf(
        0.0f, 0.0f,
        1.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 1.0f
    )

    lateinit var vertexBuffer: FloatBuffer
    lateinit var textureBuffer: FloatBuffer // 纹理坐标


    init {
        val vert = readRawTextFile(context, R.raw.camera_vert)
        // 创建顶点程序
        val vShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)
        GLES20.glShaderSource(vShader,vert)
        // 编译
        GLES20.glCompileShader(vShader)
        //查看配置 是否成功
        var status = IntArray(1)
        GLES20.glGetShaderiv(vShader,GLES20.GL_COMPILE_STATUS,status,0)

        val frag = readRawTextFile(context, R.raw.camera_frag)
        // 创建顶点程序
        val fragShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)
        GLES20.glShaderSource(fragShader,frag)
        // 编译
        GLES20.glCompileShader(fragShader)
        //查看配置 是否成功
        status = IntArray(1)
        GLES20.glGetShaderiv(fragShader,GLES20.GL_COMPILE_STATUS,status,0)

        // 创建总程序
        val program = GLES20.glCreateProgram()
        // 加载顶点程序
        GLES20.glAttachShader(program,vShader)
        // 加载片元程序
        GLES20.glAttachShader(program,fragShader)

        //链接着色器程序  gpu 激活状态
        GLES20.glLinkProgram(program)
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
}