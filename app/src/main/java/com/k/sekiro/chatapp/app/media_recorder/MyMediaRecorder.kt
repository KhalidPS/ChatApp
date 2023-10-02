package com.k.sekiro.chatapp.app.media_recorder

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class MyMediaRecorder(val context: Context){

    private var recorder:MediaRecorder? = null

    private fun createMediaRecorder():MediaRecorder{

       return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
             MediaRecorder(context)

        }else{
             MediaRecorder()
        }
    }

    fun start(outputFile:File){
        createMediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(FileOutputStream(outputFile).fd)
            prepare()
            start()

            recorder = this
        }
    }

    fun stop(){
        recorder?.stop()
        recorder?.reset()
        recorder = null
    }

}