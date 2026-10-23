package com.example.coursesmanager.ui.main

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coursesmanager.CourseApplication
import com.example.coursesmanager.R
import com.example.coursesmanager.databinding.ActivityMainBinding
import com.example.coursesmanager.ui.adapter.CourseAdapter
import com.example.coursesmanager.ui.addedit.AddEditCourseActivity
import com.example.coursesmanager.ui.category.CategoryManagementActivity
import com.example.coursesmanager.ui.detail.CourseDetailActivity
import com.example.coursesmanager.util.ToastUtil
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var courseAdapter: CourseAdapter

    private val viewModel: MainViewModel by viewModels {
        val app = application as CourseApplication
        MainViewModelFactory(app.courseRepository, app.categoryRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSearchAndFilter()
        setupListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        courseAdapter = CourseAdapter { item ->
            val intent = Intent(this, CourseDetailActivity::class.java).apply {
                putExtra(CourseDetailActivity.EXTRA_COURSE_ID, item.course.id)
            }
            startActivity(intent)
        }

        binding.rvCourses.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = courseAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupSearchAndFilter() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString().orEmpty()
                viewModel.onSearchQueryChanged(query)
                binding.btnClearSearch.visibility = if (query.isNotEmpty()) View.VISIBLE else View.GONE
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnClearSearch.setOnClickListener {
            binding.etSearch.text?.clear()
        }

        binding.chipAll.setOnClickListener {
            viewModel.onCategoryFilterSelected("")
        }
    }

    private fun setupListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshAll(silent = false)
        }

        binding.fabAddCourse.setOnClickListener {
            val intent = Intent(this, AddEditCourseActivity::class.java)
            startActivity(intent)
        }

        binding.btnManageCategories.setOnClickListener {
            val intent = Intent(this, CategoryManagementActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.courses.collect { list ->
                        courseAdapter.submitList(list)
                        binding.layoutEmptyState.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                        binding.tvCourseCountHeader.text = "Tổng số: ${list.size} khóa học"
                    }
                }

                launch {
                    viewModel.categories.collect { categoriesList ->
                        updateCategoryChips(categoriesList)
                    }
                }

                launch {
                    viewModel.isLoading.collect { loading ->
                        binding.swipeRefreshLayout.isRefreshing = loading
                        binding.progressBar.visibility = if (loading && courseAdapter.itemCount == 0) View.VISIBLE else View.GONE
                    }
                }

                launch {
                    viewModel.toastEvent.collect { message ->
                        ToastUtil.show(this@MainActivity, message)
                    }
                }
            }
        }
    }

    private fun updateCategoryChips(categories: List<com.example.coursesmanager.data.local.entity.CategoryEntity>) {
        // Giữ lại Chip "Tất cả" ở đầu, xóa các chip cũ
        val allChip = binding.chipAll
        binding.chipGroupCategories.removeAllViews()
        binding.chipGroupCategories.addView(allChip)

        val selectedCatId = viewModel.selectedCategoryId.value

        categories.forEach { category ->
            val chip = Chip(this).apply {
                text = category.name
                isCheckable = true
                isChecked = category.id == selectedCatId
                setOnClickListener {
                    viewModel.onCategoryFilterSelected(category.id)
                }
            }
            binding.chipGroupCategories.addView(chip)
        }

        if (selectedCatId.isEmpty()) {
            allChip.isChecked = true
        }
    }
}
