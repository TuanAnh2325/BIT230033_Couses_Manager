package com.example.coursesmanager.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "courses",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["categoryId"])
    ]
)
data class CourseEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val categoryId: String,
    val instructor: String,
    val price: Double,
    val duration: Int,
    val thumbnail: String,
    val trailerUrl: String,
    val createdAt: String
)
