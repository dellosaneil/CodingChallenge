package com.example.codingchallenge.di

import android.content.Context
import androidx.room.Room
import com.example.codingchallenge.Constants.Companion.BASE_URL
import com.example.codingchallenge.Constants.Companion.DATABASE_NAME
import com.example.codingchallenge.retrofit.AppleApi
import com.example.codingchallenge.room.AppleDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Singleton
    @Provides
    fun provideAppleApi(retrofit: Retrofit): AppleApi =
        retrofit.create(AppleApi::class.java)

    @Singleton
    @Provides
    fun provideRoomDatabase(@ApplicationContext context : Context) : AppleDatabase =
        Room.databaseBuilder(context, AppleDatabase::class.java, DATABASE_NAME).build()

    @Provides
    @Singleton
    fun provideAppleDao(database : AppleDatabase) = database.appleDao()

}