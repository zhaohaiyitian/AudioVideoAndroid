package com.jack.audiovideoandroid.opengl

import android.content.Context

/**
 * 滤镜管理器
 */
class FilterManager(private val context: Context) {
    
    companion object {
        const val FILTER_NORMAL = 0 // 正常滤镜
        const val FILTER_GRAY = 1   // 灰度滤镜
    }
    
    // 当前滤镜
    private var currentFilter: ScreenFilter? = null
    
    // 当前滤镜类型
    private var currentFilterType = FILTER_NORMAL
    
    /**
     * 获取滤镜
     */
    fun getFilter(): ScreenFilter {
        if (currentFilter == null) {
            currentFilter = createFilter(currentFilterType)
        }
        return currentFilter!!
    }
    
    /**
     * 切换到下一个滤镜
     */
    fun switchToNextFilter() {
        // 切换滤镜类型
        currentFilterType = when (currentFilterType) {
            FILTER_NORMAL -> FILTER_GRAY
            FILTER_GRAY -> FILTER_NORMAL
            else -> FILTER_NORMAL
        }
        
        // 释放当前滤镜资源
        currentFilter?.let {
            // 如果需要释放资源，可以在这里添加释放代码
        }
        
        // 创建新滤镜
        currentFilter = createFilter(currentFilterType)
    }
    
    /**
     * 根据类型创建滤镜
     */
    private fun createFilter(type: Int): ScreenFilter {
        return when (type) {
            FILTER_NORMAL -> ScreenFilter(context)
            FILTER_GRAY -> GrayFilter(context)
            else -> ScreenFilter(context)
        }
    }
    
    /**
     * 释放资源
     */
    fun release() {
        currentFilter = null
    }
}