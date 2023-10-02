package com.k.sekiro.chatapp.data

import com.google.firebase.firestore.DocumentId

data class FriendRequest (var senderId:String,var receiverId:String, var isAccepted:Boolean,@DocumentId var id:String = ""){
    constructor():this("","",false,"")
}