package com.k.sekiro.chatapp.ui.Adapter

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.RenderEffect
import android.graphics.Shader
import android.net.Uri
import android.os.Build
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.k.sekiro.chatapp.R
import com.k.sekiro.chatapp.app.media_recorder.MyMediaPlayer
import com.k.sekiro.chatapp.data.Message
import com.k.sekiro.chatapp.databinding.ReceiveAudioBinding
import com.k.sekiro.chatapp.databinding.ReceiveMessageBinding
import com.k.sekiro.chatapp.databinding.ReciveImageBinding
import com.k.sekiro.chatapp.databinding.SendAudioBinding
import com.k.sekiro.chatapp.databinding.SendImageBinding
import com.k.sekiro.chatapp.databinding.SendMessageBinding
import java.text.SimpleDateFormat
import java.util.Locale
import android.os.Handler
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.k.sekiro.chatapp.converters.ImageConverts
import com.k.sekiro.chatapp.data.MultiImages
import com.k.sekiro.chatapp.databinding.MultipleImagesReceiverBinding
import com.k.sekiro.chatapp.databinding.MultipleImagesSenderBinding
import com.k.sekiro.chatapp.ui.dialogs.ShowFullImage
import com.k.sekiro.chatapp.ui.dialogs.ShowMultiImagesDialog
import jp.wasabeef.blurry.Blurry

class ChatAdapter (val activity: AppCompatActivity,val messages:ArrayList<Message>):RecyclerView.Adapter<ChatAdapter.Holder>(){



    val sharedPref = activity.getSharedPreferences("user", Context.MODE_PRIVATE)


    val Message_Type_Right = 1
    val Message_Type_Right_img = 2
    val Message_Type_Left = 0
    val Message_Type_Left_img = 3
    val Message_Type_Right_Audio = 4
    val Message_Type_Left_Audio = 5
    val Message_Type_Left_4_or_more = 6
    val Message_Type_Right_4_or_more = 7







    private val handler = Handler(Looper.getMainLooper())
    private var runnable:Runnable? = null
    private var delay = 1000L


    class Holder(val binding:ViewBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        if (viewType == Message_Type_Right){
            val binding = SendMessageBinding.inflate(activity.layoutInflater,parent,false)
            return Holder(binding)

        }else if (viewType == Message_Type_Left){
            val binding = ReceiveMessageBinding.inflate(activity.layoutInflater,parent,false)
            return Holder(binding)

        }else if (viewType == Message_Type_Right_img){
            val binding = SendImageBinding.inflate(activity.layoutInflater,parent,false)
            return Holder(binding)

        }else if (viewType == Message_Type_Left_img){
            val binding = ReciveImageBinding.inflate(activity.layoutInflater,parent,false)
            return Holder(binding)

        }else if (viewType == Message_Type_Right_Audio){
            val binding = SendAudioBinding.inflate(activity.layoutInflater,parent,false)
            return Holder(binding)

        }else if (viewType == Message_Type_Left_Audio){
            val binding = ReceiveAudioBinding.inflate(activity.layoutInflater,parent,false)
            return Holder(binding)

        }else if (viewType == Message_Type_Right_4_or_more){
            val binding = MultipleImagesSenderBinding.inflate(activity.layoutInflater,parent,false)
            return Holder(binding)

        }else{
            val binding = MultipleImagesReceiverBinding.inflate(activity.layoutInflater,parent,false)
            return Holder(binding)

        }
    }

    override fun onBindViewHolder(holder:Holder, position: Int) {


       val time = SimpleDateFormat("MMMM dd yyyy - hh:mm a", Locale.getDefault()).format(messages[position].time)

        var showDate = true

        if (holder.binding is SendMessageBinding){

            sendMessage(time = time , binding = holder.binding, position = position)

        }else if (holder.binding is ReceiveMessageBinding){

            receiveMessage(binding = holder.binding, time =  time, position =  position)

/////////////////////////////////////////////////////
        }else if (holder.binding is SendImageBinding){
            Glide.with(activity).load(messages[position].message).into(holder.binding.image)
            holder.binding.msgTime.text = time



            holder.binding.image.setOnClickListener {
                ShowFullImage(messages[position].message).show(activity.supportFragmentManager,"")
            }



///////////////////////////////////////////////////
        }else if (holder.binding is ReciveImageBinding){

            if (messages[position].senderImg == ""){
                holder.binding.senderImg.setImageResource(R.drawable.ic_person)
            }else{
                Glide.with(activity).load(messages[position].senderImg).into(holder.binding.senderImg)
            }

            holder.binding.msgTime.text = time

            Glide.with(activity).load(messages[position].message).into(holder.binding.image)


            holder.binding.image.setOnClickListener {
                ShowFullImage(messages[position].message).show(activity.supportFragmentManager,"")
            }




         //////////////////////////////////////
        }else if (holder.binding is SendAudioBinding){

            var isPlay = false

            val player = MyMediaPlayer(activity)





            holder.binding.playBtn.setOnClickListener {



                isPlay = !isPlay
                if (isPlay){
                    holder.binding.playBtn.setImageResource(R.drawable.ic_pause)

                    if (player.getPlayerValue() == null){
                        player.start(Uri.parse(messages[position].message))


                        holder.binding.seekBar.max = player.getPlayerValue()!!.duration


                        runnable =  Runnable {
                            holder.binding.seekBar.progress = player.getPlayerValue()?.currentPosition!!
                            handler.postDelayed(runnable!!,0)

                        }



                        handler.postDelayed(runnable!!,0)






                    }else{
                        player.resume()
                        holder.binding.seekBar.max = player.getPlayerValue()!!.duration



                        runnable =  Runnable {
                            holder.binding.seekBar.progress = player.getPlayerValue()?.currentPosition!!
                            handler.postDelayed(runnable!!,0)

                        }



                        handler.postDelayed(runnable!!,0)



                    }


                }else{
                    holder.binding.playBtn.setImageResource(R.drawable.ic_play)
                    player.pause()
                    handler.removeCallbacks(runnable!!)

                }

            }



            player.getPlayerValue()?.setOnCompletionListener {
                holder.binding.playBtn.setImageResource(R.drawable.ic_play)
                handler.removeCallbacks(runnable!!)
            }




            
            holder.binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {

                    if (fromUser){
                        player.getPlayerValue()!!.seekTo(progress)
                    }

                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }

            })



