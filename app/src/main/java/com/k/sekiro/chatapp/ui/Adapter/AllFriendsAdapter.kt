package com.k.sekiro.chatapp.ui.Adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.k.sekiro.chatapp.R
import com.k.sekiro.chatapp.data.Message
import com.k.sekiro.chatapp.data.User
import com.k.sekiro.chatapp.database.DatabaseReferences
import com.k.sekiro.chatapp.databinding.ContactsDesignBinding
import com.k.sekiro.chatapp.ui.activities.Chat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class AllFriendsAdapter(val activity: Activity,val data:ArrayList<User>):RecyclerView.Adapter<AllFriendsAdapter.ItemHolder>(){

    val db = Firebase.firestore

    val sharedPref = activity.getSharedPreferences("user",Context.MODE_PRIVATE)

    val userId = sharedPref.getString("userId","")!!

    class ItemHolder(val binding:ContactsDesignBinding):RecyclerView.ViewHolder(binding.root)



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemHolder {
        val binding = ContactsDesignBinding.inflate(activity.layoutInflater,parent,false)
        return ItemHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {

        val user = data[position]


        if (user.img == ""){
            holder.binding.userImgC.setImageResource(R.drawable.ic_person)
        }else{
            Glide.with(activity).load(user.img).into(holder.binding.userImgC)
        }

        holder.binding.userNameC.text = user.name

        holder.binding.numberMsgHolder.visibility = View.GONE
        holder.binding.numMsg.visibility = View.GONE

        CoroutineScope(Dispatchers.Main).launch {
             getLastMessage(userId,user.id).collect{msg->
                 if (msg.type == "text"){
                     holder.binding.lastMsg.text = msg.message
                 }else if ((msg.type == "img" || msg.type == "multiImages") && msg.senderId == userId){
                     holder.binding.lastMsg.text = "you sent photo"
                 }else{
                     holder.binding.lastMsg.text = "photo has been sent to you"
                 }

                 val time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(msg.time)

                 holder.binding.timeMsg.text = time
             }

            }






        if (user.status){
            holder.binding.userState.background = activity.getDrawable(R.drawable.online_state_shape)
        }else{
            holder.binding.userState.background = activity.getDrawable(R.drawable.offline_state_shape)
        }


        holder.binding.root.setOnClickListener {
            val intent = Intent(activity, Chat::class.java)
            intent.putExtra("friendName",user.name)
            intent.putExtra("friendStatus",user.status)
            intent.putExtra("friendImg",user.img)
            intent.putExtra("friendId",user.id)
            intent.putExtra("friendToken",user.token)
            activity.startActivity(intent)
        }


    }

    override fun getItemCount(): Int {
        return data.size
    }



    fun updateUi(){
        notifyDataSetChanged()
    }


     suspend fun getLastMessage(senderId:String,receiverId:String) = callbackFlow<Message> {


      val listener =   db.collection(DatabaseReferences.MessagesCollection).orderBy(DatabaseReferences.time,Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->

                if (error != null) {
                    close(error)
                }else {
                        for (doc in value!!) {
                            val msg = doc.toObject(Message::class.java)
                            if (msg.senderId == senderId && msg.receiverId == receiverId || msg.senderId == receiverId && msg.receiverId == senderId) {
                                trySend(msg)
                                break
                            }
                        }



                }
            }

         awaitClose { listener.remove() }

            }




}