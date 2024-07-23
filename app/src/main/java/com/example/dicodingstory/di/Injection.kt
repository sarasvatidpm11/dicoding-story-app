package com.example.dicodingstory.di

import android.content.Context
import com.example.dicodingstory.data.local.database.StoryDatabase
import com.example.dicodingstory.data.remote.StoryAppRepository
import com.example.dicodingstory.data.retrofit.ApiConfig
import com.example.dicodingstory.ui.auth.pref.AuthPreferences
import com.example.dicodingstory.ui.auth.pref.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): StoryAppRepository {
        val pref = AuthPreferences.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        val database = StoryDatabase.getInstance(context)
        return StoryAppRepository.getInstance(apiService, pref, database)
    }
}