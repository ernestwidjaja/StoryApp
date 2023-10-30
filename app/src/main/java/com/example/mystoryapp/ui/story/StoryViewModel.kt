package com.example.mystoryapp.ui.story

import androidx.lifecycle.ViewModel
import com.example.mystoryapp.data.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun addStories(
        multipartBody: MultipartBody.Part,
        descriptionBody: RequestBody,
        latBody: RequestBody,
        lonBody: RequestBody,
        isShowLocation: Boolean
    ) =
        storyRepository.addStories(multipartBody, descriptionBody, latBody, lonBody, isShowLocation)
}