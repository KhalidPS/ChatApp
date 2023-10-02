package com.k.sekiro.chatapp.ui.dialogs

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.k.sekiro.chatapp.app.media_recorder.MyMediaRecorder
import com.k.sekiro.chatapp.databinding.BottomSheetBinding
import com.k.sekiro.chatapp.viewModel.ViewModelApp
import java.io.File


class BottomSheet(val viewModel:ViewModelApp):BottomSheetDialogFragment() {


    lateinit var sharedPref: SharedPreferences


    private val mediaRecorder by lazy {
        MyMediaRecorder(requireContext())
    }




    private var audioFile: File? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        sharedPref = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE)


        val binding = BottomSheetBinding.inflate(inflater, container, false)


        binding.lottieAnimationView.speed = 0.1f

        binding.cancel.setOnClickListener {
            audioFile = null
            dismiss()
        }

        binding.startRecord.setOnClickListener {

            File(requireContext().cacheDir, "msg.mp3").apply {
                audioFile = this
                mediaRecorder.start(this)
            }



            binding.lottieAnimationView.visibility = View.VISIBLE

        }


        binding.sendAudio.setOnClickListener {
            if (audioFile != null) {

                mediaRecorder.stop()
                binding.lottieAnimationView.isActivated = false


                val friendId = requireActivity().intent.getStringExtra("friendId")!!

                val sender = sharedPref.getString("userId", "") as String
                val senderImg = sharedPref.getString("userImg", "")!!


                val audioUri = Uri.fromFile(audioFile)

                viewModel.sendAudioMsg(audioUri,sender, friendId, senderImg)


                dismiss()

               /* val storageRef = Firebase.storage.reference.child("audios")
                val audioRef = storageRef.child(System.currentTimeMillis().toString() + "msg.mp3")
                audioRef.putFile(audioUri).addOnSuccessListener {

                    audioRef.downloadUrl.addOnSuccessListener {

                        Log.e("ks", "sender:$sender, friendId:$friendId, senderImg:$senderImg")


                    }
                }*/


            }
        }

        return binding.root

    }
}