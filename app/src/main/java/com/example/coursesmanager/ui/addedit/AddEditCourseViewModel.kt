package com.example.coursesmanager.ui.addedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.coursesmanager.data.local.entity.CategoryEntity
import com.example.coursesmanager.data.local.entity.CourseEntity
import com.example.coursesmanager.data.repository.CategoryRepository
import com.example.coursesmanager.data.repository.CourseRepository
import com.example.coursesmanager.util.Field
import com.example.coursesmanager.util.FormatUtil
import com.example.coursesmanager.util.ValidationResult
import com.example.coursesmanager.util.ValidationUtil
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddEditCourseViewModel(
    private val courseRepository: CourseRepository,
    private val categoryRepository: CategoryRepository,
    private val courseId: String?
) : ViewModel() {

    val isEditMode: Boolean = !courseId.isNullOrBlank()

    private val _categories = MutableStateFlow<List<CategoryEntity>>(emptyList())
    val categories: StateFlow<List<CategoryEntity>> = _categories.asStateFlow()

    private val _existingCourse = MutableStateFlow<CourseEntity?>(null)
    val existingCourse: StateFlow<CourseEntity?> = _existingCourse.asStateFlow()

    private val _validationError = MutableSharedFlow<Pair<Field, String>>()
    val validationError: SharedFlow<Pair<Field, String>> = _validationError.asSharedFlow()

    private val _saveSuccess = MutableSharedFlow<String>()
    val saveSuccess: SharedFlow<String> = _saveSuccess.asSharedFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    init {
        loadCategories()
        if (isEditMode && courseId != null) {
            loadExistingCourse(courseId)
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.allCategories.collect {
                _categories.value = it
            }
        }
    }

    private fun loadExistingCourse(id: String) {
        viewModelScope.launch {
            val item = courseRepository.getCourseWithCategoryByIdDirect(id)
            _existingCourse.value = item?.course
        }
    }

    fun saveCourse(
        title: String,
        categoryId: String,
        instructor: String,
        priceStr: String,
        durationStr: String,
        thumbnail: String,
        trailerUrl: String,
        description: String
    ) {
        // 1. Chạy Validation toàn diện
        val validation = ValidationUtil.validateCourse(
            title = title,
            categoryId = categoryId,
            instructor = instructor,
            priceStr = priceStr,
            durationStr = durationStr,
            thumbnail = thumbnail,
            trailerUrl = trailerUrl,
            description = description
        )

        if (validation is ValidationResult.Invalid) {
            viewModelScope.launch {
                _validationError.emit(Pair(validation.field, validation.errorMessage))
            }
            return
        }

        val price = priceStr.trim().toDoubleOrNull() ?: 0.0
        val duration = durationStr.trim().toIntOrNull() ?: 0

        viewModelScope.launch {
            _isSaving.value = true

            val entity = if (isEditMode && _existingCourse.value != null) {
                _existingCourse.value!!.copy(
                    title = title.trim(),
                    categoryId = categoryId.trim(),
                    instructor = instructor.trim(),
                    price = price,
                    duration = duration,
                    thumbnail = thumbnail.trim(),
                    trailerUrl = trailerUrl.trim(),
                    description = description.trim()
                )
            } else {
                CourseEntity(
                    id = "",
                    title = title.trim(),
                    categoryId = categoryId.trim(),
                    instructor = instructor.trim(),
                    price = price,
                    duration = duration,
                    thumbnail = thumbnail.trim(),
                    trailerUrl = trailerUrl.trim(),
                    description = description.trim(),
                    createdAt = FormatUtil.currentDateString()
                )
            }

            val result = if (isEditMode) {
                courseRepository.updateCourse(entity)
            } else {
                courseRepository.addCourse(entity)
            }

            _isSaving.value = false

            if (result.isSuccess) {
                val successMsg = if (isEditMode) "Đã cập nhật khóa học thành công!" else "Đã thêm khóa học thành công!"
                _saveSuccess.emit(successMsg)
            } else {
                _validationError.emit(Pair(Field.GENERAL, "Lưu khóa học thất bại: ${result.exceptionOrNull()?.localizedMessage ?: "Vui lòng thử lại"}"))
            }
        }
    }
}

class AddEditCourseViewModelFactory(
    private val courseRepository: CourseRepository,
    private val categoryRepository: CategoryRepository,
    private val courseId: String?
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddEditCourseViewModel::class.java)) {
            return AddEditCourseViewModel(courseRepository, categoryRepository, courseId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
