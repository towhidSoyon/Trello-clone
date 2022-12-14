package com.tsdev.trello_clone.models

import android.os.Parcel
import android.os.Parcelable

data class Task (
    var title:String = "",
    val createdBy: String = ""
        ): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun describeContents()=0

    override fun writeToParcel(p0: Parcel, p1: Int)= with(p0) {
        writeString(title)
        writeString(createdBy)
    }

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task {
            return Task(parcel)
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }
}