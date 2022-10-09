package com.tsdev.trello_clone.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.tsdev.trello_clone.R
import com.tsdev.trello_clone.firebase.FirestoreClass
import com.tsdev.trello_clone.models.Board
import com.tsdev.trello_clone.utils.Constants
import kotlinx.android.synthetic.main.activity_board.*
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.IOException

class BoardActivity : BaseActivity() {
    private var mSelectedImageFileUri: Uri? = null

    private lateinit var mUserName:String

    private var mBoardImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        setupActionBar()

        if (intent.hasExtra(Constants.NAME)){
            mUserName = intent.getStringExtra(Constants.NAME).toString()
        }

        iv_board_image.setOnClickListener{
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

        btn_create.setOnClickListener {
            if (mSelectedImageFileUri!= null){
                uploadBoardImage()
            } else{
                showProgressDialog("Please wait")
                createBoard()
            }
        }
    }

    fun boardCreatedSuccessfully(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun createBoard(){
        val assignedUserArrayList: ArrayList<String> = ArrayList()
        assignedUserArrayList.add(getCurrentUserID())

        var board = Board(
            et_board_name.text.toString(),
            mBoardImageURL, mUserName,
            assignedUserArrayList
        )

        FirestoreClass().createBoard(this,board)
    }

    private fun uploadBoardImage(){
        showProgressDialog("Please Wait")

            val sRef : StorageReference = FirebaseStorage.getInstance().reference.child("Board_Image"+System.currentTimeMillis()
                    +"."+Constants.getFileExtension(this,mSelectedImageFileUri))

            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                    taskSnapshot ->
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        uri ->
                    mBoardImageURL = uri.toString()
                    createBoard()
                }
            }.addOnFailureListener{
                    exception->
                Toast.makeText(this@BoardActivity, exception.message,Toast.LENGTH_SHORT).show()
                hideProgressDialog()
            }

    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_create_board_activity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.back)
            actionBar.title = "Create Board"
        }

        toolbar_create_board_activity.setNavigationOnClickListener {
            onBackPressed()
        }

        iv_board_image.setOnClickListener{
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
            Toast.makeText(this,"You just denied for the storage!!", Toast.LENGTH_SHORT).show()
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data!!.data !=null){
            mSelectedImageFileUri = data.data

            try {
                Glide
                    .with(this@BoardActivity)
                    .load(mSelectedImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_board_place_holder)
                    .into(iv_board_image)
            }catch (e: IOException){
                e.printStackTrace()
            }

        }
    }
}