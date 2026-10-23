package com.example.coursesmanager.data.remote

import com.example.coursesmanager.data.remote.model.CategoryDto
import com.example.coursesmanager.data.remote.model.CourseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface MockApiService {

    // --- Categories Endpoints ---

    @GET("categories")
    suspend fun getCategories(): Response<List<CategoryDto>>

    @POST("categories")
    suspend fun createCategory(@Body category: CategoryDto): Response<CategoryDto>

    @PUT("categories/{id}")
    suspend fun updateCategory(
        @Path("id") id: String,
        @Body category: CategoryDto
    ): Response<CategoryDto>

    @DELETE("categories/{id}")
    suspend fun deleteCategory(@Path("id") id: String): Response<CategoryDto>


    // --- Courses Endpoints ---

    @GET("courses")
    suspend fun getCourses(): Response<List<CourseDto>>

    @GET("courses/{id}")
    suspend fun getCourseById(@Path("id") id: String): Response<CourseDto>

    @POST("courses")
    suspend fun createCourse(@Body course: CourseDto): Response<CourseDto>

    @PUT("courses/{id}")
    suspend fun updateCourse(
        @Path("id") id: String,
        @Body course: CourseDto
    ): Response<CourseDto>

    @DELETE("courses/{id}")
    suspend fun deleteCourse(@Path("id") id: String): Response<CourseDto>
}
