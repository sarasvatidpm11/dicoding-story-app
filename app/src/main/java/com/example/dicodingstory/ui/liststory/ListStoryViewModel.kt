package com.example.dicodingstory.ui.liststory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingstory.data.remote.StoryAppRepository
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.dicodingstory.data.local.entity.StoryEntity

class ListStoryViewModel(private val storyAppRepository: StoryAppRepository): ViewModel() {

    fun getStories(): LiveData<PagingData<StoryEntity>> = storyAppRepository.getStories().cachedIn(viewModelScope)
}