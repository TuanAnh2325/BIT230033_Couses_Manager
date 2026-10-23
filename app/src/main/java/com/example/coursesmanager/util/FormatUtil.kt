package com.example.coursesmanager.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FormatUtil {

    /**
     * Định dạng tiền tệ VND chuẩn Việt Nam (ví dụ: 500.000 đ)
     */
    fun formatVnd(amount: Double): String {
        val symbols = DecimalFormatSymbols(Locale("vi", "VN")).apply {
            groupingSeparator = '.'
            decimalSeparator = ','
        }
        val formatter = DecimalFormat("#,###", symbols)
        return "${formatter.format(amount)} đ"
    }

    /**
     * Lấy ngày hiện tại định dạng YYYY-MM-DD
     */
    fun currentDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}
