package com.example.codingchallenge.room

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [AppleEntity::class], version = 2)
abstract class AppleDatabase: RoomDatabase() {
    abstract fun appleDao() : AppleDao
}