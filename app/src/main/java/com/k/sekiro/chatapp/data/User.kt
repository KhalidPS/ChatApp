package com.k.sekiro.chatapp.data

import com.google.firebase.firestore.DocumentId

data class User (@DocumentId var id:String,var name:String,var email:String,var password:String,var phoneNumber:String,var status:Boolean = false, var img:String = "",var appId:String = "",val friends:ArrayList<String>?,var accountType:String = "private",val token:String = ""){
    constructor():this("","","","","",false,"","",null)
}