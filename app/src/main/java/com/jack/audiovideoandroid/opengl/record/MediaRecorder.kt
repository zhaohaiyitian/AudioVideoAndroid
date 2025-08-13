package com.jack.audiovideoandroid.opengl.record

import android.content.Context
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.opengl.EGLContext
import android.os.Handler
import android.os.HandlerThread

/**
 * author：jie.wang
 * desc:
 */
class MediaRecorder(
    context: Context,
    val path: String,
    var width: Int,
    var height: Int,
    eglContext: EGLContext
) {


    fun start(speed: Float) {
        val mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height)
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE,1500_000) // 码率
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 20) // 帧率
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 20) // I帧间隔
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)

        val mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
        mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)

        val inputSurface = mediaCodec.createInputSurface()
        val mediaMuxer = MediaMuxer(path, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
        val handlerThread = HandlerThread("VideoCodec")
        handlerThread.start()
        val looper = handlerThread.looper
        val handler = Handler(looper)
        handler.post {

            mediaCodec.start()

        }

    }

}