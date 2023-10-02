package com.k.sekiro.chatapp.ui.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.k.sekiro.chatapp.databinding.ShowFullMultiImagesBinding
import com.k.sekiro.chatapp.ui.Adapter.ShowMultiImagesAdapter

class ShowMultiImagesDialog(val urls:ArrayList<String>?): DialogFragment() {

    lateinit var binding:ShowFullMultiImagesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = ShowFullMultiImagesBinding.inflate(inflater,container,false)

        val adapter = ShowMultiImagesAdapter(requireActivity(),urls)
        binding.rvMultiImages.adapter = adapter
        binding.rvMultiImages.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvMultiImages.addItemDecoration(DividerItemDecoration(requireActivity(),DividerItemDecoration.VERTICAL))


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.apply {

            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )


            setBackgroundDrawable(ColorDrawable(Color.BLACK))




        }


    }


}