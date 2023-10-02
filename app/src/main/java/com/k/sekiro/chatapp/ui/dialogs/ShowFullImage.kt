package com.k.sekiro.chatapp.ui.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.view.marginTop
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.k.sekiro.chatapp.R
import com.k.sekiro.chatapp.databinding.FragmentShowFullImageBinding
import com.k.sekiro.chatapp.viewModel.ViewModelApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShowFullImage(val url:String) :  DialogFragment() {


    val viewModelApp:ViewModelApp by viewModels()


    lateinit var binding: FragmentShowFullImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


         binding = FragmentShowFullImageBinding.inflate(inflater,container,false)

        binding.moreBtn.visibility = View.GONE

        dialog!!.apply {
            requestWindowFeature(STYLE_NO_TITLE)
            setCancelable(true)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window!!.setGravity(Gravity.CENTER)
        }



        dialog?.window?.setLayout(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        )



        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        Glide.with(requireActivity()).load(url).into(binding.fullImage)



    }

    override fun onStart() {
        super.onStart()




        binding.fullImage.setOnClickListener {

            dialog?.window?.apply {

               setLayout(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT
                )

                binding.fullImage.layoutParams.height = 0
                binding.fullImage.layoutParams.width = LayoutParams.MATCH_PARENT




                setBackgroundDrawable(ColorDrawable(Color.BLACK))

                binding.moreBtn.visibility = View.VISIBLE



            }


        }



        binding.moreBtn.setOnClickListener {
            val pop = PopupMenu(requireActivity(),binding.moreBtn).apply {
                menuInflater.inflate(R.menu.image_settings_menu,menu)

                setOnMenuItemClickListener {
                    when(it.itemId){
                        R.id.downloadItem ->{
                            Toast.makeText(requireActivity(),"download",Toast.LENGTH_SHORT).show()
                        }

                        R.id.deleteItem ->{
                            Toast.makeText(requireActivity(),"delete",Toast.LENGTH_SHORT).show()
                        }
                    }

                    true
                }

                show()
            }

        }






    }











}