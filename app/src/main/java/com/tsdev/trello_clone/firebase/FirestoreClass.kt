package com.tsdev.trello_clone.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.tsdev.trello_clone.activities.MainActivity
import com.tsdev.trello_clone.activities.SignInActivity
import com.tsdev.trello_clone.activities.SignupActivity
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

    fun signInUser(activity: SignInActivity){
        mFirestore.collection(Constants.USERS).document(getCurrentUserID())
            .get().addOnSuccessListener {
                document->
                val loggedInUser = document.toObject(User::class.java)
                if (loggedInUser != null){
                    activity.signInSuccess(loggedInUser)
                }

            }.addOnFailureListener {
                    e ->
                Log.e("signInUser","Error")
            }
    }

    fun getCurrentUserID():String{

        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""
        if (currentUserId != null){
            currentUserId = currentUser!!.uid
        }
        return currentUserId
    }
}