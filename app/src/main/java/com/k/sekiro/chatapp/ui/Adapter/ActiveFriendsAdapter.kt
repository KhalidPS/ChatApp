package com.k.sekiro.chatapp.ui.Adapter

import android.app.Activity
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.k.sekiro.chatapp.R
import com.k.sekiro.chatapp.data.User
import com.k.sekiro.chatapp.databinding.OnlinePersonDesignBinding

class ActiveFriendsAdapter(val activity: Activity,val data:ArrayList<User>):RecyclerView.Adapter<ActiveFriendsAdapter.ItemHolder> (){
    class ItemHolder(val binding:OnlinePersonDesignBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemHolder {
       val binding = OnlinePersonDesignBinding.inflate(activity.layoutInflater,parent,false)
        return ItemHolder(binding)
    }

    override fun onBindViewHolder(holder:ItemHolder, position: Int) {

        if (data[position].img == ""){
            holder.binding.userImg.setImageResource(R.drawable.ic_person)
        }else{
            Glide.with(activity).load(data[position].img).into(holder.binding.userImg)
        }


        if (data[position].name.length > 10){
            holder.binding.userName.text = data[position].name.substring(0,11)+"..."
        }else{
            holder.binding.userName.text = data[position].name
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun updateUi(){
        notifyDataSetChanged()
    }

}