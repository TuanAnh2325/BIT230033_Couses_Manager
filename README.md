# DỰ ÁN QUẢN LÝ KHÓA HỌC (COURSE MANAGEMENT APP)

| Thông Tin Sinh Viên | Chi Tiết |
| :--- | :--- |
| **Họ và tên:** | Đặng Phúc Tuấn Anh |
| **Mã sinh viên:** | BIT230033 |
| **Email:** | dangphuctuananh23@gmail.com |
| **Học phần:** | Lập trình Mobile App (Android Native) |
| **GitHub:** | [TuanAnh2325](https://github.com/TuanAnh2325) |
| **Repository:** | [BIT230033_Couses_Manager](https://github.com/TuanAnh2325/BIT230033_Couses_Manager.git) |

---

## 7. SẢN PHẨM BÀN GIAO & NỘP BÀI (THEO YÊU CẦU ĐỀ BÀI)

### 1. Source code ứng dụng Mobile
- **GitHub Repository:** [https://github.com/TuanAnh2325/BIT230033_Couses_Manager.git](https://github.com/TuanAnh2325/BIT230033_Couses_Manager.git)
- **Nhánh chính (Branch):** `main`
- **Mã nguồn:** 100% Kotlin hiện đại, kiến trúc MVVM + Repository Pattern, tích hợp Room Database và Retrofit 2 REST API.

### 2. Đường dẫn Project / API MockAPI
- **MockAPI Base URL:** `https://6602521c9b451f6f27715b74.mockapi.io/api/v1/`
- **Resource Categories Endpoint:**
  - `GET / POST` : `https://6602521c9b451f6f27715b74.mockapi.io/api/v1/categories`
  - `PUT / DELETE`: `https://6602521c9b451f6f27715b74.mockapi.io/api/v1/categories/:id`
- **Resource Courses Endpoint:**
  - `GET / POST` : `https://6602521c9b451f6f27715b74.mockapi.io/api/v1/courses`
  - `GET / PUT / DELETE`: `https://6602521c9b451f6f27715b74.mockapi.io/api/v1/courses/:id`
- **Lưu ý đã thực hiện:** URL ảnh bìa (`thumbnail`) và trailer (`trailerUrl`) được lưu trữ dạng chuỗi `String` trên MockAPI, không bắt buộc upload file ảnh/video lên máy chủ.

### 3. Video thuyết trình kĩ về các chức năng
- **Nội dung video trình bày:**
  1. Giới thiệu tổng quan cấu trúc đồ án và kiến trúc MVVM.
  2. Demo trực tiếp luồng gọi MockAPI (GET danh mục, GET khóa học, POST thêm mới, PUT cập nhật, DELETE).
  3. Xử lý dữ liệu trả về từ API và lưu trữ / cache vào SQLite Room Database.
  4. Trình diễn toàn bộ chức năng: Tìm kiếm real-time, Lọc theo Chip danh mục, Thêm/Sửa/Xóa khóa học, Xem trước ảnh (Image Preview), Phát video trailer MP4 với MediaController, Ràng buộc khóa ngoại khi xóa danh mục.
- **Đường dẫn Video Thuyết trình:** `[Link Video Google Drive / YouTube Trình Bày Bài Thi]` *(Cập nhật link video bài thuyết trình tại đây)*

### 4. README hướng dẫn chạy ứng dụng
*(Xem chi tiết tại [Mục 5: Hướng dẫn cài đặt & Chạy ứng dụng](#5-hướng-dẫn-cài-đặt--chạy-ứng-dụng))*

---

## 1. GIỚI THIỆU TỔNG QUAN
Ứng dụng **Course Manager** là giải pháp quản lý khóa học trực tuyến hoàn chỉnh trên nền tảng Android di động, kết hợp giữa lưu trữ cục bộ bảo vệ tính toàn vẹn quan hệ (Room Database SQLite) và đồng bộ đám mây thời gian thực qua REST API (MockAPI.io).

### Các điểm nổi bật về công nghệ:
1. **100% Kotlin hiện đại:** Null-safety tuyệt đối, Kotlin Coroutines, StateFlow, LiveData, ViewBinding.
2. **Room Database chuẩn Google:** 
   - Định nghĩa khóa ngoại `@ForeignKey(entity = CategoryEntity::class, onDelete = ForeignKey.RESTRICT)` bảo vệ toàn vẹn dữ liệu.
   - Quan hệ 1-N `@Relation` giữa Category và Course.
   - Nạp sẵn (Pre-populate) **3 danh mục chuẩn** và **10 khóa học mẫu** có URL ảnh và video trailer thực tế ngay lần đầu chạy.
3. **MockAPI.io REST API:** Tích hợp Retrofit 2 + OkHttp 3 Logging Interceptor + Gson Converter đầy đủ các endpoint CRUD.
4. **Tải ảnh mượt mà với Coil:** Cache 2 tầng (Disk & Memory), crossfade animation, placeholder và fallback an toàn.
5. **Trình phát Video Trailer tích hợp:** Hỗ trợ phát video MP4 trực tiếp kèm thanh điều khiển MediaController (Play/Pause/Seek), giải phóng tài nguyên trong Lifecycle tránh rò rỉ bộ nhớ.
6. **Bảo vệ toàn vẹn quan hệ 2 tầng:** Kiểm tra ứng dụng chủ động đếm số lượng khóa học trước khi xóa danh mục, chặn đứng thao tác xóa nếu danh mục đang chứa khóa học và thông báo Toast rõ ràng.
7. **Bắt lỗi & Validation chặt chẽ:** Kiểm tra độ dài, học phí >= 0 đ, thời lượng >= 1 giờ, định dạng URL hợp lệ, xử lý bất đồng bộ không gây giật lag (ANR).

---

## 2. CẤU TRÚC THƯ MỤC DỰ ÁN
```
BIT230033_Couses_Manager/
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/coursesmanager/
│       │   ├── CourseApplication.kt
│       │   ├── data/
│       │   │   ├── local/
│       │   │   │   ├── AppDatabase.kt
│       │   │   │   ├── dao/
│       │   │   │   │   ├── CategoryDao.kt
│       │   │   │   │   └── CourseDao.kt
│       │   │   │   └── entity/
│       │   │   │       ├── CategoryEntity.kt
│       │   │   │       ├── CourseEntity.kt
│       │   │   │       ├── CategoryWithCourses.kt
│       │   │   │       └── CourseWithCategory.kt
│       │   │   ├── remote/
│       │   │   │   ├── MockApiService.kt
│       │   │   │   ├── RetrofitClient.kt
│       │   │   │   └── model/
│       │   │   │       ├── CategoryDto.kt
│       │   │   │       └── CourseDto.kt
│       │   │   └── repository/
│       │   │       ├── CategoryRepository.kt
│       │   │       └── CourseRepository.kt
│       │   ├── ui/
│       │   │   ├── adapter/
│       │   │   │   ├── CategoryAdapter.kt
│       │   │   │   └── CourseAdapter.kt
│       │   │   ├── addedit/
│       │   │   │   ├── AddEditCourseActivity.kt
│       │   │   │   └── AddEditCourseViewModel.kt
│       │   │   ├── category/
│       │   │   │   ├── CategoryManagementActivity.kt
│       │   │   │   └── CategoryViewModel.kt
│       │   │   ├── detail/
│       │   │   │   ├── CourseDetailActivity.kt
│       │   │   │   └── CourseDetailViewModel.kt
│       │   │   ├── main/
│       │   │   │   ├── MainActivity.kt
│       │   │   │   └── MainViewModel.kt
│       │   │   └── player/
│       │   │       └── VideoPlayerActivity.kt
│       │   └── util/
│       │       ├── FormatUtil.kt
│       │       ├── Resource.kt
│       │       ├── SampleDataProvider.kt
│       │       ├── ToastUtil.kt
│       │       └── ValidationUtil.kt
│       └── res/
│           ├── drawable/
│           ├── layout/
│           └── values/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── .gitignore
└── README.md
```

---

## 3. CÁC MÀN HÌNH CHÍNH & TÍNH NĂNG
1. **Màn hình Danh sách khóa học (MainActivity):**
   - Tìm kiếm thời gian thực theo tên khóa học.
   - Lọc nhanh theo danh mục bằng thanh Chip cuộn ngang ("Tất cả", "Lập trình Mobile", "Lập trình Web", "Trí tuệ nhân tạo").
   - Hiển thị Card khóa học kèm ảnh bìa, badge danh mục, tên giảng viên, học phí VND, thời lượng.
   - Nút bấm xem Quản lý danh mục.
   - Nút nổi (+) thêm khóa học mới.
   - Kéo để làm mới dữ liệu (Swipe to Refresh) và trạng thái danh sách rỗng (Empty State).

2. **Màn hình Chi tiết khóa học (CourseDetailActivity):**
   - Header ảnh bìa lớn kèm badge danh mục và hiệu ứng cuộn thu gọn (CollapsingToolbar).
   - Nút "Xem Trailer" mở trình phát video trailer.
   - Hiển thị chi tiết học phí, thời lượng, giảng viên, ngày tạo, nội dung mô tả.
   - Nút "Chỉnh sửa" và "Xóa" khóa học kèm hộp thoại xác nhận an toàn.

3. **Màn hình Thêm / Cập nhật khóa học (AddEditCourseActivity):**
   - Form nhập liệu: Tên khóa học, Giảng viên, Học phí, Thời lượng, URL ảnh bìa, URL trailer, Mô tả.
   - Spinner chọn danh mục nạp động từ Room DB / MockAPI.
   - **Tính năng Xem trước ảnh (Image Preview):** Cho phép người dùng kiểm tra ảnh bìa từ URL trước khi lưu.
   - Kiểm tra dữ liệu (Validation) và cảnh báo lỗi cụ thể cho từng trường.

4. **Màn hình Quản lý danh mục (CategoryManagementActivity):**
   - Danh sách các danh mục kèm huy hiệu hiển thị **số lượng khóa học liên kết** (`X khóa học`).
   - Thêm danh mục mới qua Dialog.
   - Sửa tên và mô tả danh mục.
   - **Ràng buộc an toàn khi xóa:** Không cho phép xóa danh mục nếu đang có khóa học thuộc danh mục đó.

5. **Màn hình Phát Video Trailer (VideoPlayerActivity):**
   - Trình phát video MP4 trực tiếp với MediaController (Play, Pause, Seek).
   - Hiển thị thanh tiến trình đệm video (buffering) và thông báo lỗi rõ ràng nếu đường dẫn video không hợp lệ hoặc mất kết nối.

---

## 4. CẤU HÌNH MOCKAPI.IO
Mặc định ứng dụng đã được cấu hình với MockAPI endpoint hoặc có thể thay thế bằng endpoint cá nhân của bạn trong file:
`app/src/main/java/com/example/coursesmanager/data/remote/RetrofitClient.kt`:
```kotlin
var customBaseUrl: String = "https://6602521c9b451f6f27715b74.mockapi.io/api/v1/"
```

### Cấu trúc JSON Object trên MockAPI:
**Resource: `/categories`**
```json
{
  "id": "1",
  "name": "Lập trình Mobile",
  "description": "Android, Flutter, React Native"
}
```

**Resource: `/courses`**
```json
{
  "id": "1",
  "title": "Lập trình Android cơ bản với Kotlin",
  "description": "Học lập trình Android hiện đại với Jetpack & Room DB",
  "categoryId": "1",
  "instructor": "Nguyễn Văn A",
  "price": 500000.0,
  "duration": 30,
  "thumbnail": "https://images.unsplash.com/photo-1607252650355-f7fd0460ccdb?w=600&auto=format&fit=crop&q=80",
  "trailerUrl": "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
  "createdAt": "2026-10-13"
}
```

---

## 5. HƯỚNG DẪN CÀI ĐẶT & CHẠY ỨNG DỤNG

### Bước 1: Yêu cầu môi trường
- **Android Studio:** Hedgehog (2023.1.1) trở lên (Iguana, Jellyfish, Koala, Ladybug).
- **JDK:** Java 17 LTS.
- **Android SDK:** Compile SDK 34, Min SDK 24.
- **Kết nối mạng Internet:** Cần kết nối Internet để tải thư viện Gradle, tải ảnh từ Unsplash và gọi MockAPI.

### Bước 2: Clone hoặc Mở dự án
1. Mở Android Studio.
2. Chọn **File -> Open...** (hoặc chọn Open từ màn hình Welcome).
3. Điều hướng và chọn thư mục `BIT230033_Couses_Manager`.
4. Đợi Android Studio và Gradle thực hiện đồng bộ dự án (Gradle Sync).

### Bước 3: Build và Chạy ứng dụng
1. Khởi động máy ảo Android Emulator (Pixel khuyến nghị với Android 11 trở lên, API 30+) hoặc cắm điện thoại Android thật đã bật **USB Debugging**.
2. Trên thanh công cụ Android Studio, chọn module run là **app**.
3. Bấm nút **Run 'app'** (hoặc tổ hợp phím `Shift + F10`).
4. Ứng dụng sẽ tự động biên dịch, cài đặt file APK lên thiết bị và mở giao diện ứng dụng.

---
*Bản quyền dự án thuộc về sinh viên Đặng Phúc Tuấn Anh - BIT230033.*
