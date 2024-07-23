package com.example.dicodingstory.ui.auth.register

import androidx.lifecycle.ViewModel
import com.example.dicodingstory.data.remote.StoryAppRepository

class RegisterViewModel (private val storyAppRepository: StoryAppRepository) : ViewModel() {

    fun register(name: String, email: String, password: String)=storyAppRepository.register(name, email, password)
}