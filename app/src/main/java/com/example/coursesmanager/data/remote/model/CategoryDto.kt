package com.example.coursesmanager.data.remote.model

import com.example.coursesmanager.data.local.entity.CategoryEntity
import com.google.gson.annotations.SerializedName

data class CategoryDto(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("description")
    val description: String? = null
) {
    fun toEntity(): CategoryEntity {
        return CategoryEntity(
            id = id.orEmpty(),
            name = name.orEmpty(),
            description = description.orEmpty()
        )
    }

    companion object {
        fun fromEntity(entity: CategoryEntity): CategoryDto {
            return CategoryDto(
                id = if (entity.id.isNotBlank()) entity.id else null,
                name = entity.name,
                description = entity.description
            )
        }
    }
}
