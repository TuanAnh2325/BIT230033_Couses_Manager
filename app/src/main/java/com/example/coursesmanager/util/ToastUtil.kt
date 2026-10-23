package com.example.coursesmanager.util

import android.content.Context
import android.widget.Toast

object ToastUtil {
    private var currentToast: Toast? = null

    /**
     * Hiển thị Toast ngắn an toàn, hủy Toast cũ nếu đang hiển thị để tránh spam
     */
    fun show(context: Context?, message: String?) {
        if (context == null || message.isNullOrBlank()) return
        currentToast?.cancel()
        currentToast = Toast.makeText(context.applicationContext, message, Toast.LENGTH_SHORT)
        currentToast?.show()
    }

    /**
     * Hiển thị Toast dài khi cần người dùng đọc kỹ thông báo lỗi/nghiệp vụ
     */
    fun showLong(context: Context?, message: String?) {
        if (context == null || message.isNullOrBlank()) return
        currentToast?.cancel()
        currentToast = Toast.makeText(context.applicationContext, message, Toast.LENGTH_LONG)
        currentToast?.show()
    }
}
