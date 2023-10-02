package com.k.sekiro.chatapp.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.k.sekiro.chatapp.R
import com.k.sekiro.chatapp.database.DatabaseReferences
import com.k.sekiro.chatapp.ui.activities.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.random.Random

const val Channel_Id = "my_channel"
@AndroidEntryPoint
class FirebaseService:FirebaseMessagingService() {
    @Inject lateinit var sharedPref:SharedPreferences

    @Inject lateinit var db:FirebaseFirestore


    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification_Id = Random.nextInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }


        Log.e("ks","<<<<<<<<<<<<<<<<   ${message.data.toString()}  >>>>>>>>>>>>>>>>")
        Log.e("ks","<<<<<<<<<<<<<<<<   ${message.notification?.title.toString()}  >>>>>>>>>>>>>>>>")


        val pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, Channel_Id)
            .setContentTitle("${message.notification!!.title}")
            .setContentText("${message.notification!!.body}")
            .setSmallIcon(R.drawable.chatapp)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()


            notificationManager.notify(notification_Id,notification)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        sharedPref.edit().putString("token",token).apply()
        val userId = sharedPref.getString("userId","")!!
        Log.e("ks","the new token after update $token")
        try {
            db.collection(DatabaseReferences.UsersCollection).document(userId)
                .update("token",token)
        }catch (e:IllegalArgumentException){

            Handler(Looper.getMainLooper()).post {
                Toast.makeText(this,"${e.message}",Toast.LENGTH_SHORT).show()
            }


        } catch (e:NullPointerException){
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(this,"${e.message}",Toast.LENGTH_SHORT).show()
            }


        }catch (e:Exception){
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(this,"${e.message}",Toast.LENGTH_SHORT).show()
            }

        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channelName = "AppChannel"
        val channel = NotificationChannel(Channel_Id,channelName,NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "this channel for whole app"
                enableLights(true)
            lightColor = Color.WHITE
             }
            notificationManager.createNotificationChannel(channel)
        }


}