package com.example.dicodingstory.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dicodingstory.di.Injection
import com.example.dicodingstory.data.remote.StoryAppRepository
import com.example.dicodingstory.ui.auth.pref.dataStore
import com.example.dicodingstory.ui.auth.login.LoginViewModel
import com.example.dicodingstory.ui.auth.pref.AuthPreferences
import com.example.dicodingstory.ui.auth.pref.AuthenticationViewModel
import com.example.dicodingstory.ui.auth.register.RegisterViewModel
import com.example.dicodingstory.ui.liststory.ListStoryViewModel
import com.example.dicodingstory.ui.maps.MapsViewModel
import com.example.dicodingstory.ui.newstory.NewStoryViewModel

class ViewModelFactory(
    private val storyAppRepository: StoryAppRepository, private val authentication: AuthPreferences) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(storyAppRepository) as T
        }
        else if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(storyAppRepository) as T
        }
        else if (modelClass.isAssignableFrom(AuthenticationViewModel::class.java)) {
            return AuthenticationViewModel(authentication) as T
        }
        else if(modelClass.isAssignableFrom(ListStoryViewModel::class.java)) {
            return ListStoryViewModel(storyAppRepository) as T
        }
        else if(modelClass.isAssignableFrom(NewStoryViewModel::class.java)) {
            return NewStoryViewModel(storyAppRepository) as T
        }
        else if(modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            return MapsViewModel(storyAppRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class" + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory {
            return instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    Injection.provideRepository(context),
                    AuthPreferences.getInstance(context.dataStore)
                )
            }.also { instance = it }
        }
    }
}