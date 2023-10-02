package com.k.sekiro.chatapp.repository.userInfoRep

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.local.QueryEngine
import com.google.firebase.storage.FirebaseStorage
import com.k.sekiro.chatapp.data.AddFriendList
import com.k.sekiro.chatapp.data.FriendRequest
import com.k.sekiro.chatapp.data.FriendRequestDisplay
import com.k.sekiro.chatapp.data.Message
import com.k.sekiro.chatapp.data.MultiImages
import com.k.sekiro.chatapp.data.User
import com.k.sekiro.chatapp.data.UserMsgPageData
import com.k.sekiro.chatapp.database.DatabaseReferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.Date

class UserInfoRepImpl(@ApplicationContext context: Context,val db:FirebaseFirestore , val storage: FirebaseStorage) : UserInfoRep {

    override suspend fun getUserData(userId:String) = callbackFlow<User> {

      val listener =  db.collection(DatabaseReferences.UsersCollection).document(userId)
            .addSnapshotListener { value, error ->

                if (error != null){
                    close(error)
                }else{
                    val  user = value!!.toObject(User::class.java)!!
                    trySend(user)
                }



            }

            awaitClose { listener.remove() }
    }


    override suspend fun getUserContacts(user: User): UserMsgPageData {

        val allFriends = ArrayList<User>()
        val activeFriends = ArrayList<User>()

        val friends = user.friends!!

        for (friend in friends){

            db.collection(DatabaseReferences.UsersCollection).document(friend)
                .get().addOnSuccessListener {
                    if (friend != ""){
                        val user1 = it.toObject(User::class.java) as User

                        if (user1.status){
                            activeFriends.add(user1)
                        }

                        allFriends.add(user1)
                    }

                }.await()
        }
        return UserMsgPageData(allFriends, activeFriends)
    }

    override suspend fun getUserContacts2(user: User) = callbackFlow<UserMsgPageData>{
        val allFriends = ArrayList<User>()
        val activeFriends = ArrayList<User>()

        val friends = user.friends!!

     val listener =    db.collection(DatabaseReferences.UsersCollection).addSnapshotListener { value, error ->

            allFriends.clear()
            activeFriends.clear()

            if (error != null){
                close(error)
            }else{

                this.launch {
                    for (friend in friends){
                        for (doc in value!!){
                            if (doc.id == friend){
                                val user1 = doc.toObject(User::class.java)
                                if (user1.status){
                                    activeFriends.add(user1)
                                }
                                allFriends.add(user1)

                            }

                        }
                    }
                    trySend(UserMsgPageData(allFriends, activeFriends)).isSuccess
                }

            }
        }

        awaitClose { listener.remove() }
    }


    override suspend fun sendMessage(message: Message) {
        db.collection(DatabaseReferences.MessagesCollection).add(message)
    }



    override suspend fun getMessages(senderId: String, receiverId: String) = callbackFlow<ArrayList<Message>>{
        val messages = ArrayList<Message>()

         val listener =   db.collection(DatabaseReferences.MessagesCollection).orderBy(DatabaseReferences.time)
             .addSnapshotListener { messagesC, error ->

                    messages.clear()

                if(error != null){
                    close(error)

                }else{

                    this.launch {
                        var msg = Message()

                        for (message in messagesC!!){

                            if (message.getString("type") != "multiImages"){
                                msg = message.toObject(Message::class.java)
                            }else{
                                msg = message.toObject(MultiImages::class.java)
                            }

                            if (senderId == msg.senderId && receiverId == msg.receiverId
                                || senderId == msg.receiverId && receiverId == msg.senderId){
                                messages.add(msg)
                            }
                        }
                        trySend(messages).isSuccess
                    }

                    Log.e("ks","all messages inside userInfo class, ${messages.toString()}")
                }



            }

            awaitClose { listener.remove() }

    }

    override suspend fun getUserStatusRealtime(userId: String) = callbackFlow<Boolean>{
      val listener =  db.collection(DatabaseReferences.UsersCollection).document(userId).addSnapshotListener { value, error ->
            if (error != null){
                close(error)
            }else{
                this.launch {
                    val status = value!!.getBoolean(DatabaseReferences.UserStatus)!!
                    trySend(status).isSuccess
                }

            }
        }
        awaitClose { listener.remove() }
    }

