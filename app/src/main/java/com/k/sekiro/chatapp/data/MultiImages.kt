package com.k.sekiro.chatapp.data

import java.util.Date

 class MultiImages(senderId: String,  receiverId: String,  message: String ,  time:Date,  type:String,  senderImg:String = "", val images:ArrayList<String>?):Message(senderId, receiverId, message, time, type, senderImg){
     constructor():this("","","",Date(),"","",null)
 }