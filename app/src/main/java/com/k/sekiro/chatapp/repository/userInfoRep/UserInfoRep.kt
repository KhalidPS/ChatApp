package com.k.sekiro.chatapp.repository.userInfoRep

import android.content.ContentResolver
import android.net.Uri
import com.k.sekiro.chatapp.data.AddFriendList
import com.k.sekiro.chatapp.data.FriendRequest
import com.k.sekiro.chatapp.data.FriendRequestDisplay
import com.k.sekiro.chatapp.data.Message
import com.k.sekiro.chatapp.data.User
import com.k.sekiro.chatapp.data.UserMsgPageData
import com.k.sekiro.chatapp.database.DatabaseReferences
import kotlinx.coroutines.flow.Flow
import java.util.Date


interface UserInfoRep {


   suspend fun getUserData(userId:String):Flow<User>

   suspend fun getUserContacts(user:User):UserMsgPageData

   suspend fun getUserContacts2(user: User):Flow<UserMsgPageData>

   suspend fun sendMessage(message: Message)

   suspend fun getMessages(senderId:String, receiverId:String):Flow<ArrayList<Message>>

   suspend fun getUserStatusRealtime(userId:String):Flow<Boolean>

   suspend fun getAddFriendList(userId:String):Flow<ArrayList<AddFriendList>>

    fun checkIfUserSentAddRequest(senderId: String,receiverId: String):Flow<String>

   suspend fun sendFriendRequest(request:FriendRequest):Flow<String>

   suspend fun cancelFriendRequest(requestId:String):Flow<Boolean>

   suspend fun sendImageMsg(uri: Uri, contentResolver: ContentResolver, sender:String, friendId:String, senderImg:String):Flow<Message>

   suspend fun sendAudioMsg(uri:Uri):Flow<Uri>

   suspend fun getAllFriendRequests(receiverId: String):Flow<ArrayList<FriendRequestDisplay>>

   suspend fun getFriendReqCount(receiverId: String):Flow<Int>

   suspend fun addNewFriend(userId: String,friendId:String)

   suspend fun deleteFriendRequest(requestId: String)

   suspend fun searchById(senderId:String,tagId:String):Flow<Boolean>

   suspend fun downloadImage(url:String):Flow<Boolean>

   suspend fun sendMultiImages(list: List<Uri>,directory:String,contentResolver:ContentResolver,sender:String, friendId:String ,senderImg:String):Flow<ArrayList<String>>

   suspend fun deletePlaceHolderImages(date:Date)

   }

