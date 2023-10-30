package com.example.mystoryapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.cachedIn
import com.example.mystoryapp.data.StoryRepository
import com.example.mystoryapp.data.pref.UserModel
import kotlinx.coroutines.launch

class MainViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return storyRepository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            storyRepository.logout()
        }
    }

    fun getStoriesInfo() = storyRepository.getStoriesInfo()

    @ExperimentalPagingApi
    val story = storyRepository.getStories().cachedIn(viewModelScope)
}