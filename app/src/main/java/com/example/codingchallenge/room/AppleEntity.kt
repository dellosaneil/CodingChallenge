package com.example.codingchallenge.room

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "apple_table")
data class AppleEntity(
    val trackName : String,
    val artWork : String? = null,
    val buyPrice : Double,
    val rentPrice : Double,
    val releaseDate : String,
    val genre : String,
    val description : String,
    val duration : Int,
    val timeInserted : Long = System.currentTimeMillis()
) : Parcelable {
    @IgnoredOnParcel
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0
}