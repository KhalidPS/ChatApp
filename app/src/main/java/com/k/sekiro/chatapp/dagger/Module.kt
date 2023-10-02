package com.k.sekiro.chatapp.dagger

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.k.sekiro.chatapp.notificationApi.NotificationApi
import com.k.sekiro.chatapp.repository.UserAuth
import com.k.sekiro.chatapp.repository.UserAuthImpl
import com.k.sekiro.chatapp.repository.userInfoRep.UserInfoRep
import com.k.sekiro.chatapp.repository.userInfoRep.UserInfoRepImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class Module {

    private val baseUrl = "https://fcm.googleapis.com/v1/projects/"

    @Provides
    @Singleton
    fun provideFireStoreInstance():FirebaseFirestore{
        return Firebase.firestore
    }

    @Provides
    @Singleton
    fun provideAuthInstance():FirebaseAuth{
        return Firebase.auth
    }


   /* @Provides
    @Singleton
    fun provideSignInClient(@ApplicationContext context: Context):SignInClient{
        return Identity.getSignInClient(context)
    }
*/

    @Provides
    @Singleton
    fun provideUserAuthRepository(@ApplicationContext context: Context,db:FirebaseFirestore,auth: FirebaseAuth):UserAuth{
        return UserAuthImpl(context,db,auth)
    }


    @Provides
    @Singleton
    fun providerUserSharedPreferences(@ApplicationContext context: Context):SharedPreferences{
        return context.getSharedPreferences("user",Context.MODE_PRIVATE)
    }


    @Provides
    @Singleton
    fun providerUserInfoRepo(@ApplicationContext context: Context, db:FirebaseFirestore,storage: FirebaseStorage):UserInfoRep{
        return UserInfoRepImpl(context,db,storage)
    }


    @Provides
    @Singleton
    fun provideNotificationApi():NotificationApi{
        return Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(NotificationApi::class.java)
    }


    @Provides
    @Singleton
    fun provideFireStorageInstance():FirebaseStorage{
        return Firebase.storage
    }





}