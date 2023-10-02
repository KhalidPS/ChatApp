package com.k.sekiro.chatapp.ui.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickMultipleVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.k.sekiro.chatapp.R
import com.k.sekiro.chatapp.data.Message
import com.k.sekiro.chatapp.data.MessageNotification
import com.k.sekiro.chatapp.data.Notification
import com.k.sekiro.chatapp.data.PushNotification
import com.k.sekiro.chatapp.databinding.ActivityChatBinding
import com.k.sekiro.chatapp.databinding.BottomSheetBinding
import com.k.sekiro.chatapp.ui.Adapter.ChatAdapter
import com.k.sekiro.chatapp.ui.dialogs.BottomSheet
import com.k.sekiro.chatapp.viewModel.ViewModelApp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import android.util.Base64
import android.widget.ProgressBar
import com.google.firebase.storage.FirebaseStorage
import com.k.sekiro.chatapp.converters.ImageConverts.Companion.convertBitmapToBase64
import com.k.sekiro.chatapp.data.MultiImages
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.util.Date
import javax.inject.Inject


@AndroidEntryPoint
class Chat : AppCompatActivity() {
    lateinit var binding: ActivityChatBinding

    @Inject
    lateinit var sharedPref: SharedPreferences


    lateinit var bottomSheetBinding: BottomSheetBinding



  //  private val messages = ArrayList<Message>()

      lateinit var requestPermission: ActivityResultLauncher<String>

      lateinit var pickImage:ActivityResultLauncher<PickVisualMediaRequest>

      lateinit var pickMultiImage:ActivityResultLauncher<PickVisualMediaRequest>

    val viewModelApp: ViewModelApp by viewModels()


