package com.example.dicodingstory.ui.newstory

import androidx.lifecycle.ViewModel
import com.example.dicodingstory.data.remote.StoryAppRepository
import java.io.File

class NewStoryViewModel (private val storyAppRepository: StoryAppRepository): ViewModel() {
    fun uploadStory(imageFile: File, description:String, lat:Double?,lon:Double?)=storyAppRepository.uploadStory(imageFile, description, lat,lon)
}