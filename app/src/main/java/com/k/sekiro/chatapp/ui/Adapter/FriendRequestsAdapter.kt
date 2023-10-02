package com.k.sekiro.chatapp.ui.Adapter

import android.content.Context
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.k.sekiro.chatapp.R
import com.k.sekiro.chatapp.data.FriendRequestDisplay
import com.k.sekiro.chatapp.databinding.FriendRequestsDesignBinding
import com.k.sekiro.chatapp.viewModel.ViewModelApp

class FriendRequestsAdapter(val activity: FragmentActivity , val data:ArrayList<FriendRequestDisplay>, val viewModelApp: ViewModelApp):RecyclerView.Adapter<FriendRequestsAdapter.Holder>() {

    val sharedPref = activity.getSharedPreferences("user",Context.MODE_PRIVATE)


    class Holder(val binding:FriendRequestsDesignBinding):RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        val binding = FriendRequestsDesignBinding.inflate(activity.layoutInflater,parent,false)
        return Holder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        val user = data[position].user


        if (user.img != ""){
            Glide.with(activity).load(user.img).into(holder.binding.userImg)
        }else{
            holder.binding.userImg.setImageResource(R.drawable.ic_person)
        }


        holder.binding.userName.text = user.name


        holder.binding.acceptBtn.setOnClickListener {
            val userId = sharedPref.getString("userId","")!!
            viewModelApp.addNewFriend(userId,user.id)
            viewModelApp.addNewFriend(user.id,userId)
            viewModelApp.deleteFriendRequest(data[position].reqId)
        }


        holder.binding.deleteBtn.setOnClickListener {
            viewModelApp.deleteFriendRequest(data[position].reqId)
        }





    }


}