package com.example.coursesmanager.data.remote.model

import com.example.coursesmanager.data.local.entity.CourseEntity
import com.google.gson.annotations.SerializedName

data class CourseDto(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("categoryId")
    val categoryId: String? = null,

    @SerializedName("instructor")
    val instructor: String? = null,

    @SerializedName("price")
    val price: Double? = null,

    @SerializedName("duration")
    val duration: Int? = null,

    @SerializedName("thumbnail")
    val thumbnail: String? = null,

    @SerializedName("trailerUrl")
    val trailerUrl: String? = null,

    @SerializedName("createdAt")
    val createdAt: String? = null
) {
    fun toEntity(): CourseEntity {
        return CourseEntity(
            id = id.orEmpty(),
            title = title.orEmpty(),
            description = description.orEmpty(),
            categoryId = categoryId.orEmpty(),
            instructor = instructor.orEmpty(),
            price = price ?: 0.0,
            duration = duration ?: 0,
            thumbnail = thumbnail.orEmpty(),
            trailerUrl = trailerUrl.orEmpty(),
            createdAt = createdAt.orEmpty()
        )
    }

    companion object {
        fun fromEntity(entity: CourseEntity): CourseDto {
            return CourseDto(
                id = if (entity.id.isNotBlank()) entity.id else null,
                title = entity.title,
                description = entity.description,
                categoryId = entity.categoryId,
                instructor = entity.instructor,
                price = entity.price,
                duration = entity.duration,
                thumbnail = entity.thumbnail,
                trailerUrl = entity.trailerUrl,
                createdAt = entity.createdAt
            )
        }
    }
}
