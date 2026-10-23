package com.example.coursesmanager.util

import com.example.coursesmanager.data.local.entity.CategoryEntity
import com.example.coursesmanager.data.local.entity.CourseEntity

object SampleDataProvider {

    // 3 Danh mục theo đúng yêu cầu đề bài
    val sampleCategories = listOf(
        CategoryEntity(
            id = "1",
            name = "Lập trình Mobile",
            description = "Android, Flutter, React Native và phát triển ứng dụng di động đa nền tảng."
        ),
        CategoryEntity(
            id = "2",
            name = "Lập trình Web",
            description = "Frontend (React, Vue) và Backend (NodeJS, Spring Boot, ASP.NET Core)."
        ),
        CategoryEntity(
            id = "3",
            name = "Trí tuệ nhân tạo",
            description = "AI, Machine Learning, Deep Learning, Computer Vision và Generative AI."
        )
    )

    // 10 Khóa học mẫu với URL ảnh thực tế và video MP4 trực tiếp chất lượng cao
    val sampleCourses = listOf(
        CourseEntity(
            id = "1",
            title = "Lập trình Android cơ bản với Kotlin",
            description = "Học lập trình Android hiện đại với Kotlin, Jetpack components, Room Database, Retrofit và Coroutines từ cơ bản đến nâng cao.",
            categoryId = "1",
            instructor = "Nguyễn Văn A",
            price = 500000.0,
            duration = 30,
            thumbnail = "https://images.unsplash.com/photo-1607252650355-f7fd0460ccdb?w=600&auto=format&fit=crop&q=80",
            trailerUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            createdAt = "2026-10-13"
        ),
        CourseEntity(
            id = "2",
            title = "Phát triển ứng dụng Flutter từ số 0",
            description = "Làm chủ Flutter & Dart để xây dựng ứng dụng di động chạy mượt mà trên cả iOS và Android với một cơ sở mã nguồn duy nhất.",
            categoryId = "1",
            instructor = "Trần Thị B",
            price = 650000.0,
            duration = 36,
            thumbnail = "https://images.unsplash.com/photo-1551650975-87deedd944c3?w=600&auto=format&fit=crop&q=80",
            trailerUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
            createdAt = "2026-10-14"
        ),
        CourseEntity(
            id = "3",
            title = "React Native Masterclass",
            description = "Xây dựng ứng dụng native với JavaScript/TypeScript, tích hợp Redux Toolkit, React Navigation và native modules.",
            categoryId = "1",
            instructor = "Lê Hoàng Nam",
            price = 550000.0,
            duration = 28,
            thumbnail = "https://images.unsplash.com/photo-1526498460520-4c246339dccb?w=600&auto=format&fit=crop&q=80",
            trailerUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
            createdAt = "2026-10-15"
        ),
        CourseEntity(
            id = "4",
            title = "Fullstack Web với React và Node.js",
            description = "Khóa học toàn diện từ Frontend hiện đại với React, Vite, TailwindCSS đến Backend RESTful API với Express và MongoDB.",
            categoryId = "2",
            instructor = "Phạm Quang Huy",
            price = 800000.0,
            duration = 45,
            thumbnail = "https://images.unsplash.com/photo-1633356122544-f134324a6cee?w=600&auto=format&fit=crop&q=80",
            trailerUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyBlazes.mp4",
            createdAt = "2026-10-16"
        ),
        CourseEntity(
            id = "5",
            title = "Lập trình Backend chuyên nghiệp với Spring Boot",
            description = "Xây dựng hệ thống Microservices quy mô lớn, bảo mật với Spring Security, tối ưu hóa cơ sở dữ liệu và Docker container.",
            categoryId = "2",
            instructor = "Vũ Đình Tuấn",
            price = 750000.0,
            duration = 40,
            thumbnail = "https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=600&auto=format&fit=crop&q=80",
            trailerUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4",
            createdAt = "2026-10-17"
        ),
        CourseEntity(
            id = "6",
            title = "Frontend Hiện Đại với Next.js 14",
            description = "Học Server-Side Rendering (SSR), Server Components, App Router và triển khai sản phẩm lên nền tảng đám mây Vercel.",
            categoryId = "2",
            instructor = "Nguyễn Khánh Linh",
            price = 600000.0,
            duration = 32,
            thumbnail = "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=600&auto=format&fit=crop&q=80",
            trailerUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
            createdAt = "2026-10-18"
        ),
        CourseEntity(
            id = "7",
            title = "Nhập môn Trí tuệ Nhân tạo & Machine Learning",
            description = "Hiểu rõ các thuật toán máy học phổ biến: Hồi quy tuyến tính, SVM, Random Forest và ứng dụng với Python thư viện Scikit-learn.",
            categoryId = "3",
            instructor = "TS. Đặng Minh Tuấn",
            price = 900000.0,
            duration = 50,
            thumbnail = "https://images.unsplash.com/photo-1677442136019-21780ecad995?w=600&auto=format&fit=crop&q=80",
            trailerUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackSeeTheWorld.mp4",
            createdAt = "2026-10-19"
        ),
        CourseEntity(
            id = "8",
            title = "Deep Learning với PyTorch cho Computer Vision",
            description = "Huấn luyện mạng nơ-ron tích chập (CNN), Vision Transformers, bài toán phân loại ảnh, nhận diện khuôn mặt và phát hiện vật thể.",
            categoryId = "3",
            instructor = "Hoàng Mai Hương",
            price = 950000.0,
            duration = 48,
            thumbnail = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=600&auto=format&fit=crop&q=80",
            trailerUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
            createdAt = "2026-10-20"
        ),
        CourseEntity(
            id = "9",
            title = "Ứng dụng Generative AI & Large Language Models (LLM)",
            description = "Xây dựng ứng dụng AI với OpenAI API, LangChain, RAG (Retrieval-Augmented Generation) và Fine-tuning mô hình ngôn ngữ lớn.",
            categoryId = "3",
            instructor = "Bùi Thế Duy",
            price = 1200000.0,
            duration = 38,
            thumbnail = "https://images.unsplash.com/photo-1620712943543-bcc4688e7485?w=600&auto=format&fit=crop&q=80",
            trailerUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4",
            createdAt = "2026-10-21"
        ),
        CourseEntity(
            id = "10",
            title = "Kiến trúc Android Jetpack & Room Database Chuyên Sâu",
            description = "Xây dựng ứng dụng Android quy mô enterprise với Clean Architecture, Flow, StateFlow, Room Migration và Coroutines Concurrency.",
            categoryId = "1",
            instructor = "Đoàn Minh Khoa",
            price = 700000.0,
            duration = 35,
            thumbnail = "https://images.unsplash.com/photo-1555774698-0b77e0d5fac6?w=600&auto=format&fit=crop&q=80",
            trailerUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            createdAt = "2026-10-22"
        )
    )
}