    override suspend fun getAddFriendList(userId: String) = channelFlow<ArrayList<AddFriendList>>{

        val list = ArrayList<AddFriendList>()

          val listener =   db.collection(DatabaseReferences.UsersCollection).document(userId)
                .addSnapshotListener { value, error ->
                val user = value!!.toObject(User::class.java)!!
                val friends = user.friends!!

             db.collection(DatabaseReferences.UsersCollection).get().addOnSuccessListener {QuerySnapshot->

                 this.launch {
                     for (doc in QuerySnapshot){
                         if (doc.id == userId){
                             continue
                         }

                         val user1 = doc.toObject(User::class.java)
                         if (friends.contains(user1.id)){
                             list.add(AddFriendList(user1,true))
                         }else{
                             list.add(AddFriendList(user1,false))
                         }

                     }

                     Log.e("ks","this is list to getAddList: "+list.toString())

                     trySend(list).isSuccess

                 }

             }

                }

        awaitClose { listener.remove() }

    }




    override fun checkIfUserSentAddRequest(senderId: String, receiverId: String) = callbackFlow<String> {
       val listener =  db.collection(DatabaseReferences.FriendRequestCollection).whereEqualTo(DatabaseReferences.SenderId,senderId)
            .whereEqualTo(DatabaseReferences.ReceiverID,receiverId)
            .addSnapshotListener { value, error ->

                if (error != null){
                    trySend("")
                    close(error)
                }else{
                    this.launch {
                        val friendRequests = value?.toObjects(FriendRequest::class.java)



                        if (friendRequests != null && friendRequests?.size != 0){
                            trySend(friendRequests[0].id).isSuccess
                        }else{
                            trySend("").isSuccess
                        }

                    }
                }
            }

        awaitClose { listener.remove() }
    }


    override suspend fun sendFriendRequest(request: FriendRequest) = channelFlow<String>{
        db.collection(DatabaseReferences.FriendRequestCollection).add(request)
            .addOnSuccessListener {
                trySend(it.id)
            }
            .addOnFailureListener {
                trySend("")
            }
    }


    override suspend fun cancelFriendRequest(requestId:String) = channelFlow<Boolean> {
       db.collection(DatabaseReferences.FriendRequestCollection).document(requestId)
           .delete().addOnSuccessListener {
               trySend(true)
           }.addOnFailureListener {
               trySend(false)
           }

    }

