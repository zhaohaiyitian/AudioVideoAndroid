package com.jack.audiovideoandroid

import android.content.Context
import android.media.MediaCodec
import android.media.MediaFormat
import android.util.Log
import android.view.Surface
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException

/**
 * author：jie.wang
 * desc:
 */
class H264Player(private var surface: Surface,private var context: Context) : Runnable {

    private var mediaCodec: MediaCodec? = null
    init {
        mediaCodec = MediaCodec.createDecoderByType("video/avc") // 创建一个解码器实例。"video/avc" 是 H.264 视频格式的MIME类型标准名称。
        val mediaFormat = MediaFormat.createVideoFormat("video/avc", 640, 1920)
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE,15) // 视频的帧率为15fps
        mediaCodec?.configure(mediaFormat,surface,null,0)  // 0: 标志位，0表示配置为解码器
    }

    fun play() {
        mediaCodec?.start() // 启动 MediaCodec,解码器会进入执行状态，开始请求输入数据并产生输出数据
        Thread(this).start()
    }


    // 将解码工作放在这个后台线程中执行
    override fun run() {
        decodeH264()
    }

    private fun decodeH264() {
        var bytes: ByteArray? = null
        bytes = getBytesFromAssets("codec.h264")
        if (bytes == null) {
            return
        }
        val frameRate = 15 // 设置的帧率
        val frameDurationUs = 1_000_000L / frameRate
        // 帧序号计数器
        var frameIndex = 0L
        var isEndOfStream = false
        val info = MediaCodec.BufferInfo() // 创建BufferInfo 准备被填充
//        info.presentationTimeUs // 表示这一帧视频画面应该在什么时间点显示出来
//        info.flags // 这是控制解码流程的关键
//        info.size // 有多少字节是有效的解码数据
//        info.offset // 告诉你有效的解码数据是从缓冲区的哪个位置开始的
        var startIndex = 0
        // 无限循环来持续地解码帧
        while (!isEndOfStream) {
            //startIndex+2  在寻找下一帧的起始位置时，将搜索的起点向前移动一小段距离，以确保能够安全地越过（忽略）当前帧的起始码，从而避免找到自身而导致的无限循环
            val nextFrameStart = findByFrame(bytes,startIndex+2,bytes.size)
            if (nextFrameStart == -1) {
                isEndOfStream = true
            }
            // 获取可用的输入缓冲区
            val inIndex = mediaCodec?.dequeueInputBuffer(10000)?:0
            if (inIndex >= 0) {
                val byteBuffer = mediaCodec?.getInputBuffer(inIndex)
                val length = if (isEndOfStream) 0 else nextFrameStart - startIndex
                if (length > 0) {
                    byteBuffer?.put(bytes, startIndex, length)
                }
                val presentationTimeUs = frameIndex * frameDurationUs
                val flags = if (isEndOfStream) MediaCodec.BUFFER_FLAG_END_OF_STREAM else 0
                // 将一帧数据送入解码器
                mediaCodec?.queueInputBuffer(inIndex,0,length,presentationTimeUs,flags)
                // 更新下一帧的起始位置
                if (!isEndOfStream) {
                    startIndex = nextFrameStart
                    frameIndex++ // 递增帧序号
                }
            }

             // 获取解码后的输出缓冲区 系统自动填充info
            val outIndex = mediaCodec?.dequeueOutputBuffer(info,10000)?:-1
            if (outIndex >= 0) {
                // 检查是否是最后一帧
                if (info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                    break
                }
                // render = true 表示释放输出缓冲区并渲染到Surface上
                mediaCodec?.releaseOutputBuffer(outIndex,true)
            }
        }
        // 释放资源
        mediaCodec?.stop()
        mediaCodec?.release()
    }

    // 支持两种起始码格式 00 00 00 01 ， 00 00 01
    private fun findByFrame(bytes: ByteArray, start: Int, totalSize: Int): Int {
        for (i in start..(totalSize - 4)) {
            if (((bytes[i].toInt() == 0x00) && (bytes[i + 1].toInt() == 0x00) && (bytes[i + 2].toInt() == 0x00) && (bytes[i + 3].toInt() == 0x01))
                || ((bytes[i].toInt() == 0x00) && (bytes[i + 1].toInt() == 0x00) && (bytes[i + 2].toInt() == 0x01))
            ) {
                return i
            }
        }
        return -1
    }


    private fun getBytesFromAssets(fileName: String): ByteArray? {
        return try {
            val inputStream = context.assets.open(fileName)
            inputStream.use { stream ->
                val bos = ByteArrayOutputStream()
                val buf = ByteArray(4096)
                var len: Int
                while (stream.read(buf).also { len = it } != -1) {
                    bos.write(buf, 0, len)
                }
                bos.toByteArray()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}