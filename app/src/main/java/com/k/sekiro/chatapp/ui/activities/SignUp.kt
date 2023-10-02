package com.k.sekiro.chatapp.ui.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.k.sekiro.chatapp.R
import com.k.sekiro.chatapp.data.User
import com.k.sekiro.chatapp.database.DatabaseReferences
import com.k.sekiro.chatapp.databinding.ActivitySignUpBinding
import com.k.sekiro.chatapp.databinding.LoadingDialogEmailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.regex.Pattern
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class SignUp : AppCompatActivity() {
    lateinit var binding:ActivitySignUpBinding

    @Inject lateinit var auth: FirebaseAuth

    @Inject lateinit var db:FirebaseFirestore

    lateinit var builder:AlertDialog

    var noError = false
    var noErrorName = false
    var noErrorEmail = false
    var noErrorPassword = false

    // 332dp 222dp


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)



    }

    override fun onStart() {
        super.onStart()
        checkNameSyntax()

        checkEmailSyntax()

        checkPasswordSyntax()

    }

    override fun onResume() {
        super.onResume()

        val loadingBinding = LoadingDialogEmailBinding.inflate(layoutInflater)

        binding.signupBtn.setOnClickListener {
            if (noErrorName && noErrorEmail && noErrorPassword){
                var name = binding.nameField.text.toString().trim()
                var email = binding.emailField.text.toString().trim()
                var password = binding.passwordField.text.toString()

                auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener {reslut->

                    reslut.user!!.sendEmailVerification().addOnSuccessListener {
                        val dialog = AlertDialog.Builder(this)
                            .setCancelable(false)


                        if (loadingBinding.root.parent != null){ //  شرط لحذف تصميم  من dialog في حال كان موجود قبل لتجنب اكسبشن
                            val parent = loadingBinding.root.parent as ViewGroup
                            parent.removeView(loadingBinding.root)
                        }
                        dialog.setView(loadingBinding.root)
                        builder = dialog.create()
                        builder.show()


                        val exceptionHandler = CoroutineExceptionHandler{_,throwable->
                            Toast.makeText(this,"${throwable.message}",Toast.LENGTH_SHORT).show()
                        }

                            lifecycleScope.launch (Dispatchers.IO + exceptionHandler){


                                val appId = name.replace(" ","")+"#${set.elementAt(0)}"

                                val user  = User("",name,email,password,"",false,"",appId,
                                   ArrayList<String>()
                                )
                                checkEmailVerify(loadingBinding,user)

                            }





                    }


                }
            }


        }
    }


    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    fun checkNameSyntax(){
        binding.nameField.doOnTextChanged { text, start, before, count ->
            val pattern = Pattern.compile("^[a-zA-z\\s]+")
            val matcher = pattern.matcher(text!!.trim())
            if (!matcher.matches()){
                binding.nameLayout.error = this.getString(R.string.NameErrorStarterCharacter)
                noErrorName = false
            }else{
                binding.nameLayout.error = null
                noErrorName = true
            }
        }
    }

    fun checkEmailSyntax(){
        binding.emailField.doOnTextChanged { text, start, before, count ->
            val pattern = Pattern.compile("^[a-zA-z]+[0-9]*+@+[a-z]+[.]+[a-z]+")
            val matcher = pattern.matcher(text!!.trim())
            if (!matcher.matches()){
                binding.emailLayout.error = this.getString(R.string.emailErrorSyntax)
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
                binding.passwordLayout.error = this.getString(R.string.passwordErrorLength)
                noErrorPassword = false
            }else if (!matcher.matches()){
                binding.passwordLayout.error = this.getString(R.string.passwordErrorSyntax)
                noErrorPassword = false
            }else{
                binding.passwordLayout.error = null
                noErrorPassword = true
            }
        }
    }


    suspend fun checkEmailVerify(loadingBinding: LoadingDialogEmailBinding,userData: User){
        try {
            var count = 60
            while(true){
                withContext(Dispatchers.Main){
                    loadingBinding.counterLoading.text = "$count"
                    Log.e("ks","counter:$count")
                }

                if (count == 0){
                    auth.currentUser!!.delete()
                    Snackbar.make(binding.root,this.getString(R.string.timeExpired),Snackbar.LENGTH_SHORT).show()
                    builder.dismiss()
                    break
                }
                auth.currentUser!!.reload()
                val user = auth.currentUser
                Log.e("ks","${user!!.isEmailVerified}")
                if (user!!.isEmailVerified){
                    Log.e("ks","verify done")
                    builder.dismiss()

                    db.collection(DatabaseReferences.UsersCollection).add(userData)
                        .addOnSuccessListener {
                            Log.e("ks","add account to fireStore done")
                        }.addOnFailureListener {
                            Log.e("ks","add account to fireStore fail")
                        }



                    val intent = Intent(this@SignUp, LoginActivity::class.java)
                    intent.putExtra("isCreated",true)
                    startActivity(intent)
                    finish()
                    break
                }
                delay(1000)
                --count

            }
        }catch (e:NullPointerException){
            Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show()
        }catch (e:IllegalStateException){
            Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show()
        }catch (e:ExceptionInInitializerError){
            Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show()
        }catch (e:Exception){
            Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show()
        }


    }


    companion object{
        val set = generateSequence { Random.nextInt(1011,9999) }
            .distinct()
            .take(1)
            .toSet()
    }

}