        lateinit var layoutManager: LinearLayoutManager






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)

        bottomSheetBinding = BottomSheetBinding.inflate(layoutInflater)

        val friendName = intent.getStringExtra("friendName")
        val friendStatus = intent.getBooleanExtra("friendStatus", false)
        val friendImg = intent.getStringExtra("friendImg")
        val friendId = intent.getStringExtra("friendId")!!


        requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()){

            if (it){
                val bottomSheet = BottomSheet(viewModelApp)
                bottomSheet.show(supportFragmentManager,"bottom_sheet")
            }else{

                  startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package",packageName,null)))
                    Snackbar.make(binding.root,"Allow microphone permission",Snackbar.LENGTH_SHORT).show()

            }

        }


        pickImage = registerForActivityResult(PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                Log.d("ks", "Selected URI: $uri")

                val sender = sharedPref.getString("userId", "") as String
                val senderImg =  sharedPref.getString("userImg","")!!


                viewModelApp.sendImageMsg(uri,contentResolver,sender,friendId,senderImg)




            /*  val storageReference =   storage.reference.child("images")

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                    val img = ImageDecoder.createSource(contentResolver,uri)
                    val bitmap = ImageDecoder.decodeBitmap(img)

                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val data = baos.toByteArray()

                    val imgReference = storageReference.child("${System.currentTimeMillis()}chat.jpeg")
                    val uploadTask = imgReference.putBytes(data)
                    uploadTask.addOnFailureListener {
                        // Handle unsuccessful uploads
                    }.addOnSuccessListener { taskSnapshot ->




                            val sender = sharedPref.getString("userId", "") as String
                            val senderImg =  sharedPref.getString("userImg","")!!

                           imgReference.downloadUrl.addOnSuccessListener {
                               Log.e("ks","the url $it")

                               val url = it.toString()

                               val message = Message(sender, friendId,url, Date(),"img", senderImg)

                               viewModelApp.sendMessage(message)
                            }





                    }

                }*/



            } else {
                Log.d("ks", "No media selected")
            }
        }



        pickMultiImage = registerForActivityResult(PickMultipleVisualMedia()) { uris ->

            val date = Date()

            if (uris != null) {

                val sender = sharedPref.getString("userId", "") as String
                val senderImg = sharedPref.getString("userImg", "")!!

                if (uris.size > 1) {

                    val placeHolderImages = ArrayList<String>()
                    placeHolderImages.add(uris[0].toString())
                    placeHolderImages.add(uris[1].toString())
                    placeHolderImages.add(uris[2].toString())
                    placeHolderImages.add(uris[3].toString())
                    // هاد بس عشان يظهر لليوزر انو في صور يتم ارسالها

                    viewModelApp.sendMessage(
                        MultiImages(
                            sender, friendId,"",date,"multiImages",senderImg,placeHolderImages
                        )
                    )




                    val directoryName = Date().toString()
                    viewModelApp.sendMultiImages(uris,directoryName,contentResolver,sender, friendId, senderImg)



                    viewModelApp.multiImagesLiveData.observe(this){

                        findViewById<ProgressBar>(R.id.loading).visibility = View.GONE


                               viewModelApp.deletePlaceholderImages(date)


                            Log.e("ks","all multi images: ${it.size}")
                            viewModelApp.sendMessage(
                                MultiImages(
                                    sender,
                                    friendId,
                                    directoryName,
                                    Date(),
                                    "multiImages",
                                    senderImg,
                                    it
                                )
                            )



                        }


                } else {

                    val sender = sharedPref.getString("userId", "") as String
                    val senderImg = sharedPref.getString("userImg", "")!!


                    viewModelApp.sendImageMsg(uris[0], contentResolver, sender, friendId, senderImg)

                }

            }
        }




       /*  launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if (it.data != null && it.resultCode == Activity.RESULT_OK){

                    Log.e("ks","yes done select img")

            }
        }*/




         viewModelApp.getUserStatusRealtime(friendId)


        binding.userName.text = friendName


        viewModelApp.userStatusLiveData.observe(this){
            if (it) {
                binding.userState.text = "online"
                binding.colorState.setImageResource(R.drawable.state_online_without_stroke)
            } else {
                binding.userState.text = "offline"
                binding.colorState.setImageResource(R.drawable.state_offline_without_stroke)
            }

        }



        binding.backBtn.setOnClickListener {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
            finish()
        }


        binding.messageField.doOnTextChanged { text, start, before, count ->

            if (text!!.isNotEmpty()) {
                binding.micBtn.visibility = View.GONE
                binding.sendBtn.visibility = View.VISIBLE
            } else {
                binding.sendBtn.visibility = View.GONE
                binding.micBtn.visibility = View.VISIBLE
            }
        }



        setContentView(binding.root)
    }


    override fun onStart() {
        super.onStart()

        val friendId = intent.getStringExtra("friendId")!!
        val senderImg =  sharedPref.getString("userImg","")!!
        val friendToken = intent.getStringExtra("friendToken")!!



        Log.e("ks","sender img before clcik button ${senderImg}")


        val sender = sharedPref.getString("userId", "") as String



    /////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////

        binding.sendBtn.setOnClickListener {
            val message = binding.messageField.text.toString()
            Log.e("ks","here sender img ${senderImg}")
            viewModelApp.sendMessage(Message(sender, friendId, message, Date(),"text", senderImg))
            binding.messageField.text.clear()


            val senderName = sharedPref.getString("userName","")!!

            viewModelApp.sendNotification(
                PushNotification(
                    MessageNotification(
                        Notification("$senderName sent message for you ","Message"),
                        friendToken
                    )
                )
            )

            Log.e("ks",  PushNotification(
                MessageNotification(
                    Notification("$senderName sent message for you ","Message"),
                    friendToken
                )
            ).toString())



            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken,0)

        }


    /////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////


        viewModelApp.getMessages(sender,friendId)

        viewModelApp.userMessagesLiveData.observe(this){
            /*val sortedMsg = it.sortedWith(compareBy {msg->
                msg.time
            })*/
             val adapter = ChatAdapter(this, it)
            binding.rvChat.adapter = adapter
             layoutManager = LinearLayoutManager(this)
            layoutManager.stackFromEnd = true
            binding.rvChat.layoutManager = layoutManager
            //  binding.rvChat.adapter!!.notifyItemRangeInserted(sortedMsg.size,sortedMsg.size)

          /*  if (it.isNotEmpty()) {
                binding.rvChat.scrollToPosition(it.size - 1)
            }*/

            layoutManager.findFirstVisibleItemPosition()

        }


        binding.rvChat.viewTreeObserver.addOnScrollChangedListener {
            if (layoutManager.findLastVisibleItemPosition() != binding.rvChat.adapter!!.itemCount -1){
                binding.arrowDown.visibility = View.VISIBLE
            }else{
                binding.arrowDown.visibility = View.GONE
            }
        }


        binding.arrowDown.setOnClickListener {
            binding.rvChat.smoothScrollToPosition(binding.rvChat.adapter!!.itemCount -1)
        }







       // getMessages(sender, friendId)

       // Log.e("ks", "messages array after method: ${messages.toString()}")


        //viewModelApp.getMessages(sender,friendId)

        /*  viewModelApp.userMessagesLiveData.observe(this){
            binding.rvChat.adapter = ChatAdapter(this,it)
            Log.e("ks","inside chat activity: "+it.toString())

        }*/


    }


    override fun onResume() {
        super.onResume()


        binding.addMedia.setOnClickListener {

           // pickImage.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))

            pickMultiImage.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))


        }



        binding.micBtn.setOnClickListener {

            requestPermission.launch(android.Manifest.permission.RECORD_AUDIO)


        }


        /* fun getMessages(senderId: String, receiverId: String) {


        db.collection(DatabaseReferences.MessagesCollection)
            .addSnapshotListener { messagesC, error ->
                messages.clear()
                for (message in messagesC!!) {
                    val msg = message.toObject(Message::class.java)
                    if (senderId == msg.senderId && receiverId == msg.receiverId
                        || senderId == msg.receiverId && receiverId == msg.senderId
                    ) {
                        messages.add(msg)
                    }
                }


                val sortedMsg = messages.sortedWith(compareBy {
                    it.time
                })
                val adapter = ChatAdapter(this, sortedMsg)
                binding.rvChat.adapter = adapter
                var layoutManager = LinearLayoutManager(this)
                layoutManager.stackFromEnd = true
                binding.rvChat.layoutManager = layoutManager
                //  binding.rvChat.adapter!!.notifyItemRangeInserted(sortedMsg.size,sortedMsg.size)
                if (sortedMsg.isNotEmpty()) {
                    binding.rvChat.scrollToPosition(sortedMsg.size - 1)
                }



                Log.e("ks", "all messages , ${messages.toString()}")
            }


    }*/

    }






}