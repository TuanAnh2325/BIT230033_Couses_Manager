package com.example.coursesmanager.data.repository

import com.example.coursesmanager.data.local.dao.CategoryDao
import com.example.coursesmanager.data.local.dao.CategoryWithCount
import com.example.coursesmanager.data.local.entity.CategoryEntity
import com.example.coursesmanager.data.remote.MockApiService
import com.example.coursesmanager.data.remote.model.CategoryDto
import com.example.coursesmanager.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID

class CategoryRepository(
    private val categoryDao: CategoryDao,
    private val apiService: MockApiService
) {

    val allCategories: Flow<List<CategoryEntity>> = categoryDao.getAllCategories()
    val categoriesWithCount: Flow<List<CategoryWithCount>> = categoryDao.getCategoriesWithCourseCount()

    suspend fun getAllCategoriesList(): List<CategoryEntity> = withContext(Dispatchers.IO) {
        categoryDao.getAllCategoriesList()
    }

    suspend fun getCategoryById(id: String): CategoryEntity? = withContext(Dispatchers.IO) {
        categoryDao.getCategoryById(id)
    }


    suspend fun refreshCategories(): Resource<List<CategoryEntity>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getCategories()
            if (response.isSuccessful && response.body() != null) {
                val dtos = response.body()!!
                val entities = dtos.map { it.toEntity() }
                if (entities.isNotEmpty()) {
                    categoryDao.insertCategories(entities)
                }
                Resource.Success(entities)
            } else {
                Resource.Error("Máy chủ trả về lỗi: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("Không thể kết nối đến MockAPI: ${e.localizedMessage ?: "Lỗi mạng"}", e)
        }
    }


    suspend fun addCategory(name: String, description: String): Result<CategoryEntity> = withContext(Dispatchers.IO) {
        try {
            val localId = System.currentTimeMillis().toString()
            val newEntity = CategoryEntity(
                id = localId,
                name = name.trim(),
                description = description.trim()
            )

            // Lưu trước vào Room DB
            categoryDao.insertCategory(newEntity)

            // Thử đồng bộ lên MockAPI nếu có kết nối
            try {
                val dto = CategoryDto(id = null, name = name.trim(), description = description.trim())
                val response = apiService.createCategory(dto)
                if (response.isSuccessful && response.body() != null) {
                    val remoteEntity = response.body()!!.toEntity()
                    if (remoteEntity.id.isNotBlank() && remoteEntity.id != localId) {
                        categoryDao.deleteCategoryById(localId)
                        categoryDao.insertCategory(remoteEntity)
                        return@withContext Result.success(remoteEntity)
                    }
                }
            } catch (_: Exception) {
                // Tiếp tục với dữ liệu local nếu network gặp trục trặc
            }

            Result.success(newEntity)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun updateCategory(category: CategoryEntity): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Cập nhật Room DB
            categoryDao.updateCategory(category)

            // Đồng bộ MockAPI
            try {
                val dto = CategoryDto.fromEntity(category)
                apiService.updateCategory(category.id, dto)
            } catch (_: Exception) {
                // Ghi nhận thay đổi cục bộ
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun deleteCategoryWithConstraintCheck(category: CategoryEntity): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // 1. Kiểm tra số lượng khóa học đang liên kết với danh mục này
            val coursesCount = categoryDao.countCoursesByCategoryId(category.id)
            if (coursesCount > 0) {
                val errorMsg = "Không thể xóa! Danh mục \"${category.name}\" đang có $coursesCount khóa học liên kết. Hãy chuyển hoặc xóa các khóa học trước!"
                return@withContext Result.failure(IllegalStateException(errorMsg))
            }

            // 2. Nếu không có khóa học nào thuộc danh mục này -> Cho phép xóa
            // Xóa trên Room DB
            categoryDao.deleteCategory(category)

            // Xóa trên MockAPI
            try {
                apiService.deleteCategory(category.id)
            } catch (_: Exception) {
                // Xóa offline thành công
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
