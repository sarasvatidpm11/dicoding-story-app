package com.example.dicodingstory.ui.maps

import androidx.lifecycle.ViewModel
import com.example.dicodingstory.data.remote.StoryAppRepository

class MapsViewModel (private val storyAppRepository: StoryAppRepository): ViewModel() {
    fun getStoriesLocation() = storyAppRepository.getStoriesLocation()
}