package com.example.coursesmanager.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class CourseWithCategory(
    @Embedded
    val course: CourseEntity,

    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: CategoryEntity?
)
