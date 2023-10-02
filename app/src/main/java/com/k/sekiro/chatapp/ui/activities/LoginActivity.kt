package com.k.sekiro.chatapp.ui.activities

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.k.sekiro.chatapp.R
import com.k.sekiro.chatapp.data.User
import com.k.sekiro.chatapp.database.DatabaseReferences
import com.k.sekiro.chatapp.databinding.ActivityLoginBinding
import com.k.sekiro.chatapp.repository.UserAuth
import com.k.sekiro.chatapp.viewModel.ViewModelApp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.regex.Pattern
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {



    lateinit var binding: ActivityLoginBinding

    @Inject
    lateinit var repository:UserAuth

    @Inject
    lateinit var auth:FirebaseAuth

    @Inject
    lateinit var db:FirebaseFirestore

    @Inject lateinit var sharedPref: SharedPreferences

    private val viewModelApp:ViewModelApp by viewModels()/*{ViewModelApp.ViewModelFactory(repository)}*/


    var noErrorEmail = false
    var noErrorPassword = false



   private val registerForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK){
            val account = GoogleSignIn.getSignedInAccountFromIntent(it.data).result
            Log.e("ks","arrive account")

            signInWithGoogleCredential(account)


            val edit = sharedPref.edit()
            edit.putString("userEmail",account.email)
            edit.putString("userName",account.displayName)
            edit.putString("userImg",account.photoUrl.toString())
            edit.putBoolean("isActive",true)
            edit.commit()

            var exist = false



            lifecycleScope.launch(Dispatchers.IO) {
                db.collection(DatabaseReferences.UsersCollection).get()
                    .addOnSuccessListener {
                        for (row in it){
                            if (row.data.get("email") == "${account.email}"){
                                exist = true
                                sharedPref.edit().putString("userId",row.id).commit()
                                Log.e("ks","break loop")
                                break
                            }
                        }

                        if (!exist){

                            Log.e("ks","add account to firebase")
                            val appId = account.displayName!!.replace(" ","")+"#${SignUp.set.elementAt(0)}"

                            val userdata = User(
                                "","${account.displayName}"
                                ,"${account.email}","",
                                "",true,account.photoUrl.toString(),appId,ArrayList<String>()
                            )


                            viewModelApp.saveGoogleAccountDataToFireStore(userdata)
                        }

                    }
            }

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        if (intent.extras!=null){
           val created =  intent.getBooleanExtra("isCreated",false)
            if (created){
               val snack = Snackbar.make(binding.root,this.getString(R.string.accountCreatedMsg),Snackbar.LENGTH_LONG)
                snack.setAction(this.getString(R.string.dismiss)){
                    snack.dismiss()
                }
                snack.show()
            }
        }

    }


    override fun onStart() {
        super.onStart()

        binding.signGoogle.setOnClickListener {
             /*  val intent =  viewModelApp.signInWithGoogle()
                registerForResult.launch(intent)*/
            viewModelApp.signInWithGoogle()

        }

        viewModelApp.signInGoogleLiveData.observe(this){
            registerForResult.launch(it)
        }




        binding.register.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }



    checkEmailSyntax()
    checkPasswordSyntax()




       binding.loginBtN.setOnClickListener {
             Log.e("ks","login clicked")
             if (noErrorEmail && noErrorPassword){
                 val email = binding.emailField.text.toString().trim()
                 val password = binding.passwordField.text.toString()
                 Log.e("ks","in first if")
                 auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {


                     val editor = sharedPref.edit()

                     lifecycleScope.launch(Dispatchers.IO){
                         db.collection(DatabaseReferences.UsersCollection).get()
                             .addOnSuccessListener {
                                 for(user in it){
                                     if (user.get("email") == email){
                                         editor.putString("userId",user.id).putString("userName",user.getString("name")).commit()
                                         Log.e("ks","${user.id}")
                                         break
                                     }
                                 }
                             }.await()


                         withContext(Dispatchers.Main){

                             if (binding.checkBox.isChecked){
                                 editor.putString("email",email)
                                 editor.putBoolean("isActive",true)
                                 editor.commit()
                             }else{
                                 editor.putString("email",email)
                                 editor.putBoolean("isActive",false)
                                 editor.commit()
                             }


                             Log.e("ks","in auth success")
                             val intent = Intent(this@LoginActivity, MainActivity::class.java)
                             intent.putExtra("email",email)
                             startActivity(intent)
                             finish()


                         }

                     }





                 }.addOnFailureListener {

                    val snack =  Snackbar.make(binding.root,"${it.message}",Snackbar.LENGTH_LONG)
                     snack.setAction(this.getString(R.string.dismiss)){
                         snack.dismiss()
                     }

                     snack.show()
                 }



             }
         }



    }

    ////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////

    override fun onResume() {
        super.onResume()

        binding.forgetPassword.setOnClickListener {
            auth.sendPasswordResetEmail("khalidgoal1@gmail.com").addOnSuccessListener {

            }





        }
    }



    private fun signInWithGoogleCredential(account:GoogleSignInAccount){
        val credential = GoogleAuthProvider.getCredential(account.idToken,null)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.signInWithCredential(credential).await()
                withContext(Dispatchers.Main){
                    Toast.makeText(this@LoginActivity,"successfully sign in",Toast.LENGTH_SHORT).show()
                    Toast.makeText(this@LoginActivity,"email is : ${account.email}, name:${account.displayName}",Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            }catch (e:Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(this@LoginActivity,"${e.message}",Toast.LENGTH_SHORT).show()
                }
            }

        }
    }



    fun checkEmailSyntax(){
        binding.emailField.doOnTextChanged { text, start, before, count ->
            val pattern = Pattern.compile("^[a-zA-z]+[0-9]*+@+[a-z]+[.]+[a-z]+")
            val matcher = pattern.matcher(text!!.trim())
            if (!matcher.matches()){
                binding.emailLayout.error = "email should be like example@email.com"
                noErrorEmail = false
            }else{
                binding.emailLayout.error = null
                noErrorEmail = true
            }

        }
    }

    fun checkPasswordSyntax(){
        binding.passwordField.doOnTextChanged { text, start, before, count ->
            val pattern = Pattern.compile("^[a-zA-z]+[a-zA-Z0-9\\s!@#$%^&*+=]+")
            val matcher = pattern.matcher(text)

            if (text!!.length < 8){
                binding.passwordLayout.error = "password should be 8 letters or more"
                noErrorPassword = false
            }else if (!matcher.matches()){
                binding.passwordLayout.error = "password should contains letter and signs like $ and number"
                noErrorPassword = false
            }else{
                binding.passwordLayout.error = null
                noErrorPassword = true
            }
        }
    }



}