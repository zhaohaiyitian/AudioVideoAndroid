package com.jack.audiovideoandroid

import android.content.Context
import android.util.Log
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException

/**
 * author：jie.wang
 * desc:
 */
fun writeBytes(context: Context,array: ByteArray?) {
    var writer: FileOutputStream? = null
    try {

        // "${externalCacheDir?.absolutePath}/screen_record_${System.currentTimeMillis()}.mp4"
        // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
        writer = FileOutputStream(context.externalCacheDir?.absolutePath + "/codec.h264",
            true
        )
        writer.write(array)
        writer.write('\n'.code)
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        try {
            writer?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

fun writeContent(context: Context,array: ByteArray): String {
    val HEX_CHAR_TABLE = charArrayOf(
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    )
    val sb = StringBuilder()
    for (b in array) {
        sb.append(HEX_CHAR_TABLE[(b.toInt() and 0xf0) shr 4])
        sb.append(HEX_CHAR_TABLE[b.toInt() and 0x0f])
    }
    Log.i("wangjie", "writeContent: $sb")
    var writer: FileWriter? = null
    try {
        // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
        writer = FileWriter(
            context.externalCacheDir?.absolutePath + "/codecH264.txt",
            true
        )
        writer!!.write(sb.toString())
        writer!!.write("\n")
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        try {
            if (writer != null) {
                writer!!.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return sb.toString()
}