            holder.binding.msgTime.text = time

 ////////////////////////////////////////////////////////////////////////////////////////////////
 ////////////////////////////////////////////////////////////////////////////////////////////////

        }else if (holder.binding is MultipleImagesSenderBinding){


            sendMultiImages(binding = holder.binding, time =  time, position = position)


        }







    }

    override fun getItemCount(): Int {
        return messages.size
    }


    override fun getItemViewType(position: Int): Int {
        if (sharedPref.getString("userId","") == messages[position].senderId){
           return when(messages[position].type){
                "text" -> Message_Type_Right
                "audio" -> Message_Type_Right_Audio
                "multiImages" -> {

                        return if (messages[position] is MultiImages && (messages[position] as MultiImages).images!!.size>= 4){

                            Message_Type_Right_4_or_more
                        }else{
                            Message_Type_Right_4_or_more
                        }

                }
                else -> Message_Type_Right_img
            }

        }else{
            return when(messages[position].type){
                "text" -> Message_Type_Left
                "audio" -> Message_Type_Left_Audio
                "multiImages" -> Message_Type_Left_4_or_more
                else -> Message_Type_Left_img
            }
        }
    }




////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////
    fun sendMessage(binding: SendMessageBinding,time:String,position: Int){
        var showDate = true


        binding.msgTime.visibility = View.GONE
        binding.msgTime.text = time
        binding.messageTv.text = messages[position].message
        Log.e("ks","this is SendMessageBinding")


        binding.root.setOnClickListener {
            if (showDate){
                binding.msgTime.visibility = View.VISIBLE
            }else{
                binding.msgTime.visibility = View.GONE
            }
            showDate = !showDate
        }
    }

////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////
    fun receiveMessage(binding: ReceiveMessageBinding,time:String,position: Int){

        var showDate = true

        binding.msgTime.visibility = View.GONE
        binding.msgTime.text = time
        binding.messageTv.text = messages[position].message

        Log.e("ks","this is ReceiveMessageBinding")


        if (messages[position].senderImg == ""){

            binding.senderImg.setImageResource(R.drawable.ic_person)
        }else{

            Glide.with(activity).load(messages[position].senderImg).into(binding.senderImg)
        }


        binding.root.setOnClickListener {

            if (showDate){

                binding.msgTime.visibility = View.VISIBLE
            }else{

                binding.msgTime.visibility = View.GONE
            }
            showDate = !showDate
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////

    fun sendMultiImages(binding: MultipleImagesSenderBinding,time: String,position: Int){

        val msg = messages[position] as MultiImages

        if (msg.message != ""){

            try {
                Glide.with(activity).load(msg.images!![0]).into(binding.image1)
                Glide.with(activity).load(msg.images!![1]).into(binding.image2)
                Glide.with(activity).load(msg.images!![2]).into(binding.image3)
                Glide.with(activity).load(msg.images!![3]).into(binding.image4)
            }catch (e:IndexOutOfBoundsException){

                Toast.makeText(activity,e.message,Toast.LENGTH_SHORT).show()

            }catch (e:ArrayIndexOutOfBoundsException){

                Toast.makeText(activity,e.message,Toast.LENGTH_SHORT).show()

            }catch (e:Exception){

                Toast.makeText(activity,e.message,Toast.LENGTH_SHORT).show()

            }


            binding.imagesHolder.setOnClickListener {

                ShowMultiImagesDialog(msg.images).show(activity.supportFragmentManager,"")

            }




        }else{

            binding.image1.setImageURI(Uri.parse(msg.images!![0]))
            binding.image2.setImageURI(Uri.parse(msg.images!![1]))
            binding.image3.setImageURI(Uri.parse(msg.images!![2]))
            binding.image4.setImageURI(Uri.parse(msg.images!![3]))


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
                binding.image1.setRenderEffect(RenderEffect.createBlurEffect(10f,10f,Shader.TileMode.MIRROR))
                binding.image2.setRenderEffect(RenderEffect.createBlurEffect(10f,10f,Shader.TileMode.MIRROR))
                binding.image3.setRenderEffect(RenderEffect.createBlurEffect(10f,10f,Shader.TileMode.MIRROR))
                binding.image4.setRenderEffect(RenderEffect.createBlurEffect(10f,10f,Shader.TileMode.MIRROR))
            }

            binding.loading.visibility = View.VISIBLE


        }





        binding.msgTime.text = time


    }






}