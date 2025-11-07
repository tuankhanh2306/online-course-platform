package edu.uth.online_course_platform.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Enum quản lý tập trung các mã lỗi trong ứng dụng.
 * Mỗi mã lỗi bao gồm:
 * - code: Mã lỗi nội bộ (dùng cho client)
 * - message: Thông báo lỗi chi tiết (trả về cho người dùng)
 * - statusCode: Mã trạng thái HTTP (dùng cho ResponseEntity)
 */
public enum ErrorCode {

    // --- 1. Lỗi chung (Generic Errors) ---
    UNCATEGORIZED_EXCEPTION(999, "Lỗi hệ thống không xác định", HttpStatus.INTERNAL_SERVER_ERROR),
    VALIDATION_ERROR(1000, "Dữ liệu đầu vào không hợp lệ", HttpStatus.BAD_REQUEST),
    METHOD_NOT_ALLOWED(1001, "Phương thức HTTP không được hỗ trợ", HttpStatus.METHOD_NOT_ALLOWED),
    RESOURCE_NOT_FOUND(1002, "Không tìm thấy tài nguyên được yêu cầu", HttpStatus.NOT_FOUND),

    // --- 2. Lỗi Xác thực & Phân quyền (Auth Errors) ---
    USER_EXISTED(1101, "Địa chỉ email này đã được sử dụng", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1102, "Không tìm thấy người dùng", HttpStatus.NOT_FOUND),
    INVALID_CREDENTIALS(1103, "Email hoặc mật khẩu không chính xác", HttpStatus.UNAUTHORIZED),
    PASSWORD_CONFIRM_NOT_MATCH(1104, "Mật khẩu xác nhận không khớp", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(1105,"Lỗi Email", HttpStatus.BAD_REQUEST),
    // 403 Forbidden (Đã xác thực nhưng không có quyền)
    UNAUTHORIZED(1201, "Bạn không có quyền thực hiện hành động này", HttpStatus.FORBIDDEN),

    // 401 Unauthorized (Chưa xác thực)
    JWT_TOKEN_MISSING(1202, "Yêu cầu thiếu token xác thực", HttpStatus.UNAUTHORIZED),
    JWT_TOKEN_INVALID(1203, "Token xác thực không hợp lệ hoặc đã bị thay đổi", HttpStatus.UNAUTHORIZED),
    JWT_TOKEN_EXPIRED(1204, "Token xác thực đã hết hạn", HttpStatus.UNAUTHORIZED),

    // --- 3. Lỗi Nghiệp vụ (Business Logic Errors) ---
    COURSE_NOT_FOUND(1301, "Không tìm thấy khóa học", HttpStatus.NOT_FOUND),
    LESSON_NOT_FOUND(1302, "Không tìm thấy bài học", HttpStatus.NOT_FOUND),
    ENROLLMENT_EXISTED(1303, "Bạn đã đăng ký khóa học này rồi", HttpStatus.CONFLICT), // 409 Conflict
    NOT_ENROLLED(1304, "Bạn chưa đăng ký khóa học này", HttpStatus.FORBIDDEN),
    FEEDBACK_EXISTED(1305, "Bạn đã đánh giá khóa học này rồi", HttpStatus.CONFLICT),

    // Thanh toán
    PAYMENT_FAILED(1401, "Quá trình thanh toán thất bại", HttpStatus.BAD_REQUEST),
    PAYMENT_METHOD_INVALID(1402, "Phương thức thanh toán không hợp lệ", HttpStatus.BAD_REQUEST);


    private final int code;
    private final String message;
    private final HttpStatus statusCode;

    ErrorCode(int code, String message, HttpStatus statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }
}