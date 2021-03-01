package com.example.codingchallenge

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.lifecycle.lifecycleScope
import com.example.codingchallenge.Constants.Companion.CHECK_UPDATED_KEY
import com.example.codingchallenge.Constants.Companion.DATA_STORE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainActivityViewModel : MainActivityViewModel by viewModels()
    private lateinit var dataStore : DataStore<Preferences>
    private val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeDataStore()
        checkDataForUpdate()
    }

    private fun checkDataForUpdate() {
        val dataStoreKey = booleanPreferencesKey(CHECK_UPDATED_KEY)
        lifecycleScope.launch(IO) {
            val preference = dataStore.data.first()
            if(preference[dataStoreKey] == null){
                Log.i(TAG, "checkDataForUpdate: ${preference[dataStoreKey]}")
            }
        }
    }

    private fun initializeDataStore() {
        dataStore = this.createDataStore(DATA_STORE)
    }


}