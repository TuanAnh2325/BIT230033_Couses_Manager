package com.example.coursesmanager.ui.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.coursesmanager.data.local.dao.CategoryWithCount
import com.example.coursesmanager.data.local.entity.CategoryEntity
import com.example.coursesmanager.data.repository.CategoryRepository
import com.example.coursesmanager.util.ValidationResult
import com.example.coursesmanager.util.ValidationUtil
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _categoriesWithCount = MutableStateFlow<List<CategoryWithCount>>(emptyList())
    val categoriesWithCount: StateFlow<List<CategoryWithCount>> = _categoriesWithCount.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.categoriesWithCount.collect {
                _categoriesWithCount.value = it
            }
        }
    }

    fun refreshCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            categoryRepository.refreshCategories()
            _isLoading.value = false
        }
    }

    fun addCategory(name: String, description: String) {
        val validation = ValidationUtil.validateCategory(name, description)
        if (validation is ValidationResult.Invalid) {
            viewModelScope.launch {
                _toastEvent.emit(validation.errorMessage)
            }
            return
        }

        viewModelScope.launch {
            val result = categoryRepository.addCategory(name, description)
            if (result.isSuccess) {
                _toastEvent.emit("Thêm danh mục mới thành công!")
            } else {
                _toastEvent.emit("Lỗi khi thêm danh mục: ${result.exceptionOrNull()?.localizedMessage ?: "Vui lòng thử lại"}")
            }
        }
    }

    fun updateCategory(id: String, name: String, description: String) {
        val validation = ValidationUtil.validateCategory(name, description)
        if (validation is ValidationResult.Invalid) {
            viewModelScope.launch {
                _toastEvent.emit(validation.errorMessage)
            }
            return
        }

        viewModelScope.launch {
            val entity = CategoryEntity(id = id, name = name.trim(), description = description.trim())
            val result = categoryRepository.updateCategory(entity)
            if (result.isSuccess) {
                _toastEvent.emit("Cập nhật danh mục thành công!")
            } else {
                _toastEvent.emit("Lỗi khi cập nhật danh mục: ${result.exceptionOrNull()?.localizedMessage ?: "Vui lòng thử lại"}")
            }
        }
    }

    /**
     * Xóa danh mục với kiểm tra ràng buộc toàn vẹn (Không xóa nếu còn khóa học)
     */
    fun deleteCategory(categoryWithCount: CategoryWithCount) {
        viewModelScope.launch {
            val result = categoryRepository.deleteCategoryWithConstraintCheck(categoryWithCount.category)
            if (result.isSuccess) {
                _toastEvent.emit("Đã xóa danh mục thành công!")
            } else {
                val error = result.exceptionOrNull()
                val message = error?.message ?: "Lỗi khi xóa danh mục!"
                _toastEvent.emit(message)
            }
        }
    }
}

class CategoryViewModelFactory(
    private val categoryRepository: CategoryRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            return CategoryViewModel(categoryRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
