package com.tsdev.trello_clone.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.tsdev.trello_clone.R
import com.tsdev.trello_clone.firebase.FirestoreClass
import com.tsdev.trello_clone.models.User
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        auth = FirebaseAuth.getInstance()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setUpActionBar()
    }

    fun signInSuccess(user: User){
        hideProgressDialog()
        startActivity(Intent(this,MainActivity::class.java))
    }

    private fun setUpActionBar() {
        setSupportActionBar(toolbar_sign_in_activity)

        val actionbar = supportActionBar
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.back)
        }

        toolbar_sign_in_activity.setNavigationOnClickListener { onBackPressed() }

        btn_sign_in.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {

        val email : String = et_email_signIn.text.toString().trim{ it <= ' '}
        val password : String = et_password_signIn.text.toString().trim{ it <= ' '}

        if (validateForm(email,password)){
            showProgressDialog("Please wait")
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
                task ->
                hideProgressDialog()
                if (task.isSuccessful){
                    FirestoreClass().loadUserData(this)

                } else{
                    Toast.makeText(baseContext,"Authentication Failed!!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validateForm(email: String, password: String): Boolean{
        return when {
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