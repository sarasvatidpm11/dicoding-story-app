package com.example.dicodingstory.data.remote

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.dicodingstory.data.local.StoryRemoteMediator
import com.example.dicodingstory.data.local.database.StoryDatabase
import com.example.dicodingstory.data.local.entity.StoryEntity
import com.example.dicodingstory.data.response.ListStoryItem
import com.example.dicodingstory.data.response.LoginResponse
import com.example.dicodingstory.data.response.RegisterResponse
import com.example.dicodingstory.data.retrofit.ApiConfig
import com.example.dicodingstory.data.retrofit.ApiService
import com.example.dicodingstory.ui.auth.pref.AuthPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class StoryAppRepository(
    private val apiService: ApiService,
    private val authentication: AuthPreferences,
    private val database: StoryDatabase
) {
    private val storyDao = database.storyDao()

    fun register(name: String, email: String, password: String)
    : LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.register(name, email, password)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "An unknown error occurred"))
        }
    }

    fun login(email: String, password: String)
    : LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(email, password)
            if (!response.error) {
                val loginResult = response.loginResult
                authentication.saveSession(
                    loginResult.token,
                    loginResult.name,
                    loginResult.userId
                )
                Log.d("StoryAppRepository", "saveSession called: token=${loginResult.token}, name=${loginResult.name}, id=${loginResult.userId}")
                emit(Result.Success(response))
            } else {
                emit(Result.Error(response.message))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "An unknown error occurred"))
        }
    }

    fun getStories(): LiveData<PagingData<StoryEntity>> {
        val token = runBlocking { authentication.getSession().first().token }
        val apiService = ApiConfig.getApiService(token)

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(database, apiService),
            pagingSourceFactory = {
                storyDao.getAllStory()
            }
        ).liveData
    }



    fun uploadStory(imageFile: File, description: String, lat: Double?, lon: Double?)
    : LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)
        try {
            val loginResult = authentication.getSession().first()
            val token = loginResult.token
            if (token.isNotEmpty()) {
                val reqDescription = description.toRequestBody("text/plain".toMediaType())
                val reqImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                val multipartBody = MultipartBody.Part.createFormData(
                    "photo",
                    imageFile.name,
                    reqImageFile
                )
                val reqLat = lat?.toString()?.toRequestBody("text/plain".toMediaType())
                val reqLon = lon?.toString()?.toRequestBody("text/plain".toMediaType())
                val apiService = ApiConfig.getApiService(token)
                val response = apiService.uploadStory(
                    multipartBody,
                    reqDescription,
                    reqLat,
                    reqLon
                )
                emit(Result.Success(response))
            }
            else {
                emit(Result.Error("Token is empty"))
            }
        }
        catch (e: Exception) {
            emit(Result.Error(e.message ?: "An unknown error occurred"))
        }
    }

    fun getStoriesLocation(location: Int = 1): LiveData<Result<List<ListStoryItem>>> = liveData {
        emit(Result.Loading)
        try {
            val loginResult = runBlocking { authentication.getSession().first() }
            val token = loginResult.token
            Log.d("StoryAppRepository", "Fetched token: $token")

            if (token.isNotEmpty()) {
                val apiService = ApiConfig.getApiService(token)
                val response = apiService.getStoriesLocation(location)
                val responseResult = response.listStory
                if (!response.error) {
                    emit(Result.Success(responseResult))
                } else {
                    emit(Result.Error(response.message))
                }
            } else {
                emit(Result.Error("Token is empty"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "An unknown error occurred"))
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: StoryAppRepository? = null

        fun getInstance(
            apiService: ApiService,
            authentication: AuthPreferences,
            database: StoryDatabase
        ): StoryAppRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: StoryAppRepository(apiService, authentication, database)
            }.also { INSTANCE = it }
        }
    }
}
