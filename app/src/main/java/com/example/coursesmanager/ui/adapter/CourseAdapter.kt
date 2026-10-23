package com.example.coursesmanager.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.coursesmanager.R
import com.example.coursesmanager.data.local.entity.CourseWithCategory
import com.example.coursesmanager.databinding.ItemCourseBinding
import com.example.coursesmanager.util.FormatUtil

class CourseAdapter(
    private val onCourseClick: (CourseWithCategory) -> Unit
) : ListAdapter<CourseWithCategory, CourseAdapter.CourseViewHolder>(CourseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val binding = ItemCourseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CourseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CourseViewHolder(
        private val binding: ItemCourseBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CourseWithCategory) {
            val course = item.course
            val category = item.category

            binding.tvTitle.text = course.title
            binding.tvInstructor.text = "GV: ${course.instructor} • ${course.duration} giờ"
            binding.tvPrice.text = FormatUtil.formatVnd(course.price)
            binding.tvCategoryBadge.text = category?.name ?: "Khác"

            // Tải ảnh bìa bằng Coil kèm hiệu ứng chuyển đổi mượt mà và ảnh fallback
            binding.ivThumbnail.load(course.thumbnail) {
                crossfade(true)
                placeholder(R.drawable.ic_course_placeholder)
                error(R.drawable.ic_course_placeholder)
            }

            binding.root.setOnClickListener {
                onCourseClick(item)
            }
        }
    }

    class CourseDiffCallback : DiffUtil.ItemCallback<CourseWithCategory>() {
        override fun areItemsTheSame(oldItem: CourseWithCategory, newItem: CourseWithCategory): Boolean {
            return oldItem.course.id == newItem.course.id
        }

        override fun areContentsTheSame(oldItem: CourseWithCategory, newItem: CourseWithCategory): Boolean {
            return oldItem == newItem
        }
    }
}
