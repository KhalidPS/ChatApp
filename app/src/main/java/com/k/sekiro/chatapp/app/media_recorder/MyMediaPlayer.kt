package com.k.sekiro.chatapp.app.media_recorder

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import androidx.core.net.toUri
import java.io.File

class MyMediaPlayer(val context: Context) {

    private var player:MediaPlayer? = null

    fun start(uri:Uri){
        MediaPlayer.create(context,uri).apply {
            player = this
            start()
        }
    }

    fun resume(){
        if (player != null){
            player?.start()
        }
    }



    fun pause(){
        player?.pause()
       // player?.release()
       // player = null
    }

    fun getPlayerValue():MediaPlayer?{
        return this.player
    }





    fun getPlayerDuration() = player?.duration

}