package com.k.sekiro.chatapp.ui.Adapter

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.k.sekiro.chatapp.R
import com.k.sekiro.chatapp.data.AddFriendList
import com.k.sekiro.chatapp.data.FriendRequest
import com.k.sekiro.chatapp.data.MessageNotification
import com.k.sekiro.chatapp.data.Notification
import com.k.sekiro.chatapp.data.PushNotification
import com.k.sekiro.chatapp.databinding.AddFriendListDesignBinding
import com.k.sekiro.chatapp.viewModel.ViewModelApp
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AddFriendListAdapter(val activity: FragmentActivity , val data:ArrayList<AddFriendList>,val viewModelApp: ViewModelApp):RecyclerView.Adapter<AddFriendListAdapter.ItemHolder>() {

    val sharedPref = activity.getSharedPreferences("user",Context.MODE_PRIVATE)
    val userId = sharedPref.getString("userId","")!!

    class ItemHolder(val binding:AddFriendListDesignBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {

        val binding = AddFriendListDesignBinding.inflate(activity.layoutInflater,parent,false)
        return ItemHolder(binding)

    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val user = data[position].user
        val isFriend = data[position].isFriend



        if (user.img != ""){
            Glide.with(activity).load(user.img).into(holder.binding.userImg)
        }else{
            holder.binding.userImg.setImageResource(R.drawable.ic_person)
        }

        holder.binding.userName.text = user.name
        holder.binding.userAccountType.text = user.accountType+" account"



        activity.lifecycleScope.launch {
            viewModelApp.checkIfUserSentAddRequest(userId,user.id).collect{

                Log.e("ks","isFriend:$isFriend before if")

                if (isFriend){
                    Log.e("ks","isFriend:$isFriend inside if")
                    holder.binding.sendAddRequest.visibility = View.GONE
                    holder.binding.pendingReq.visibility = View.GONE
                    holder.binding.friendTv.visibility = View.VISIBLE
                }else if (it != ""){
                    holder.binding.sendAddRequest.visibility = View.GONE
                    holder.binding.pendingReq.visibility = View.VISIBLE
                    holder.binding.requestId.text = it
                }else{
                    holder.binding.sendAddRequest.visibility = View.VISIBLE
                    holder.binding.pendingReq.visibility = View.GONE
                    holder.binding.friendTv.visibility = View.GONE
                }
            }
        }




        /* viewModelApp.viewModelScope.launch {
     viewModelApp.checkIfUserSentAddRequest(userId,user.id).collect{

         //  viewModelApp.checkIfUserSentAddRequestLiveData.observe(activity){

         Log.e("ks","isFriend:$isFriend before if")

         if (isFriend){
             Log.e("ks","isFriend:$isFriend inside if")
             holder.binding.sendAddRequest.visibility = View.GONE
             holder.binding.pendingReq.visibility = View.GONE
             holder.binding.friendTv.visibility = View.VISIBLE
         }else if (it != ""){
             holder.binding.sendAddRequest.visibility = View.GONE
             holder.binding.pendingReq.visibility = View.VISIBLE
             holder.binding.requestId.text = it
         }else{
             holder.binding.sendAddRequest.visibility = View.VISIBLE
             holder.binding.pendingReq.visibility = View.GONE
             holder.binding.friendTv.visibility = View.GONE
         }
         //   }

     }


 }*/





        holder.binding.sendAddRequest.setOnClickListener {

            viewModelApp.sendFriendRequest(FriendRequest(userId,user.id,false))
            viewModelApp.sendNotification(
                PushNotification(
                    MessageNotification(Notification("${user.name} send friend request to you","friend request"),user.token)
                )
            )

            viewModelApp.friendReqResultLiveData.observe(activity){
                if (it == ""){
                    Toast.makeText(activity,"friend request has been failed",Toast.LENGTH_SHORT).show()
                }else{
                    holder.binding.requestId.text = it
                }
            }

        }


        holder.binding.pendingReq.setOnClickListener {

            val dialog = AlertDialog.Builder(activity).apply {
                setTitle("delete request")
                setMessage("Do you want to delete friend request")
                setIcon(R.drawable.baseline_delete_24)
                setPositiveButton("Yes"){_,_ ->
                    viewModelApp.cancelFriendRequest(holder.binding.requestId.text.toString())
                    Log.e("ks", "this friend reqId "+ holder.binding.requestId.text.toString())

                    viewModelApp.cancelResultLiveData.observe(activity){
                        if (!it){
                            Toast.makeText(activity,"delete friend request has been failed",Toast.LENGTH_SHORT).show()
                        }
                    }

                }

                setNegativeButton("No"){dis,_ ->
                    dis.dismiss()
                }

                create()
                show()
            }

        }





    }





}