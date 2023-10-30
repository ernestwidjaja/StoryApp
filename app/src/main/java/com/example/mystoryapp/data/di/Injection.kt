package com.example.mystoryapp.data.di

import android.content.Context
import com.example.mystoryapp.data.StoryRepository
import com.example.mystoryapp.data.api.ApiConfig
import com.example.mystoryapp.data.database.StoryDatabase
import com.example.mystoryapp.data.pref.UserPreferences
import com.example.mystoryapp.data.pref.dataStore

object Injection {
    fun provideRepository(context: Context) : StoryRepository {
        val userPreferences = UserPreferences.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        val database = StoryDatabase.getDatabase(context)
        return StoryRepository.getInstance(apiService, userPreferences, database)
    }
}