    override suspend fun sendImageMsg(uri: Uri,contentResolver:ContentResolver,sender:String, friendId:String ,senderImg:String) = channelFlow<Message> {
        val storageReference =   storage.reference.child("images")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            val img = ImageDecoder.createSource(contentResolver,uri)
            val bitmap = ImageDecoder.decodeBitmap(img)

            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            val imgReference = storageReference.child("${System.currentTimeMillis()}chat.jpeg")
            val uploadTask = imgReference.putBytes(data)
           val listener =  uploadTask.addOnFailureListener {
                // Handle unsuccessful uploads
            }.addOnSuccessListener { taskSnapshot ->





                imgReference.downloadUrl.addOnSuccessListener {
                    Log.e("ks","the url $it")

                    val url = it.toString()

                    val message = Message(sender, friendId,url, Date(),"img", senderImg)

                    trySend(message).isSuccess

                }.addOnFailureListener {
                    close(it)
                }

            }

            awaitClose { listener.cancel() }

        }

    }


    override suspend fun sendAudioMsg(uri: Uri) = channelFlow<Uri>  {
        val storageRef = storage.reference.child("audios")
        val audioRef = storageRef.child(System.currentTimeMillis().toString()+"msg.mp3")
        audioRef.putFile(uri).addOnSuccessListener {
            audioRef.downloadUrl.addOnSuccessListener {
               trySend(it)
            }
        }

        awaitClose { this.close() }
    }


    override suspend fun getAllFriendRequests(receiverId: String) =  callbackFlow<ArrayList<FriendRequestDisplay>> {

        val allRequests = ArrayList<FriendRequestDisplay>()


        val listener =  db.collection(DatabaseReferences.FriendRequestCollection).whereEqualTo(DatabaseReferences.ReceiverID,receiverId)
            .addSnapshotListener { QuerySnapshot, error ->

                if (QuerySnapshot != null){
                    for (req in QuerySnapshot!!){

                        val request = req.toObject(FriendRequest::class.java)

                        Log.e("ks","sender :: ${request.senderId}")

                        db.collection(DatabaseReferences.UsersCollection).document(request.senderId).get()
                            .addOnSuccessListener {

                                val user = it.toObject(User::class.java)

                                Log.e("ks","user inside getAllFriendRequests : ${user.toString()}")

                                if (user != null){
                                    allRequests.add(FriendRequestDisplay(user,request.id))
                                    Log.e("ks","inside getAllFriendRequests ${user.toString()}")

                                    trySend(allRequests)

                                }


                            }
                    }

                    Log.e("ks","inside getAllFriendRequests ${allRequests.toString()}")

                }




            }

        awaitClose { listener.remove() }

    }


    override suspend fun getFriendReqCount(receiverId: String) = channelFlow<Int> {

        val listener = db.collection(DatabaseReferences.FriendRequestCollection).whereEqualTo(DatabaseReferences.ReceiverID,receiverId)
            .addSnapshotListener { value, error ->

                if (value != null){
                    trySend(value!!.count())
                }

            }
        awaitClose { listener.remove() }

    }

    override suspend fun addNewFriend(userId: String, friendId: String) {
        db.collection(DatabaseReferences.UsersCollection).document(userId)
            .update(DatabaseReferences.UserFriends,FieldValue.arrayUnion(friendId))
    }

    override suspend fun deleteFriendRequest(requestId: String) {
        db.collection(DatabaseReferences.FriendRequestCollection).document(requestId).delete()
    }




    override suspend fun searchById(senderId: String, tagId: String) = channelFlow<Boolean> {

        var exist = false

        db.collection(DatabaseReferences.UsersCollection).get()
            .addOnSuccessListener { QuerySnapshot ->
                for (doc in QuerySnapshot) {
                    val user = doc.toObject(User::class.java)

                    if (user.appId == tagId) {

                        exist = true

                        db.collection(DatabaseReferences.FriendRequestCollection).add(
                            FriendRequest(senderId, user.id, false)
                        ).addOnSuccessListener {
                            trySend(true)
                        }

                        break

                    }

                }

                if (!exist){
                    trySend(false)
                }


            }


        awaitClose { this.close() }
    }


    override suspend fun downloadImage(url: String): Flow<Boolean> {
        TODO("Not yet implemented")
    }


    override suspend fun sendMultiImages(
        list: List<Uri>,
        directory: String,
        contentResolver: ContentResolver,
        sender: String,
        friendId: String,
        senderImg: String
    )  = channelFlow<ArrayList<String>>{


            /*   val path = storage.reference.child("images").child(directory).listAll().addOnSuccessListener {
                  for (i in it.items){
                      i.downloadUrl.addOnSuccessListener {

                      }
                  }
               }*/
            val path = storage.reference.child("images").child(directory)

                     launch {

                         val images = ArrayList<String>()

                        for (uri in list) {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                val img = ImageDecoder.createSource(contentResolver, uri)
                                val bitmap = ImageDecoder.decodeBitmap(img)

                                val baos = ByteArrayOutputStream()
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)
                                val data = baos.toByteArray()

                                val imgReference = path.child("${System.currentTimeMillis()}chat.jpeg")
                                val uploadTask = imgReference.putBytes(data)
                                val listener = uploadTask.addOnFailureListener {
                                    // Handle unsuccessful uploads
                                }.addOnSuccessListener { taskSnapshot ->


                                    imgReference.downloadUrl.addOnSuccessListener {
                                        Log.e("ks", "the url $it")

                                        val url = it.toString()
                                        images.add(url)
                                        Log.e("ks", "the multi images inside download url : ${images.size}")
                                    }

                                }.await()


                            }

                        }

                         delay(1000)

                         trySend(images).isSuccess

                    }



        awaitClose { this.close() }
    }


    override suspend fun deletePlaceHolderImages(date: Date) {
        db.collection(DatabaseReferences.MessagesCollection).whereEqualTo("message","").whereEqualTo("time",date)
            .get().addOnSuccessListener {
                db.collection(DatabaseReferences.MessagesCollection).document(it.documents[0].id).delete()
            }
    }
}