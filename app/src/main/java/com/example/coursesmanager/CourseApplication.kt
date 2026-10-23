package com.example.coursesmanager

import android.app.Application
import com.example.coursesmanager.data.local.AppDatabase
import com.example.coursesmanager.data.remote.RetrofitClient
import com.example.coursesmanager.data.repository.CategoryRepository
import com.example.coursesmanager.data.repository.CourseRepository

class CourseApplication : Application() {

    val database by lazy { AppDatabase.getInstance(this) }
    val apiService by lazy { RetrofitClient.getApiService() }

    val categoryRepository by lazy {
        CategoryRepository(database.categoryDao(), apiService)
    }

    val courseRepository by lazy {
        CourseRepository(database.courseDao(), apiService)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: CourseApplication
            private set
    }
}
