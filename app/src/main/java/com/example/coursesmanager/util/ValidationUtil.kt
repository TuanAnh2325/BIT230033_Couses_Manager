package com.example.coursesmanager.util

import android.webkit.URLUtil

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val errorMessage: String, val field: Field = Field.GENERAL) : ValidationResult()
}

enum class Field {
    TITLE,
    CATEGORY,
    INSTRUCTOR,
    PRICE,
    DURATION,
    THUMBNAIL,
    TRAILER,
    DESCRIPTION,
    GENERAL
}

object ValidationUtil {

    fun validateCourse(
        title: String?,
        categoryId: String?,
        instructor: String?,
        priceStr: String?,
        durationStr: String?,
        thumbnail: String?,
        trailerUrl: String?,
        description: String?
    ): ValidationResult {
        // 1. Kiểm tra Tiêu đề
        if (title.isNullOrBlank() || title.trim().length < 3) {
            return ValidationResult.Invalid("Vui lòng nhập tên khóa học (tối thiểu 3 ký tự)!", Field.TITLE)
        }

        // 2. Kiểm tra Danh mục
        if (categoryId.isNullOrBlank() || categoryId == "0" || categoryId == "-1") {
            return ValidationResult.Invalid("Vui lòng chọn danh mục cho khóa học!", Field.CATEGORY)
        }

        // 3. Kiểm tra Giảng viên
        if (instructor.isNullOrBlank() || instructor.trim().length < 2) {
            return ValidationResult.Invalid("Vui lòng nhập tên giảng viên (tối thiểu 2 ký tự)!", Field.INSTRUCTOR)
        }

        // 4. Kiểm tra Học phí (không âm)
        val price = priceStr?.trim()?.toDoubleOrNull()
        if (price == null || price < 0) {
            return ValidationResult.Invalid("Học phí không hợp lệ! Học phí phải là số không âm (>= 0 đ).", Field.PRICE)
        }

        // 5. Kiểm tra Thời lượng (tối thiểu 1 giờ)
        val duration = durationStr?.trim()?.toIntOrNull()
        if (duration == null || duration <= 0) {
            return ValidationResult.Invalid("Thời lượng không hợp lệ! Khóa học phải có thời lượng từ 1 giờ trở lên.", Field.DURATION)
        }

        // 6. Kiểm tra URL ảnh bìa
        val thumbTrim = thumbnail?.trim().orEmpty()
        if (thumbTrim.isEmpty() || (!thumbTrim.startsWith("http://", ignoreCase = true) && !thumbTrim.startsWith("https://", ignoreCase = true))) {
            return ValidationResult.Invalid("URL ảnh bìa không hợp lệ! Vui lòng nhập link bắt đầu bằng http:// hoặc https://", Field.THUMBNAIL)
        }

        // 7. Kiểm tra URL Video Trailer
        val trailerTrim = trailerUrl?.trim().orEmpty()
        if (trailerTrim.isEmpty() || (!trailerTrim.startsWith("http://", ignoreCase = true) && !trailerTrim.startsWith("https://", ignoreCase = true))) {
            return ValidationResult.Invalid("URL trailer không hợp lệ! Vui lòng nhập link video bắt đầu bằng http:// hoặc https://", Field.TRAILER)
        }

        // 8. Kiểm tra Mô tả khóa học
        if (description.isNullOrBlank() || description.trim().length < 10) {
            return ValidationResult.Invalid("Vui lòng nhập mô tả khóa học chi tiết (tối thiểu 10 ký tự)!", Field.DESCRIPTION)
        }

        return ValidationResult.Valid
    }

    fun validateCategory(name: String?, description: String?): ValidationResult {
        if (name.isNullOrBlank() || name.trim().length < 2) {
            return ValidationResult.Invalid("Tên danh mục không được để trống (tối thiểu 2 ký tự)!")
        }
        if (description.isNullOrBlank() || description.trim().length < 3) {
            return ValidationResult.Invalid("Mô tả danh mục không được để trống (tối thiểu 3 ký tự)!")
        }
        return ValidationResult.Valid
    }

    fun isValidUrl(url: String?): Boolean {
        if (url.isNullOrBlank()) return false
        val trimmed = url.trim()
        return (trimmed.startsWith("http://", ignoreCase = true) || trimmed.startsWith("https://", ignoreCase = true)) &&
                URLUtil.isValidUrl(trimmed)
    }
}
