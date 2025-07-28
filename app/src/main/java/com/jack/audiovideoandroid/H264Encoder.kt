package com.jack.audiovideoandroid

import android.hardware.display.DisplayManager
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.projection.MediaProjection

class H264Encoder(var mediaProjection: MediaProjection) {

    private lateinit var mediaCodec: MediaCodec
    private var width = 640
    private var height = 1920

    init {
        mediaCodec = MediaCodec.createEncoderByType("video/avc")
        val mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC,width,height)
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE,20)
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL,30)
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE,width * height)
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT,MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)

        mediaCodec.configure(mediaFormat,null,null, MediaCodec.CONFIGURE_FLAG_ENCODE)

        val surface = mediaCodec.createInputSurface()
        mediaProjection.createVirtualDisplay("jack", width, height, 2, DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC, surface, null, null)
    }












    fun start() {

    }
}