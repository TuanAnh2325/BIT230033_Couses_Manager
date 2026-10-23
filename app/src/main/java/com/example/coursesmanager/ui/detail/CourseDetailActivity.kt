package com.example.coursesmanager.ui.detail

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import com.example.coursesmanager.CourseApplication
import com.example.coursesmanager.R
import com.example.coursesmanager.data.local.entity.CourseWithCategory
import com.example.coursesmanager.databinding.ActivityCourseDetailBinding
import com.example.coursesmanager.ui.addedit.AddEditCourseActivity
import com.example.coursesmanager.ui.player.VideoPlayerActivity
import com.example.coursesmanager.util.FormatUtil
import com.example.coursesmanager.util.ToastUtil
import kotlinx.coroutines.launch

class CourseDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCourseDetailBinding
    private var currentCourseWithCat: CourseWithCategory? = null

    private val courseId: String by lazy {
        intent.getStringExtra(EXTRA_COURSE_ID).orEmpty()
    }

    private val viewModel: CourseDetailViewModel by viewModels {
        val app = application as CourseApplication
        CourseDetailViewModelFactory(app.courseRepository, courseId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupListeners()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbarDetail.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupListeners() {
        // Nút Xem Trailer (ở cả Banner lẫn Card chi tiết)
        val playTrailerAction = {
            val trailerUrl = currentCourseWithCat?.course?.trailerUrl
            val title = currentCourseWithCat?.course?.title ?: "Trailer"
            if (!trailerUrl.isNullOrBlank()) {
                val intent = Intent(this, VideoPlayerActivity::class.java).apply {
                    putExtra(VideoPlayerActivity.EXTRA_VIDEO_URL, trailerUrl)
                    putExtra(VideoPlayerActivity.EXTRA_VIDEO_TITLE, title)
                }
                startActivity(intent)
            } else {
                ToastUtil.show(this, getString(R.string.err_trailer_invalid))
            }
        }

        binding.btnWatchTrailerBanner.setOnClickListener { playTrailerAction() }
        binding.btnWatchTrailerDirect.setOnClickListener { playTrailerAction() }

        // Nút Sửa khóa học
        binding.btnEditCourse.setOnClickListener {
            val intent = Intent(this, AddEditCourseActivity::class.java).apply {
                putExtra(AddEditCourseActivity.EXTRA_COURSE_ID, courseId)
            }
            startActivity(intent)
        }

        // Nút Xóa khóa học kèm AlertDialog xác nhận
        binding.btnDeleteCourse.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun showDeleteConfirmationDialog() {
        val courseTitle = currentCourseWithCat?.course?.title.orEmpty()
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_delete_course_title))
            .setMessage(getString(R.string.dialog_delete_course_msg, courseTitle))
            .setPositiveButton(getString(R.string.btn_confirm_delete)) { _, _ ->
                viewModel.deleteCourse()
            }
            .setNegativeButton(getString(R.string.btn_cancel), null)
            .show()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.course.collect { item ->
                        item?.let { bindCourseData(it) }
                    }
                }

                launch {
                    viewModel.deleteSuccess.collect { success ->
                        if (success) {
                            ToastUtil.show(this@CourseDetailActivity, getString(R.string.msg_course_deleted_success))
                            finish()
                        }
                    }
                }

                launch {
                    viewModel.errorMessage.collect { message ->
                        ToastUtil.show(this@CourseDetailActivity, message)
                    }
                }
            }
        }
    }

    private fun bindCourseData(item: CourseWithCategory) {
        currentCourseWithCat = item
        val course = item.course
        val category = item.category

        binding.tvDetailTitle.text = course.title
        binding.tvDetailInstructor.text = course.instructor
        binding.tvDetailPrice.text = FormatUtil.formatVnd(course.price)
        binding.tvDetailDuration.text = "• ${course.duration} giờ học"
        binding.tvDetailCategory.text = category?.name ?: "Khác"
        binding.tvDetailCreatedAt.text = course.createdAt
        binding.tvDetailTrailerUrl.text = course.trailerUrl
        binding.tvDetailDescription.text = course.description

        binding.ivDetailThumbnail.load(course.thumbnail) {
            crossfade(true)
            placeholder(R.drawable.ic_course_placeholder)
            error(R.drawable.ic_course_placeholder)
        }
    }

    companion object {
        const val EXTRA_COURSE_ID = "extra_course_id"
    }
}
