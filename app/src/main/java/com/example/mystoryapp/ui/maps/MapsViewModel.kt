package com.example.mystoryapp.ui.maps

import androidx.lifecycle.ViewModel
import com.example.mystoryapp.data.StoryRepository

class MapsViewModel (private val storyRepository: StoryRepository) : ViewModel() {
    fun getStoriesLocation() = storyRepository.getStoriesLocation()
}