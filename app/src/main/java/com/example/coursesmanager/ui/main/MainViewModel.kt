package com.example.coursesmanager.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.coursesmanager.data.local.entity.CategoryEntity
import com.example.coursesmanager.data.local.entity.CourseWithCategory
import com.example.coursesmanager.data.repository.CategoryRepository
import com.example.coursesmanager.data.repository.CourseRepository
import com.example.coursesmanager.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(
    private val courseRepository: CourseRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow("")
    val selectedCategoryId: StateFlow<String> = _selectedCategoryId.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    val categories: StateFlow<List<CategoryEntity>> = categoryRepository.allCategories
        .let { flow ->
            val state = MutableStateFlow<List<CategoryEntity>>(emptyList())
            viewModelScope.launch {
                flow.collect { state.value = it }
            }
            state.asStateFlow()
        }

    val courses: StateFlow<List<CourseWithCategory>> = combine(
        _searchQuery,
        _selectedCategoryId
    ) { query, categoryId ->
        Pair(query, categoryId)
    }.flatMapLatest { (query, categoryId) ->
        courseRepository.filterCourses(query, categoryId)
    }.let { flow ->
        val state = MutableStateFlow<List<CourseWithCategory>>(emptyList())
        viewModelScope.launch {
            flow.collect { state.value = it }
        }
        state.asStateFlow()
    }

    init {
        refreshAll(silent = true)
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCategoryFilterSelected(categoryId: String) {
        _selectedCategoryId.value = categoryId
    }

    fun refreshAll(silent: Boolean = false) {
        viewModelScope.launch {
            if (!silent) _isLoading.value = true

            val catResult = categoryRepository.refreshCategories()
            val courseResult = courseRepository.refreshCourses()

            if (!silent) {
                _isLoading.value = false
                if (catResult is Resource.Error || courseResult is Resource.Error) {
                    _toastEvent.emit("Chế độ Offline: Đang hiển thị dữ liệu đã lưu trong máy.")
                } else {
                    _toastEvent.emit("Đã cập nhật dữ liệu mới nhất thành công!")
                }
            }
        }
    }
}

class MainViewModelFactory(
    private val courseRepository: CourseRepository,
    private val categoryRepository: CategoryRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(courseRepository, categoryRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
