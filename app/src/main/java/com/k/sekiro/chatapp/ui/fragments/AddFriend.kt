package com.k.sekiro.chatapp.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.k.sekiro.chatapp.R
import com.k.sekiro.chatapp.databinding.FragmentAddFriendBinding
import com.k.sekiro.chatapp.ui.Adapter.AddFriendListAdapter
import com.k.sekiro.chatapp.viewModel.ViewModelApp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.appcompat.widget.SearchView
import com.k.sekiro.chatapp.data.AddFriendList
import java.util.Locale

@AndroidEntryPoint
class AddFriend : Fragment() {
    lateinit var binding:FragmentAddFriendBinding

    @Inject lateinit var sharedPref:SharedPreferences



    private val viewModelApp:ViewModelApp by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddFriendBinding.inflate(inflater,container,false)




        return binding.root
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onStart() {
        super.onStart()


        val copyList = ArrayList<AddFriendList>()
        val searchList = ArrayList<AddFriendList>()


        val userId = sharedPref.getString("userId", "")!!
        Log.e("ks", "this is user id $userId")

        viewModelApp.getAddFriendList(userId)

        Log.e("ks", "after call getAddFriendList")


        val activity = requireActivity()


        viewModelApp.addFriendListLiveData.observe(activity) {

            copyList.clear()
            searchList.clear()

            copyList.addAll(it)
            searchList.addAll(it)

            Log.e("ks", "after observe")
            val adapter = AddFriendListAdapter(activity, searchList, viewModelApp)
            binding.rvListAdd.layoutManager = LinearLayoutManager(activity)
            binding.rvListAdd.adapter = adapter
        }




        binding.buttonAdd.setOnClickListener {

            val tag = binding.searchViewAddById.query.toString()

            if (tag.isNotEmpty()){
                viewModelApp.searchById(userId, tag)



                viewModelApp.searchByIdLiveData.observe(this) {
                    if (!it) {
                        binding.errorAddFriend.setTextColor(activity.getColor(R.color.red))
                        binding.errorAddFriend.visibility = View.VISIBLE
                        binding.errorAddFriend.text = activity.getString(R.string.errorAddFriendByIdMsg)


                    } else {
                        binding.errorAddFriend.setTextColor(activity.getColor(R.color.coolGreen))
                        binding.errorAddFriend.visibility = View.VISIBLE
                        binding.errorAddFriend.text = activity.getString(R.string.successAddFriendByIdMsg)

                    }

                    object : CountDownTimer(3000,1000){
                        override fun onTick(millisUntilFinished: Long) {

                            lifecycleScope.launch {
                                delay(1000)
                            }

                        }

                        override fun onFinish() {
                            binding.errorAddFriend.visibility = View.INVISIBLE
                        }
                    }.start()

                }

            }




        }







        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                if (newText!!.isNotEmpty()){
                    searchList.clear()
                    val search = newText.lowercase(Locale.getDefault())

                    for (item in copyList){
                        if (item.user.name.lowercase(Locale.getDefault()).contains(search)){
                            searchList.add(item)
                        }

                        binding.rvListAdd.adapter!!.notifyDataSetChanged()
                    }



                }else{
                    searchList.clear()
                    searchList.addAll(copyList)
                    binding.rvListAdd.adapter!!.notifyDataSetChanged()
                }
                return true
            }
        })





    }


    override fun onResume() {
        super.onResume()
    }


    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}