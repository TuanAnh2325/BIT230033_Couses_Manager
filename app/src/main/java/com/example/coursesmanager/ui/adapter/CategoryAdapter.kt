package com.example.coursesmanager.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.coursesmanager.R
import com.example.coursesmanager.data.local.dao.CategoryWithCount
import com.example.coursesmanager.databinding.ItemCategoryBinding

class CategoryAdapter(
    private val onEditClick: (CategoryWithCount) -> Unit,
    private val onDeleteClick: (CategoryWithCount) -> Unit
) : ListAdapter<CategoryWithCount, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CategoryViewHolder(
        private val binding: ItemCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CategoryWithCount) {
            val category = item.category
            binding.tvCategoryName.text = category.name
            binding.tvCategoryDescription.text = category.description
            binding.tvCourseCountBadge.text = binding.root.context.getString(R.string.category_course_count, item.courseCount)

            binding.btnEditCategory.setOnClickListener {
                onEditClick(item)
            }

            binding.btnDeleteCategory.setOnClickListener {
                onDeleteClick(item)
            }
        }
    }

    class CategoryDiffCallback : DiffUtil.ItemCallback<CategoryWithCount>() {
        override fun areItemsTheSame(oldItem: CategoryWithCount, newItem: CategoryWithCount): Boolean {
            return oldItem.category.id == newItem.category.id
        }

        override fun areContentsTheSame(oldItem: CategoryWithCount, newItem: CategoryWithCount): Boolean {
            return oldItem == newItem
        }
    }
}
