package com.tsdev.trello_clone.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.telecom.Call
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.tsdev.trello_clone.R
import com.tsdev.trello_clone.firebase.FirestoreClass
import com.tsdev.trello_clone.models.User
import com.tsdev.trello_clone.utils.Constants
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.IOException

class ProfileActivity : BaseActivity() {

    private var mSelectedImageFileUri: Uri? = null
    private lateinit var mUserDetails: User
    private var mProfileImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setupActionBar()

        FirestoreClass().loadUserData(this)

        iv_profile_image.setOnClickListener{
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
                    Constants.showImageChooser(this)
            }else{
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        btn_update.setOnClickListener {
            if (mSelectedImageFileUri != null){
                uploadUserImage()
            }else{
                showProgressDialog("Please Wait")
                updateUserProfileData()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
            if (grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }
        }else{
            Toast.makeText(this,"You just denied for the storage!!",Toast.LENGTH_SHORT).show()
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data!!.data !=null){
            mSelectedImageFileUri = data.data

            try {
                Glide
                    .with(this@ProfileActivity)
                    .load(mSelectedImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(iv_profile_image)
            }catch (e: IOException){
                e.printStackTrace()
            }

        }
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_my_profile_activity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.back)
            actionBar.title = "My Profile"

        }

        toolbar_my_profile_activity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun setUserDataInUI(user: User){

        mUserDetails=user

        Glide
            .with(this@ProfileActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(iv_profile_image)

        et_name.setText(user.name)
        et_email.setText(user.email)
        if (user.mobile != 0L){
            et_mobile.setText(user.mobile.toString())
        }
    }

    private fun updateUserProfileData(){
        val userHashMap = HashMap<String,Any>()

        var anyChangesMade = false
        if (mProfileImageURL.isNotEmpty() && mProfileImageURL!= mUserDetails.image){
            userHashMap[Constants.IMAGE] = mProfileImageURL

        }

        if (et_name.text.toString() != mUserDetails.name){
            userHashMap[Constants.NAME] = et_name.text.toString()

        }

        if (et_mobile.text.toString() != mUserDetails.mobile.toString()){
            userHashMap[Constants.MOBILE] = et_mobile.text.toString().toLong()

        }


            FirestoreClass().updateUserProfileData(this,userHashMap)


    }

    private fun uploadUserImage(){
        showProgressDialog("Please Wait")

        if (mSelectedImageFileUri!= null){
            val sRef : StorageReference = FirebaseStorage.getInstance().reference.child("User_Image"+System.currentTimeMillis()
            +"."+Constants.getFileExtension(this,mSelectedImageFileUri))

            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                taskSnapshot ->
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri ->
                    mProfileImageURL = uri.toString()
                    updateUserProfileData()
                }
            }.addOnFailureListener{
                exception->
                Toast.makeText(this@ProfileActivity, exception.message,Toast.LENGTH_SHORT).show()
                hideProgressDialog()
            }
        }

    }



    fun profileUpdateSuccess(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()

    }
}