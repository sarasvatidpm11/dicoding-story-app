package com.example.dicodingstory.ui.auth.pref

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.dicodingstory.data.response.LoginResult
import kotlinx.coroutines.launch

class AuthenticationViewModel(private val authentication: AuthPreferences) : ViewModel() {

    fun getSession():LiveData<LoginResult> = authentication.getSession().asLiveData()

    fun clearSession() {
        viewModelScope.launch {
            authentication.clearSession()
        }
    }
}