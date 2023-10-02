package com.k.sekiro.chatapp.repository

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.k.sekiro.chatapp.R
import com.k.sekiro.chatapp.data.User
import com.k.sekiro.chatapp.database.DatabaseReferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class UserAuthImpl @Inject constructor(@ApplicationContext var context: Context,var db:FirebaseFirestore,var auth:FirebaseAuth) : UserAuth {




    override suspend fun signInWithGoogle():Intent{
       val option = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
           .requestIdToken(context.getString(R.string.web_client_id))
           .requestEmail()
           .build()

        val googleSignIn = GoogleSignIn.getClient(context,option)
           return googleSignIn.signInIntent
    }


    override fun saveGoogleAccountDataToFireStore(user: User) {


        db.collection(DatabaseReferences.UsersCollection).add(user)
            .addOnSuccessListener {
                Toast.makeText(context,context.getString(R.string.saveGoogleToFireStoreSuccessMsg),Toast.LENGTH_SHORT).show()
                val sharedPref = context.getSharedPreferences("user",Context.MODE_PRIVATE)
                sharedPref.edit().putString("userId","${it.id}").commit()
            }
            .addOnFailureListener {
                Toast.makeText(context,"${it.message}",Toast.LENGTH_SHORT).show()
            }
    }




    /*
    override suspend fun signInWithGoogle(): IntentSender? {
        val result = try {
            oneTapClient.beginSignIn(
                buildSignINRequest()
            ).await()

        }catch (e:Exception){
            if (e is CancellationException) throw e
                null
        }

        return result?.pendingIntent?.intentSender
    }


    private fun buildSignINRequest():BeginSignInRequest{
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.web_client_id))
                    .build()
            ).build()
    }

     */






}