package com.example.coursesmanager.data.repository

import com.example.coursesmanager.data.local.dao.CourseDao
import com.example.coursesmanager.data.local.entity.CourseEntity
import com.example.coursesmanager.data.local.entity.CourseWithCategory
import com.example.coursesmanager.data.remote.MockApiService
import com.example.coursesmanager.data.remote.model.CourseDto
import com.example.coursesmanager.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CourseRepository(
    private val courseDao: CourseDao,
    private val apiService: MockApiService
) {

    val allCoursesWithCategory: Flow<List<CourseWithCategory>> = courseDao.getAllCoursesWithCategory()

    fun filterCourses(query: String, categoryId: String): Flow<List<CourseWithCategory>> {
        return courseDao.filterCourses(query = query.trim(), categoryId = categoryId.trim())
    }

    fun getCourseWithCategoryById(id: String): Flow<CourseWithCategory?> {
        return courseDao.getCourseWithCategoryById(id)
    }

    suspend fun getCourseWithCategoryByIdDirect(id: String): CourseWithCategory? = withContext(Dispatchers.IO) {
        courseDao.getCourseWithCategoryByIdDirect(id)
    }

    /**
     * Đồng bộ danh sách khóa học từ MockAPI về Room Database
     */
    suspend fun refreshCourses(): Resource<List<CourseEntity>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getCourses()
            if (response.isSuccessful && response.body() != null) {
                val dtos = response.body()!!
                val entities = dtos.map { it.toEntity() }
                if (entities.isNotEmpty()) {
                    courseDao.insertCourses(entities)
                }
                Resource.Success(entities)
            } else {
                Resource.Error("Máy chủ trả về lỗi: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("Không thể kết nối đến MockAPI: ${e.localizedMessage ?: "Lỗi mạng"}", e)
        }
    }

    /**
     * Thêm khóa học mới
     */
    suspend fun addCourse(course: CourseEntity): Result<CourseEntity> = withContext(Dispatchers.IO) {
        try {
            var entityToSave = course
            if (entityToSave.id.isBlank()) {
                entityToSave = entityToSave.copy(id = System.currentTimeMillis().toString())
            }

            // Lưu Room DB
            courseDao.insertCourse(entityToSave)

            // Đồng bộ MockAPI
            try {
                val dto = CourseDto.fromEntity(entityToSave)
                val response = apiService.createCourse(dto)
                if (response.isSuccessful && response.body() != null) {
                    val remoteEntity = response.body()!!.toEntity()
                    if (remoteEntity.id.isNotBlank() && remoteEntity.id != entityToSave.id) {
                        courseDao.deleteCourseById(entityToSave.id)
                        courseDao.insertCourse(remoteEntity)
                        return@withContext Result.success(remoteEntity)
                    }
                }
            } catch (_: Exception) {
                // Tiếp tục với dữ liệu local nếu network gặp trục trặc
            }

            Result.success(entityToSave)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Cập nhật khóa học
     */
    suspend fun updateCourse(course: CourseEntity): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Cập nhật Room DB
            courseDao.updateCourse(course)

            // Cập nhật MockAPI
            try {
                val dto = CourseDto.fromEntity(course)
                apiService.updateCourse(course.id, dto)
            } catch (_: Exception) {
                // Tiếp tục offline
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Xóa khóa học
     */
    suspend fun deleteCourse(courseId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Xóa Room DB
            courseDao.deleteCourseById(courseId)

            // Xóa MockAPI
            try {
                apiService.deleteCourse(courseId)
            } catch (_: Exception) {
                // Tiếp tục offline
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
