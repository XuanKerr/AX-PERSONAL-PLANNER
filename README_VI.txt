AX PERSONAL PLANNER - Android
================================

BẢN DỰ ÁN CÓ HỖ TRỢ BIÊN DỊCH TRỰC TUYẾN
Bạn KHÔNG cần cài Android Studio, Android SDK hoặc Java trên máy tính.

CÁCH TẠO FILE APK BẰNG GITHUB ACTIONS
1. Đăng nhập hoặc tạo tài khoản miễn phí tại https://github.com
2. Tạo repository mới:
   - Nhấn dấu + > New repository
   - Repository name: AX-PERSONAL-PLANNER
   - Chọn Public (để dùng GitHub Actions miễn phí dễ dàng)
   - Nhấn Create repository
3. Tải lên toàn bộ nội dung bên trong thư mục AX_PERSONAL_PLANNER đã giải nén.
   Lưu ý: cần tải cả thư mục ẩn .github và file .github/workflows/android-build.yml.
   Trên trang repository, chọn Add file > Upload files; kéo thả toàn bộ tệp/thư mục.
4. Sau khi tải lên xong, mở tab Actions.
5. Chọn workflow "Build AX PERSONAL PLANNER APK".
6. Nhấn Run workflow > Run workflow.
7. Đợi quá trình build hoàn tất (thường vài phút).
8. Mở lần chạy vừa hoàn thành, kéo xuống phần Artifacts.
9. Tải "AX-PERSONAL-PLANNER-APK.zip" về máy tính.
10. Giải nén, lấy app-debug.apk, chép sang điện thoại Android và cài đặt.

LƯU Ý
- GitHub sẽ biên dịch APK trên máy chủ của họ; máy tính của bạn không cần bộ công cụ Android.
- Lần đầu tải SDK và Gradle cần Internet ở phía máy chủ.
- Ứng dụng sau khi cài đặt vẫn lưu dữ liệu trên điện thoại và dùng offline.
- Không chia sẻ repository công khai nếu sau này bạn thêm dữ liệu cá nhân hoặc khóa bí mật. Mã nguồn hiện tại không chứa dữ liệu công việc cá nhân.
- Bản APK debug dùng để cài thử; trước khi phát hành lâu dài nên tạo bản release có chữ ký riêng và lưu khóa ký an toàn.

CHỨC NĂNG
- Lưu dữ liệu cục bộ bằng SQLite.
- Tối đa 15 công việc chưa hoàn thành.
- Thêm, sửa, xóa công việc.
- Chọn ngày/giờ thực hiện và nhắc trước.
- Trạng thái: Chưa thực hiện, Đang thực hiện, Tạm hoãn, Đã hoàn thành.
- Sắp xếp theo ngày thực hiện.
- Khôi phục lịch nhắc sau khi khởi động lại điện thoại.

YÊU CẦU
- Android 8.0 trở lên.
