package com.k.sekiro.chatapp.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.k.sekiro.chatapp.R
import com.k.sekiro.chatapp.databinding.FragmentFriendRequestsBinding
import com.k.sekiro.chatapp.ui.Adapter.FriendRequestsAdapter
import com.k.sekiro.chatapp.viewModel.ViewModelApp
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FriendRequests : Fragment() {

     val viewModelApp:ViewModelApp by viewModels()

    @Inject
    lateinit var sharedPref:SharedPreferences


    lateinit var binding: FragmentFriendRequestsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

         binding = FragmentFriendRequestsBinding.inflate(inflater,container,false)






        return binding.root
    }


    override fun onStart() {
        super.onStart()

        viewModelApp.getAllFriendRequests(sharedPref.getString("userId","")!!)



        viewModelApp.friendRequestDisplayLiveData.observe(requireActivity()){

            Log.e("ks","array: ${it.toString()}")

            val adapter = FriendRequestsAdapter(requireActivity(),it , viewModelApp)

            binding.rvRequests.layoutManager = LinearLayoutManager(requireActivity())

            binding.rvRequests.adapter  = adapter


        }

    }






}