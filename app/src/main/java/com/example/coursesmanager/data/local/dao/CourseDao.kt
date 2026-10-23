package com.example.coursesmanager.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.coursesmanager.data.local.entity.CourseEntity
import com.example.coursesmanager.data.local.entity.CourseWithCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {

    @Transaction
    @Query("SELECT * FROM courses ORDER BY id DESC")
    fun getAllCoursesWithCategory(): Flow<List<CourseWithCategory>>

    @Transaction
    @Query("SELECT * FROM courses WHERE id = :id LIMIT 1")
    fun getCourseWithCategoryById(id: String): Flow<CourseWithCategory?>

    @Transaction
    @Query("SELECT * FROM courses WHERE id = :id LIMIT 1")
    suspend fun getCourseWithCategoryByIdDirect(id: String): CourseWithCategory?

    @Query("SELECT * FROM courses WHERE id = :id LIMIT 1")
    suspend fun getCourseById(id: String): CourseEntity?

    @Transaction
    @Query("""
        SELECT * FROM courses 
        WHERE (:query = '' OR title LIKE '%' || :query || '%')
          AND (:categoryId = '' OR categoryId = :categoryId)
        ORDER BY id DESC
    """)
    fun filterCourses(query: String, categoryId: String): Flow<List<CourseWithCategory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: CourseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourses(courses: List<CourseEntity>)

    @Update
    suspend fun updateCourse(course: CourseEntity)

    @Delete
    suspend fun deleteCourse(course: CourseEntity)

    @Query("DELETE FROM courses WHERE id = :id")
    suspend fun deleteCourseById(id: String)

    @Query("SELECT COUNT(*) FROM courses")
    suspend fun countAll(): Int

    @Query("DELETE FROM courses")
    suspend fun clearAll()
}
