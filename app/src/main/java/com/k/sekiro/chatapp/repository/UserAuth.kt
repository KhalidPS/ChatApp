package com.k.sekiro.chatapp.repository

import android.content.Intent
import com.k.sekiro.chatapp.data.User

interface UserAuth {

   suspend fun signInWithGoogle(): Intent

   fun saveGoogleAccountDataToFireStore(user: User)




}