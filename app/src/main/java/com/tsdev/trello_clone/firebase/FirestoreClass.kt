package com.tsdev.trello_clone.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.tsdev.trello_clone.activities.*
import com.tsdev.trello_clone.models.Board
import com.tsdev.trello_clone.models.User
import com.tsdev.trello_clone.utils.Constants

class FirestoreClass {

    private val mFirestore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignupActivity, userInfo: User){
        mFirestore.collection(Constants.USERS).document(getCurrentUserID())
            .set(userInfo, SetOptions.merge()).addOnSuccessListener {
                activity.userRegisteredSuccess()
            }.addOnFailureListener {
                e ->
                Log.e(activity.javaClass.simpleName,"Error")
            }
    }

    fun createBoard(activity: BoardActivity, board: Board){
        mFirestore.collection(Constants.BOARDS).document()
            .set(board, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(activity, "Board created successfully!!", Toast.LENGTH_SHORT).show()
                activity.boardCreatedSuccessfully()
            }.addOnFailureListener {
                exception->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,
                "Error while creating",exception)
            }
    }

    fun getBoardsList(activity: MainActivity){
        mFirestore.collection(Constants.BOARDS)
            .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserID())
            .get()
            .addOnSuccessListener {
                document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())
                val boardList: ArrayList<Board> = ArrayList()
                for(i in document.documents){
                    val board = i.toObject(Board::class.java)!!
                    board.documentId = i.id
                    boardList.add(board)
                }


                activity.populateBoardsListToUi(boardList)
            }.addOnFailureListener {
                e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a board",e)
            }
    }


    fun updateUserProfileData(activity: ProfileActivity, userHashMap: HashMap<String,Any>){
        mFirestore.collection(Constants.USERS).document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                Toast.makeText(activity, "Profile Update Successfully", Toast.LENGTH_SHORT).show()
                activity.profileUpdateSuccess()
            }.addOnFailureListener {
                e->
                activity.hideProgressDialog()
                Toast.makeText(activity, "Profile Update Error", Toast.LENGTH_SHORT).show()
            }
    }

    fun loadUserData(activity: Activity, readBoardList: Boolean = false){
        mFirestore.collection(Constants.USERS).document(getCurrentUserID())
            .get().addOnSuccessListener {
                document->
                val loggedInUser = document.toObject(User::class.java)
                if (loggedInUser != null){

                   when(activity){
                       is SignInActivity ->{
                           activity.signInSuccess(loggedInUser)
                       }
                       is MainActivity->{
                           activity.updateNavigationUserDetails(loggedInUser,readBoardList)
                       }
                       is ProfileActivity->{
                           activity.setUserDataInUI(loggedInUser)
                       }
                   }

                }

            }.addOnFailureListener {

                    e ->
                when(activity){
                    is SignInActivity ->{
                        activity.hideProgressDialog()
                    }
                    is MainActivity->{
                        activity.hideProgressDialog()
                    }
                }
                Log.e("signInUser","Error")
            }
    }

    fun getCurrentUserID(): String {
        // An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

    fun getBoardsDetails(activity: TaskListActivity, boardDocumentId: String) {
        mFirestore.collection(Constants.BOARDS)
            .document(boardDocumentId)
            .get()
            .addOnSuccessListener {
                    document ->
                Log.i(activity.javaClass.simpleName, document.toString())
                activity.boardDetails(document.toObject(Board::class.java)!!  )

            }.addOnFailureListener {
                    e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a board",e)
            }

    }
}