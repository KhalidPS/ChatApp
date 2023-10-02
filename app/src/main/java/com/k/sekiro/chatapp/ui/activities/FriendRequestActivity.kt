package com.k.sekiro.chatapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.k.sekiro.chatapp.databinding.ActivityFriendRequestBinding
import com.k.sekiro.chatapp.ui.Adapter.ViewPagerAdapter
import com.k.sekiro.chatapp.ui.fragments.FriendRequests
import com.k.sekiro.chatapp.ui.fragments.PendingRequests
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FriendRequestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityFriendRequestBinding.inflate(layoutInflater)

        setContentView(binding.root)


        val frag = ArrayList<Fragment>()
        frag.add(FriendRequests())
        frag.add(PendingRequests())


        val title = listOf("Friend Request","Pending Request")



        val adapter = ViewPagerAdapter(supportFragmentManager,frag,lifecycle)

        binding.viewPager2.adapter = adapter

        TabLayoutMediator(binding.tabLayout,binding.viewPager2) {tab, position ->
            tab.text = title[position]
        }.attach()



    }
}