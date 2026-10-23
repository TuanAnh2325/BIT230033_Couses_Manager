package com.example.coursesmanager.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.coursesmanager.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

data class CategoryWithCount(
    @Embedded
    val category: CategoryEntity,
    val courseCount: Int
)

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories ORDER BY name ASC")
    suspend fun getAllCategoriesList(): List<CategoryEntity>

    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    suspend fun getCategoryById(id: String): CategoryEntity?

    @Query("""
        SELECT categories.*, COUNT(courses.id) AS courseCount 
        FROM categories 
        LEFT JOIN courses ON categories.id = courses.categoryId 
        GROUP BY categories.id 
        ORDER BY categories.name ASC
    """)
    fun getCategoriesWithCourseCount(): Flow<List<CategoryWithCount>>

    @Query("SELECT COUNT(*) FROM courses WHERE categoryId = :categoryId")
    suspend fun countCoursesByCategoryId(categoryId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteCategoryById(id: String)

    @Query("DELETE FROM categories")
    suspend fun clearAll()
}
