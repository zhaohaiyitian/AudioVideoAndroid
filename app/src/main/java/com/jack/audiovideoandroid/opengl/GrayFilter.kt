package com.jack.audiovideoandroid.opengl

import android.content.Context
import com.jack.audiovideoandroid.R

/**
 * 灰色滤镜
 */
class GrayFilter(context: Context) : ScreenFilter(context) {
    
    init {
        // 使用灰色滤镜的片段着色器
        val vert = readRawTextFile(context, R.raw.camera_vert)
        val frag = readRawTextFile(context, R.raw.gray_filter_frag)
        
        // 创建并编译顶点着色器
        val vShader = createShader(SHADER_TYPE_VERTEX, vert)
        
        // 创建并编译片段着色器
        val fShader = createShader(SHADER_TYPE_FRAGMENT, frag)
        
        // 创建并链接程序
        program = createAndLinkProgram(vShader, fShader)
    }
    
    companion object {
        const val SHADER_TYPE_VERTEX = 0
        const val SHADER_TYPE_FRAGMENT = 1
    }
}