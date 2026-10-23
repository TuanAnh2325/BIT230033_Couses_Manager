package com.example.coursesmanager.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coursesmanager.CourseApplication
import com.example.coursesmanager.R
import com.example.coursesmanager.data.local.dao.CategoryWithCount
import com.example.coursesmanager.databinding.ActivityCategoryManagementBinding
import com.example.coursesmanager.databinding.DialogCategoryFormBinding
import com.example.coursesmanager.ui.adapter.CategoryAdapter
import com.example.coursesmanager.util.ToastUtil
import kotlinx.coroutines.launch

class CategoryManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryManagementBinding
    private lateinit var categoryAdapter: CategoryAdapter

    private val viewModel: CategoryViewModel by viewModels {
        val app = application as CourseApplication
        CategoryViewModelFactory(app.categoryRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbarCategory.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        categoryAdapter = CategoryAdapter(
            onEditClick = { item -> showCategoryDialog(item) },
            onDeleteClick = { item -> handleDeleteCategory(item) }
        )

        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(this@CategoryManagementActivity)
            adapter = categoryAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupListeners() {
        binding.swipeRefreshCategory.setOnRefreshListener {
            viewModel.refreshCategories()
        }

        binding.fabAddCategory.setOnClickListener {
            showCategoryDialog(null)
        }
    }

    /**
     * XỬ LÝ XÓA DANH MỤC TUÂN THỦ RÀNG BUỘC TOÀN VẸN 1-N (Mục 3.5 đề bài)
     */
    private fun handleDeleteCategory(item: CategoryWithCount) {
        val count = item.courseCount
        val categoryName = item.category.name

        // RÀNG BUỘC: Nếu danh mục đang có khóa học -> Chặn ngay lập tức và Toast thông báo rõ ràng!
        if (count > 0) {
            val errorMsg = getString(R.string.err_cannot_delete_cat_has_courses, categoryName, count)
            AlertDialog.Builder(this)
                .setTitle("Không thể xóa danh mục")
                .setMessage(errorMsg)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Đã hiểu", null)
                .show()
            ToastUtil.showLong(this, errorMsg)
            return
        }

        // Nếu không có khóa học nào thuộc danh mục này -> Xác nhận trước khi xóa
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_delete_category_title))
            .setMessage(getString(R.string.dialog_delete_category_msg, categoryName))
            .setPositiveButton(getString(R.string.btn_confirm_delete)) { _, _ ->
                viewModel.deleteCategory(item)
            }
            .setNegativeButton(getString(R.string.btn_cancel), null)
            .show()
    }

    private fun showCategoryDialog(existingItem: CategoryWithCount?) {
        val dialogBinding = DialogCategoryFormBinding.inflate(LayoutInflater.from(this))
        val isEdit = existingItem != null

        dialogBinding.tvDialogTitle.text = if (isEdit) {
            getString(R.string.dialog_edit_category_title)
        } else {
            getString(R.string.dialog_add_category_title)
        }

        if (isEdit) {
            dialogBinding.etCategoryName.setText(existingItem!!.category.name)
            dialogBinding.etCategoryDescription.setText(existingItem.category.description)
        }

        AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setPositiveButton(if (isEdit) getString(R.string.btn_update) else getString(R.string.btn_save)) { _, _ ->
                val name = dialogBinding.etCategoryName.text?.toString().orEmpty()
                val desc = dialogBinding.etCategoryDescription.text?.toString().orEmpty()

                if (isEdit) {
                    viewModel.updateCategory(existingItem!!.category.id, name, desc)
                } else {
                    viewModel.addCategory(name, desc)
                }
            }
            .setNegativeButton(getString(R.string.btn_cancel), null)
            .show()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.categoriesWithCount.collect { list ->
                        categoryAdapter.submitList(list)
                        binding.layoutEmptyCategory.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                    }
                }

                launch {
                    viewModel.isLoading.collect { loading ->
                        binding.swipeRefreshCategory.isRefreshing = loading
                        binding.progressBarCategory.visibility = if (loading && categoryAdapter.itemCount == 0) View.VISIBLE else View.GONE
                    }
                }

                launch {
                    viewModel.toastEvent.collect { message ->
                        ToastUtil.show(this@CategoryManagementActivity, message)
                    }
                }
            }
        }
    }
}
