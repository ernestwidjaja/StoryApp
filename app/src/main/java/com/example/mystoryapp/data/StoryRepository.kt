package com.example.mystoryapp.data

import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.example.mystoryapp.data.api.ApiService
import com.example.mystoryapp.data.api.response.LoginResponse
import com.example.mystoryapp.data.api.response.RegisterResponse
import com.example.mystoryapp.data.api.response.StoryDetailResponse
import com.example.mystoryapp.data.api.response.StoryResponse
import com.example.mystoryapp.data.database.StoryDatabase
import com.example.mystoryapp.data.pref.UserModel
import com.example.mystoryapp.data.pref.UserPreferences
import com.example.mystoryapp.util.wrapEspressoIdlingResource
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences,
    private val storyDatabase: StoryDatabase
){

    fun register(name: String, email: String, password: String) = liveData {
        emit(Result.Loading)
        try {
            val successResponse = apiService.register(name, email, password)
            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, RegisterResponse::class.java)
            emit(Result.Error(errorResponse.message.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun login(email: String, password: String) = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
            try {
                val successResponse = apiService.login(email, password).loginResult
                emit(Result.Success(successResponse))
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
                emit(Result.Error(errorResponse.message.toString()))
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getStories() = Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = true
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData

    fun getStoriesInfo() = liveData {
        emit(Result.Loading)
        try {
            val successResponse = apiService.getStories(TOKEN).listStory
            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, StoryResponse::class.java)
            emit(Result.Error(errorResponse.message.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }


    fun getStoriesLocation() = liveData {
        emit(Result.Loading)
        try {
            val successResponse = apiService.getStoriesLocation(TOKEN).listStory
            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, StoryResponse::class.java)
            emit(Result.Error(errorResponse.message.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getStoriesDetail(id: String) = liveData {
        emit(Result.Loading)
        try {
            val successResponse = apiService.getStoriesDetail(TOKEN, id)
            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, StoryDetailResponse::class.java)
            emit(Result.Error(errorResponse.message.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun addStories(multipartBody: MultipartBody.Part, descriptionBody: RequestBody, latBody: RequestBody, lonBody: RequestBody, isShowLocation: Boolean) = liveData {
        emit(Result.Loading)
        try {
            if (isShowLocation) {
                val successResponse = apiService.addStories(TOKEN, multipartBody, descriptionBody, latBody, lonBody).message
                emit(Result.Success(successResponse))
            } else {
                val successResponse = apiService.addStories(TOKEN, multipartBody, descriptionBody).message
                emit(Result.Success(successResponse))
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, StoryDetailResponse::class.java)
            emit(Result.Error(errorResponse.message.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    suspend fun saveSession(user: UserModel) {
        userPreferences.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreferences.getSession()
    }

    suspend fun logout() {
        userPreferences.logout()
    }

    companion object {
        var TOKEN = "token"
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(
            apiService: ApiService,
            userPreferences: UserPreferences,
            storyDatabase: StoryDatabase
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, userPreferences, storyDatabase)
            }.also { instance = it }
    }
}