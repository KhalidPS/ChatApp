package com.k.sekiro.chatapp.ui.Adapter

import android.os.Build
import android.print.PrintAttributes.Margins
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.WindowManager.LayoutParams
import android.view.WindowMetrics
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.MarginLayoutParamsCompat
import androidx.core.view.marginEnd
import androidx.core.view.setMargins
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.k.sekiro.chatapp.R
import com.k.sekiro.chatapp.databinding.FragmentShowFullImageBinding

class ShowMultiImagesAdapter(val activity: FragmentActivity, val uris:ArrayList<String>?):RecyclerView.Adapter<ShowMultiImagesAdapter.MyHolder>(){
    class MyHolder(var binding:FragmentShowFullImageBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val binding = FragmentShowFullImageBinding.inflate(activity.layoutInflater,parent,false)
        return MyHolder(binding)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {

        var windowWidth = 0
        var windowHeight = 0
        var scale = activity.resources.displayMetrics.density

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){

            windowWidth =  activity.windowManager.currentWindowMetrics.bounds.width()
            windowHeight =  activity.windowManager.currentWindowMetrics.bounds.height()
        }else{
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)

            windowWidth = displayMetrics.widthPixels
            windowHeight = displayMetrics.heightPixels
        }

        Glide.with(activity).load(uris!![position]).into(holder.binding.fullImage)



            holder.binding.moreBtn.visibility = View.VISIBLE
            holder.binding.fullImage.layoutParams.width = LayoutParams.MATCH_PARENT
            holder.binding.fullImage.layoutParams.height = LayoutParams.MATCH_PARENT

            holder.binding.fullImage.scaleType = ImageView.ScaleType.CENTER_CROP

           // holder.binding.fullImage.setPadding(0,0,0,(12*scale + 0.5f).toInt())

            holder.binding.root.setPadding(0,0,0,(12*scale + 0.5f).toInt())


            holder.binding.moreBtn.setOnClickListener {
                val pop = PopupMenu(activity,holder.binding.moreBtn).apply {
                    menuInflater.inflate(R.menu.image_settings_menu,menu)

                setOnMenuItemClickListener {
                    when(it.itemId){
                        R.id.downloadItem ->{
                            Toast.makeText(activity,"download", Toast.LENGTH_SHORT).show()
                        }

                        R.id.deleteItem ->{
                            Toast.makeText(activity,"delete", Toast.LENGTH_SHORT).show()
                        }
                    }

                    true
                }

                show()
            }

        }


    }

    override fun getItemCount(): Int {
        return uris!!.size
    }


}