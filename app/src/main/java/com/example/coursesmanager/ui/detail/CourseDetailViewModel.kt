package com.example.coursesmanager.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.coursesmanager.data.local.entity.CourseWithCategory
import com.example.coursesmanager.data.repository.CourseRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CourseDetailViewModel(
    private val courseRepository: CourseRepository,
    private val courseId: String
) : ViewModel() {

    private val _course = MutableStateFlow<CourseWithCategory?>(null)
    val course: StateFlow<CourseWithCategory?> = _course.asStateFlow()

    private val _deleteSuccess = MutableSharedFlow<Boolean>()
    val deleteSuccess: SharedFlow<Boolean> = _deleteSuccess.asSharedFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage: SharedFlow<String> = _errorMessage.asSharedFlow()

    init {
        loadCourseDetail()
    }

    private fun loadCourseDetail() {
        viewModelScope.launch {
            courseRepository.getCourseWithCategoryById(courseId).collect {
                _course.value = it
            }
        }
    }

    fun deleteCourse() {
        viewModelScope.launch {
            val result = courseRepository.deleteCourse(courseId)
            if (result.isSuccess) {
                _deleteSuccess.emit(true)
            } else {
                _errorMessage.emit("Lỗi khi xóa khóa học: ${result.exceptionOrNull()?.localizedMessage ?: "Vui lòng thử lại"}")
            }
        }
    }
}

class CourseDetailViewModelFactory(
    private val courseRepository: CourseRepository,
    private val courseId: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CourseDetailViewModel::class.java)) {
            return CourseDetailViewModel(courseRepository, courseId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
