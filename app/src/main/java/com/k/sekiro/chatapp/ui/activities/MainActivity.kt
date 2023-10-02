package com.k.sekiro.chatapp.ui.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.k.sekiro.chatapp.R
import com.k.sekiro.chatapp.database.DatabaseReferences
import com.k.sekiro.chatapp.databinding.ActivityMainBinding
import com.k.sekiro.chatapp.viewModel.ViewModelApp
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding:ActivityMainBinding

    @Inject lateinit var sharedPref: SharedPreferences
    @Inject lateinit var db:FirebaseFirestore


    val viewModelApp:ViewModelApp by  viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setSupportActionBar(binding.include.toolbar)

        val bottomNavView = binding.bottomNav


        val navController = findNavController(R.id.fragmentContainerView)

        val appBarConfiguration = AppBarConfiguration(setOf(R.id.message, R.id.addFriend, R.id.profile))

        setupActionBarWithNavController(navController, appBarConfiguration)

        bottomNavView.setupWithNavController(navController)


        supportActionBar!!.setDisplayShowTitleEnabled(false)




        findNavController(R.id.fragmentContainerView).addOnDestinationChangedListener{nav,destination,args->
            when(destination.id){
                R.id.message -> {
                    binding.include.appTitle.text = "Message"
                    binding.include.notificationBtn.visibility = View.VISIBLE
                    binding.include.listFriendBtn.root.visibility = View.GONE
                }

                R.id.profile -> {
                    binding.include.appTitle.text = "Profile"
                    binding.include.notificationBtn.visibility = View.GONE
                    binding.include.listFriendBtn.root.visibility = View.GONE
                }

                R.id.addFriend->{
                    binding.include.appTitle.text = "Add Friend"
                    binding.include.notificationBtn.visibility = View.GONE
                    binding.include.listFriendBtn.root.visibility = View.VISIBLE
                }

            }
        }


        viewModelApp.getFriendReqCount(sharedPref.getString("userId","")!!)

        viewModelApp.friendRequestCounterLiveData.observe(this){
            Log.e("ks","here req counter : $it")

            if (it == 0){
                binding.include.listFriendBtn.numRequest.visibility = View.GONE
                binding.include.listFriendBtn.numberReqHolder.visibility = View.GONE
            }else{
                binding.include.listFriendBtn.numRequest.visibility = View.VISIBLE
                binding.include.listFriendBtn.numberReqHolder.visibility = View.VISIBLE
                binding.include.listFriendBtn.numRequest.text = "$it"
            }
        }






        binding.include.listFriendBtn.root.setOnClickListener {

            val i = Intent(this, FriendRequestActivity::class.java)
            startActivity(i)
        }


     /*   FirebaseInstallations.getInstance().id.addOnCompleteListener {
            if (it.isSuccessful) {
                sharedPref.edit().putString("token", it.result).apply()
                val userId = sharedPref.getString("userId", "")!!
                Log.e("ks", "this is my token : ${it.result}")
                db.collection(DatabaseReferences.UsersCollection).document(userId)
                    .update("token", it.result)
            }
        }*/




       FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful){
                sharedPref.edit().putString("token",it.result).apply()
                val userId = sharedPref.getString("userId","")!!
                Log.e("ks","this is my token : ${it.result}")
                db.collection(DatabaseReferences.UsersCollection).document(userId)
                    .update("token",it.result)
            }
        }









     /*   binding.include.appTitle.setOnClickListener {
            navController.navigate(R.id.move_to_home)
        }*/





       /* val radius = 25f
        val windowBackground = window.decorView.background

        binding.bottomBlur.setupWith(binding.root)
            .setFrameClearDrawable(windowBackground)
            .setBlurRadius(radius)
            .setBlurEnabled(true)*/

    }
}