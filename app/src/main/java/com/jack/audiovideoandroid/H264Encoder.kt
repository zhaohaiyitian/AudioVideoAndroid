package com.jack.audiovideoandroid

import android.content.Context
import android.hardware.display.DisplayManager
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.media.projection.MediaProjection
import android.os.FileUtils

class H264Encoder(var context: Context,var mediaProjection: MediaProjection): Thread() {

    private lateinit var mediaCodec: MediaCodec
    private var width = 640
    private var height = 1920

    init {
        mediaCodec = MediaCodec.createEncoderByType("video/avc")
        val mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC,width,height)
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE,20) // 帧率
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL,30) // I 帧/关键帧间隔  单位是秒
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE,width * height) // 比特率/码率   每秒的数据量
        // COLOR_FormatSurface 不是真实的颜色格式，而是一个标志位
        // 它告诉 MediaCodec："输入数据将通过 Surface 直接传递，而非传统的 ByteBuffer"
        // 避免 CPU 参与数据拷贝，直接使用 GPU 到编码器的专用通道
        // 系统自动处理 RGBA/YUV 转换， 通常使用 GPU 着色器进行高效转换
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT,MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
        // 获取输入Surface（必须在configure之后调用！）
        mediaCodec.configure(mediaFormat,null,null, MediaCodec.CONFIGURE_FLAG_ENCODE)

        val surface = mediaCodec.createInputSurface()
        // 创建一个虚拟的显示器，将当前屏幕（或指定应用）的画面内容实时投射到该显示器，并通过绑定的 Surface 输出数据。
        mediaProjection.createVirtualDisplay("jack", width, height, 2, DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC, surface, null, null)
    }








    override fun run() {
        super.run()
        mediaCodec.start()
        val info = MediaCodec.BufferInfo()
        while (true) {
            val outIndex = mediaCodec.dequeueOutputBuffer(info, 10000)
            if (outIndex >= 0) {
                val byteBuffer = mediaCodec.getOutputBuffer(outIndex)
                // ByteArray 易造成内存抖动，待优化
                val ba = ByteArray(byteBuffer?.remaining()?:0) // 获取当前缓冲区剩余的可读字节数
                byteBuffer?.get(ba) // 将数据从 ByteBuffer 读取到 ba 中
                writeBytes(context,ba)
                writeContent(context,ba)
                mediaCodec.releaseOutputBuffer(outIndex,false)
            }
        }

    }
}