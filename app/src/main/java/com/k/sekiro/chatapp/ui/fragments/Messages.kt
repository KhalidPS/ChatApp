package com.k.sekiro.chatapp.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.k.sekiro.chatapp.databinding.FragmentMessagesBinding
import com.k.sekiro.chatapp.ui.Adapter.ActiveFriendsAdapter
import com.k.sekiro.chatapp.ui.Adapter.AllFriendsAdapter
import com.k.sekiro.chatapp.viewModel.ViewModelApp
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class Messages : Fragment() {
    lateinit var binding:FragmentMessagesBinding

    val viewModelApp:ViewModelApp by viewModels()




    @Inject lateinit var db:FirebaseFirestore


  @Inject lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMessagesBinding.inflate(inflater,container,false)





        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



    }

    override fun onStart() {
        super.onStart()

     val activity = requireActivity()

        val id = sharedPref.getString("userId","") as String
        Log.e("ks","id:$id")

        viewModelApp.getUserData(id)


         viewModelApp.userDataLiveData.observe(activity){
            val user = it
            Log.e("ks","user data here ${user.toString()}")
            sharedPref.edit().putString("userImg",user.img).commit()



            viewModelApp.getUserContacts(user)

            viewModelApp.userContactsLiveData.observe(activity){

                val adapter1 = ActiveFriendsAdapter(activity,it.activeFriends)
                binding.rvOnline.adapter = adapter1
                binding.rvOnline.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)



               val adapter2  = AllFriendsAdapter(activity,it.allFriends)
                binding.rvContacts.adapter = adapter2
                binding.rvContacts.layoutManager = LinearLayoutManager(activity)


            }







            /* viewModelApp.getUserContacts(user)

             viewModelApp.userContactsLiveData.observe(requireActivity()){

                 val adapter1 = ActiveFriendsAdapter(requireActivity(),it.activeFriends)
                 Log.e("ks",it.activeFriends.toString())
                 binding.rvOnline.adapter = adapter1
                 binding.rvOnline.layoutManager = LinearLayoutManager(requireActivity(),LinearLayoutManager.HORIZONTAL,false)
                 adapter1.updateUi()


                 val adapter2  = AllFriendsAdapter(requireActivity(),it.allFriends)
                 Log.e("ks",it.allFriends .toString())
                 binding.rvContacts.adapter = adapter2
                 binding.rvContacts.layoutManager = LinearLayoutManager(requireActivity())
                 adapter2.updateUi()

             }*/


        }



        binding.refreshLayout.setOnRefreshListener {
            if (binding.rvContacts.adapter != null){
                binding.rvContacts.adapter!!.notifyDataSetChanged()
            }
            if (binding.rvOnline.adapter != null){
                binding.rvOnline.adapter!!.notifyDataSetChanged()
            }

            binding.refreshLayout.isRefreshing = false
        }



    //    Log.e("ks",activeFriends.toString())
      //  Log.e("ks",allFriends.toString())


    }


    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }


}