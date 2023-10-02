package com.k.sekiro.chatapp.data

import java.util.Date

   open class Message(val senderId: String, val receiverId: String, val message: String, val time:Date, val type:String, val senderImg:String = ""){
        constructor():this("","","",Date(),"")
}