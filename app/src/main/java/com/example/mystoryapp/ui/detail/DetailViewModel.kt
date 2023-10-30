package com.example.mystoryapp.ui.detail

import androidx.lifecycle.ViewModel
import com.example.mystoryapp.data.StoryRepository

class DetailViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun getStoriesDetail(id: String) = storyRepository.getStoriesDetail(id)
}