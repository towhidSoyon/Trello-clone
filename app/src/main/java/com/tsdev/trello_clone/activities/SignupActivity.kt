package com.tsdev.trello_clone.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.tsdev.trello_clone.R
import com.tsdev.trello_clone.firebase.FirestoreClass
import com.tsdev.trello_clone.models.User
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setUpActionBar()
    }

    fun userRegisteredSuccess(){
        Toast.makeText(this,"you have successfully registered",Toast.LENGTH_SHORT).show()
        hideProgressDialog()
        FirebaseAuth.getInstance().signOut()
        finish()

    }

    private fun setUpActionBar(){
        setSupportActionBar(toolbar_sign_up_activity)

        val actionbar = supportActionBar
        if (actionbar != null){
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.back)
        }

        toolbar_sign_up_activity.setNavigationOnClickListener { onBackPressed() }

        btn_sign_up.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser(){
        val name : String = et_name.text.toString().trim{ it <= ' '}
        val email : String = et_email.text.toString().trim{ it <= ' '}
        val password : String = et_password.text.toString().trim{ it <= ' '}

        if(validateForm(name,email,password)){
            showProgressDialog("Please wait")
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                task ->

                if (task.isSuccessful){
                    val firebaseUser: FirebaseUser = task.result!!.user!!
                    val registeredEmail = firebaseUser.email!!
                    val user = User(firebaseUser.uid, name,registeredEmail)

                    FirestoreClass().registerUser(this,user)
                }else{
                    Toast.makeText(this,"Registration Failed",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun validateForm(name: String, email: String, password: String): Boolean{
        return when {
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar("Please Enter a name")
                false
            }
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please Enter an email")
                false
            }

            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please Enter password")
                false
            }else->{
                return true
            }
        }
    }
}