package com.example.dicodingstory.ui.auth.login

import androidx.lifecycle.ViewModel
import com.example.dicodingstory.data.remote.StoryAppRepository

class LoginViewModel (private val storyAppRepository: StoryAppRepository) : ViewModel() {

    fun login(email: String, password: String)=storyAppRepository.login(email, password)
}
