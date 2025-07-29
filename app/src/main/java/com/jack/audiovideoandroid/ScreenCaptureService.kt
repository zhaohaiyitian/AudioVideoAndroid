package com.jack.audiovideoandroid
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.NotificationCompat
import java.io.FileDescriptor

class ScreenCaptureService : Service() {

    private lateinit var mediaProjection: MediaProjection
    private lateinit var virtualDisplay: VirtualDisplay
    private lateinit var mediaRecorder: MediaRecorder

    private var currentVideoUri: Uri? = null

    companion object {
        const val RESULT_CODE = "resultCode"
        const val RESULT_DATA = "resultData"
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "screen_record_channel"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val resultCode = intent?.getIntExtra(RESULT_CODE, 0) ?: 0
        val resultData = intent?.getParcelableExtra<Intent>(RESULT_DATA)

        if (resultCode != 0 && resultData != null) {
            startRecording(resultCode, resultData)
        }

        return START_STICKY
    }

    private fun startRecording(resultCode: Int, resultData: Intent) {
        // 1. 创建通知并启动前台服务
        startForeground(NOTIFICATION_ID, createNotification())

        // 2. 初始化媒体投影
        val projectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mediaProjection = projectionManager.getMediaProjection(resultCode, resultData)
        val encoder = H264Encoder(applicationContext,mediaProjection)
        encoder.start()
//        val fileName = "ScreenRecord_${System.currentTimeMillis()}.mp4"
//        // 3. 初始化MediaRecorder
//        mediaRecorder = MediaRecorder().apply {
//            setVideoSource(MediaRecorder.VideoSource.SURFACE)
//            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
//            setOutputFile(getOutputFileDescriptor(applicationContext,fileName))
//            setVideoEncoder(MediaRecorder.VideoEncoder.H264)
//            setVideoSize(1080, 1920) // 根据实际需求调整
//            setVideoFrameRate(30)
//            prepare()
//        }
//
//        // 4. 创建VirtualDisplay
//        virtualDisplay = mediaProjection.createVirtualDisplay(
//            "ScreenRecorder",
//            1080, 1920, resources.displayMetrics.densityDpi,
//            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
//            mediaRecorder.surface, null, null
//        )
//
//        // 5. 开始录制
//        mediaRecorder.start()
    }

    private fun createNotification(): Notification {
        createNotificationChannel()

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("屏幕录制中")
            .setContentText("正在录制您的屏幕操作")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "屏幕录制",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "屏幕录制服务正在运行"
            }

            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
    }

    private fun getOutputFileDescriptor(context: Context, fileName: String): FileDescriptor? {
        // ... (ContentValues 的创建和 resolver 的获取不变) ...
        val contentValues = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/")
                put(MediaStore.Video.Media.IS_PENDING, 1)
            }
        }

        val collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val itemUri = context.contentResolver.insert(collection, contentValues)

        // 将创建的 Uri 保存到成员变量中，以便稍后更新
        currentVideoUri = itemUri

        return if (itemUri != null) {
            context.contentResolver.openFileDescriptor(itemUri, "w")?.fileDescriptor
        } else {
            null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRecording()
    }

    private fun stopRecording() {
        mediaRecorder.apply {
            stop()
            reset()
            release()
        }

        virtualDisplay.release()

        if (::mediaProjection.isInitialized) {
            mediaProjection.stop()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && currentVideoUri != null) {
            val contentValues = ContentValues().apply {
                put(MediaStore.Video.Media.IS_PENDING, 0)
            }
            contentResolver.update(currentVideoUri!!, contentValues, null, null)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}