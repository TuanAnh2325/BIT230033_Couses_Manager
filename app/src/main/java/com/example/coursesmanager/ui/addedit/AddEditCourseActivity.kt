package com.example.coursesmanager.ui.addedit

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import com.example.coursesmanager.CourseApplication
import com.example.coursesmanager.R
import com.example.coursesmanager.data.local.entity.CategoryEntity
import com.example.coursesmanager.data.local.entity.CourseEntity
import com.example.coursesmanager.databinding.ActivityAddEditCourseBinding
import com.example.coursesmanager.util.Field
import com.example.coursesmanager.util.ToastUtil
import com.example.coursesmanager.util.ValidationUtil
import kotlinx.coroutines.launch

class AddEditCourseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditCourseBinding
    private var categoryList: List<CategoryEntity> = emptyList()

    private val courseId: String? by lazy {
        intent.getStringExtra(EXTRA_COURSE_ID)
    }

    private val viewModel: AddEditCourseViewModel by viewModels {
        val app = application as CourseApplication
        AddEditCourseViewModelFactory(app.courseRepository, app.categoryRepository, courseId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditCourseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupListeners()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbarAddEdit.title = if (viewModel.isEditMode) {
            getString(R.string.title_edit_course)
        } else {
            getString(R.string.title_add_course)
        }
        binding.toolbarAddEdit.setNavigationOnClickListener {
            finish()
        }
        binding.btnSaveCourse.text = if (viewModel.isEditMode) {
            getString(R.string.btn_update)
        } else {
            getString(R.string.btn_save)
        }
    }

    private fun setupListeners() {
        // Nút xem trước ảnh bìa từ URL
        binding.btnPreviewImage.setOnClickListener {
            val url = binding.etThumbnail.text?.toString()?.trim().orEmpty()
            if (ValidationUtil.isValidUrl(url)) {
                binding.cardPreview.visibility = View.VISIBLE
                binding.ivPreview.load(url) {
                    crossfade(true)
                    placeholder(R.drawable.ic_course_placeholder)
                    error(R.drawable.ic_course_placeholder)
                    listener(
                        onError = { _, _ ->
                            ToastUtil.show(this@AddEditCourseActivity, getString(R.string.err_image_preview_failed))
                        }
                    )
                }
            } else {
                ToastUtil.show(this, getString(R.string.err_thumbnail_invalid))
            }
        }

        // Tự động xem trước ảnh khi người dùng rời khỏi ô nhập URL ảnh
        binding.etThumbnail.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val url = binding.etThumbnail.text?.toString()?.trim().orEmpty()
                if (ValidationUtil.isValidUrl(url)) {
                    binding.ivPreview.load(url) {
                        crossfade(true)
                        placeholder(R.drawable.ic_course_placeholder)
                        error(R.drawable.ic_course_placeholder)
                    }
                }
            }
        }

        // Nút Lưu khóa học
        binding.btnSaveCourse.setOnClickListener {
            clearErrors()

            val selectedPos = binding.spinnerCategory.selectedItemPosition
            val selectedCategoryId = if (selectedPos in categoryList.indices) {
                categoryList[selectedPos].id
            } else {
                ""
            }

            viewModel.saveCourse(
                title = binding.etTitle.text?.toString().orEmpty(),
                categoryId = selectedCategoryId,
                instructor = binding.etInstructor.text?.toString().orEmpty(),
                priceStr = binding.etPrice.text?.toString().orEmpty(),
                durationStr = binding.etDuration.text?.toString().orEmpty(),
                thumbnail = binding.etThumbnail.text?.toString().orEmpty(),
                trailerUrl = binding.etTrailerUrl.text?.toString().orEmpty(),
                description = binding.etDescription.text?.toString().orEmpty()
            )
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.categories.collect { list ->
                        categoryList = list
                        setupCategorySpinner(list)
                    }
                }

                launch {
                    viewModel.existingCourse.collect { course ->
                        course?.let { populateFormData(it) }
                    }
                }

                launch {
                    viewModel.validationError.collect { (field, message) ->
                        handleValidationError(field, message)
                    }
                }

                launch {
                    viewModel.saveSuccess.collect { message ->
                        ToastUtil.show(this@AddEditCourseActivity, message)
                        finish()
                    }
                }

                launch {
                    viewModel.isSaving.collect { isSaving ->
                        binding.btnSaveCourse.isEnabled = !isSaving
                        binding.btnSaveCourse.text = if (isSaving) "Đang lưu..." else {
                            if (viewModel.isEditMode) getString(R.string.btn_update) else getString(R.string.btn_save)
                        }
                    }
                }
            }
        }
    }

    private fun setupCategorySpinner(list: List<CategoryEntity>) {
        val categoryNames = list.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryNames).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.spinnerCategory.adapter = adapter

        // Nếu đang sửa, chọn đúng danh mục hiện tại của khóa học
        viewModel.existingCourse.value?.let { course ->
            val index = list.indexOfFirst { it.id == course.categoryId }
            if (index >= 0) {
                binding.spinnerCategory.setSelection(index)
            }
        }
    }

    private fun populateFormData(course: CourseEntity) {
        binding.etTitle.setText(course.title)
        binding.etInstructor.setText(course.instructor)
        binding.etPrice.setText(if (course.price % 1.0 == 0.0) course.price.toLong().toString() else course.price.toString())
        binding.etDuration.setText(course.duration.toString())
        binding.etThumbnail.setText(course.thumbnail)
        binding.etTrailerUrl.setText(course.trailerUrl)
        binding.etDescription.setText(course.description)

        if (categoryList.isNotEmpty()) {
            val index = categoryList.indexOfFirst { it.id == course.categoryId }
            if (index >= 0) {
                binding.spinnerCategory.setSelection(index)
            }
        }

        if (course.thumbnail.isNotBlank()) {
            binding.ivPreview.load(course.thumbnail) {
                crossfade(true)
                placeholder(R.drawable.ic_course_placeholder)
                error(R.drawable.ic_course_placeholder)
            }
        }
    }

    private fun clearErrors() {
        binding.tilTitle.error = null
        binding.tilInstructor.error = null
        binding.tilPrice.error = null
        binding.tilDuration.error = null
        binding.tilThumbnail.error = null
        binding.tilTrailerUrl.error = null
        binding.tilDescription.error = null
    }

    private fun handleValidationError(field: Field, message: String) {
        when (field) {
            Field.TITLE -> binding.tilTitle.error = message
            Field.CATEGORY -> ToastUtil.show(this, message)
            Field.INSTRUCTOR -> binding.tilInstructor.error = message
            Field.PRICE -> binding.tilPrice.error = message
            Field.DURATION -> binding.tilDuration.error = message
            Field.THUMBNAIL -> binding.tilThumbnail.error = message
            Field.TRAILER -> binding.tilTrailerUrl.error = message
            Field.DESCRIPTION -> binding.tilDescription.error = message
            Field.GENERAL -> ToastUtil.show(this, message)
        }
        ToastUtil.show(this, message)
    }

    companion object {
        const val EXTRA_COURSE_ID = "extra_course_id"
    }
}
