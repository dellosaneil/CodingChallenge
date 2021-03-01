package com.example.codingchallenge.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "apple_table")
data class AppleEntity(
    val trackName : String,
    val artWork : String? = null,
    val price : Double,
    val genre : String,
    val description : String,
    val timeInserted : Long = System.currentTimeMillis()
) {
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0
}