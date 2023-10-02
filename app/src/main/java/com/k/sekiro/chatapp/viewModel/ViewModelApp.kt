package com.k.sekiro.chatapp.viewModel

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.k.sekiro.chatapp.data.AddFriendList
import com.k.sekiro.chatapp.data.FriendRequest
import com.k.sekiro.chatapp.data.FriendRequestDisplay
import com.k.sekiro.chatapp.data.Message
import com.k.sekiro.chatapp.data.PushNotification
import com.k.sekiro.chatapp.data.User
import com.k.sekiro.chatapp.data.UserMsgPageData
import com.k.sekiro.chatapp.notificationApi.NotificationApi
import com.k.sekiro.chatapp.repository.UserAuth
import com.k.sekiro.chatapp.repository.userInfoRep.UserInfoRep
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject


@HiltViewModel
class ViewModelApp @Inject constructor(
    val repositoryLUserAuthImpl: UserAuth,
    val userInfoRepo:UserInfoRep,
    val api:NotificationApi
):ViewModel() {




       private val signInGoogleMutableLiveData = MutableLiveData<Intent>()
        val signInGoogleLiveData:LiveData<Intent> = signInGoogleMutableLiveData


        private val userDataMutableLiveData = MutableLiveData<User>()
        val userDataLiveData:LiveData<User> = userDataMutableLiveData


         private val userContactsMutableLiveData = MutableLiveData<UserMsgPageData>()
         val userContactsLiveData:LiveData<UserMsgPageData> = userContactsMutableLiveData


         private val userMessagesMutableLiveData = MutableLiveData<ArrayList<Message>>()
         val userMessagesLiveData:LiveData<ArrayList<Message>> = userMessagesMutableLiveData


        private val userStatusMutableLiveData = MutableLiveData<Boolean>()
        val userStatusLiveData:LiveData<Boolean> = userStatusMutableLiveData


        private val addFriendListMutableLiveData = MutableLiveData<ArrayList<AddFriendList>>()
        val addFriendListLiveData:LiveData<ArrayList<AddFriendList>> = addFriendListMutableLiveData


       // private val checkIfUserSentAddRequestMutableLiveData = MutableLiveData<String>()
       // val checkIfUserSentAddRequestLiveData:LiveData<String> = checkIfUserSentAddRequestMutableLiveData


        private val friendReqResultMutableLiveData = MutableLiveData<String>()
        val friendReqResultLiveData:LiveData<String> = friendReqResultMutableLiveData


        private val cancelResultMutableLiveData = MutableLiveData<Boolean>()
        val cancelResultLiveData:LiveData<Boolean> = cancelResultMutableLiveData


        private val friendRequestDisplayMutableLiveData = MutableLiveData<ArrayList<FriendRequestDisplay>>()
        val friendRequestDisplayLiveData:LiveData<ArrayList<FriendRequestDisplay>> = friendRequestDisplayMutableLiveData

        private val friendRequestCounterMutableLiveData = MutableLiveData<Int>()
        val friendRequestCounterLiveData:LiveData<Int> = friendRequestCounterMutableLiveData


        private val searchByIdMutableLiveData = MutableLiveData<Boolean>()
        val searchByIdLiveData:LiveData<Boolean> = searchByIdMutableLiveData

        private val multiImagesMutableLiveData = MutableLiveData<ArrayList<String>>()
        val multiImagesLiveData:LiveData<ArrayList<String>> = multiImagesMutableLiveData



    fun signInWithGoogle(){

        viewModelScope.launch {
           val  intent = async { repositoryLUserAuthImpl.signInWithGoogle() }.await()
            signInGoogleMutableLiveData.value = intent
        }

    }



    fun saveGoogleAccountDataToFireStore(user: User){
        repositoryLUserAuthImpl.saveGoogleAccountDataToFireStore(user)
    }


    fun getUserData(userId:String){
        val handlerException = CoroutineExceptionHandler{_,throwable ->
            Log.e("ks","${throwable.message!!}")
        }

        viewModelScope.launch(handlerException){
            try {
                 userInfoRepo.getUserData(userId).collect{
                     userDataMutableLiveData.value = it
                 }

            }catch (e:NullPointerException){
                Log.e("ks","${e}")
            }catch (e:Exception){
                Log.e("ks","${e}")
            }

        }
    }


   /* fun getUserContacts(user: User){
        viewModelScope.launch {
            val data = userInfoRepo.getUserContacts(user)
            userContactsMutableLiveData.value = data

        }
    }*/

    fun getUserContacts(user: User){
        viewModelScope.launch {
             userInfoRepo.getUserContacts2(user).collect{
                 userContactsMutableLiveData.value = it
             }


        }
    }









    fun sendMessage(message: Message){
        viewModelScope.launch {
            userInfoRepo.sendMessage(message)
        }
    }


    fun getMessages(senderId: String, receiverId: String){
        viewModelScope.launch {
            userInfoRepo.getMessages(senderId, receiverId).collect{
                userMessagesMutableLiveData.value = it
            }


        }
    }



    fun getUserStatusRealtime(userId: String){
        viewModelScope.launch {
            userInfoRepo.getUserStatusRealtime(userId).collect{
                userStatusMutableLiveData.value = it
            }
        }
    }



    fun getAddFriendList(userId:String){
        viewModelScope.launch {
            userInfoRepo.getAddFriendList(userId).collect{
                addFriendListMutableLiveData.value = it
            }
        }
    }


    fun checkIfUserSentAddRequest(senderId: String, receiverId: String):Flow<String>{

        return userInfoRepo.checkIfUserSentAddRequest(senderId, receiverId)

    }


    /*suspend fun checkIfUserSentAddRequest(senderId: String, receiverId: String): Flow<String> {

        return  userInfoRepo.checkIfUserSentAddRequest(senderId, receiverId)
        //   checkIfUserSentAddRequestMutableLiveData.value = it
    }*/


    fun sendFriendRequest(request: FriendRequest){
        viewModelScope.launch {
            userInfoRepo.sendFriendRequest(request).collect{
                friendReqResultMutableLiveData.value = it
            }
        }
    }


    fun cancelFriendRequest(requestId:String){
        viewModelScope.launch {
            userInfoRepo.cancelFriendRequest(requestId).collect{
                cancelResultMutableLiveData.value = it
            }
        }

    }


    fun sendNotification(notification: PushNotification){
        Log.e("ks","inside sendNotification fun")
        viewModelScope.launch {
            try {
                Log.e("ks","inside sendNotification fun")
                val response =  api.postNotification(notification)
                if (response.isSuccessful){
                     //   Log.e("ks","${Gson().toJson(response)}")
                }
            }catch (e:Exception){
                Log.e("ks","${e.message}")
            }

        }
    }



    fun getAllFriendRequests(receiverId: String){
        viewModelScope.launch {
            userInfoRepo.getAllFriendRequests(receiverId).collect{
                friendRequestDisplayMutableLiveData.value = it
            }
        }
    }




    fun sendImageMsg(uri: Uri, contentResolver: ContentResolver, sender:String, friendId:String, senderImg:String){
        viewModelScope.launch {
            userInfoRepo.sendImageMsg(uri, contentResolver, sender, friendId, senderImg).collect{
                sendMessage(it)
            }
        }
    }



    fun sendAudioMsg(uri:Uri,sender:String, friendId:String, senderImg:String){
        viewModelScope.launch {

            userInfoRepo.sendAudioMsg(uri).collect{
                val msg = Message(sender,friendId,it.toString(), Date(),"audio",senderImg)
                sendMessage(msg)
            }
        }

    }



    fun addNewFriend(userId: String, friendId: String) {
        viewModelScope.launch {
            userInfoRepo.addNewFriend(userId, friendId)
        }
    }


    fun deleteFriendRequest(requestId: String) {
        viewModelScope.launch{
            userInfoRepo.deleteFriendRequest(requestId)
        }
    }

    fun getFriendReqCount(receiverId: String){
        viewModelScope.launch {
            userInfoRepo.getFriendReqCount(receiverId).collect{
                friendRequestCounterMutableLiveData.value = it
            }
        }
    }




    fun searchById(senderId:String,tagId:String){
        viewModelScope.launch {
            userInfoRepo.searchById(senderId, tagId).collect{
                searchByIdMutableLiveData.value = it
            }
        }
    }



    fun sendMultiImages(list: List<Uri>, directory: String, contentResolver: ContentResolver, sender: String, friendId: String, senderImg: String){

        viewModelScope.launch {
            userInfoRepo.sendMultiImages(list, directory, contentResolver, sender, friendId, senderImg).collect{
                multiImagesMutableLiveData.value = it
            }
        }

    }



    fun deletePlaceholderImages(date: Date){
        viewModelScope.launch {
            userInfoRepo.deletePlaceHolderImages(date)
        }
    }
   /* class ViewModelFactory(var repository: UserAuth):ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ViewModelApp(repository) as T
        }
    }*/

    override fun onCleared() {
        super.onCleared()
    }

}