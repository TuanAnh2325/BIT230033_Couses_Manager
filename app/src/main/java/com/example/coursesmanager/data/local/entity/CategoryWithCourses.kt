package com.example.coursesmanager.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class CategoryWithCourses(
    @Embedded
    val category: CategoryEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "categoryId"
    )
    val courses: List<CourseEntity>
)
