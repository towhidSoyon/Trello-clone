package com.tsdev.trello_clone.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tsdev.trello_clone.R
import com.tsdev.trello_clone.firebase.FirestoreClass
import com.tsdev.trello_clone.models.Board
import com.tsdev.trello_clone.utils.Constants
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_task_list2.*

class TaskListActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list2)

        var boardDocumentId =""
        if(intent.hasExtra(Constants.DOCUMENT_ID)){
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardsDetails(this, boardDocumentId)

    }

    private fun setupActionBar(title: String){
        setSupportActionBar(toolbar_taskList_activity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.back)
            actionBar.title = title

        }

        toolbar_taskList_activity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun boardDetails(board: Board){
        hideProgressDialog()
        setupActionBar(board.name)

    }
}