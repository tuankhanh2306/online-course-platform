package edu.uth.online_course_platform.exceptions;

public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(999, "Uncategorized Exception"),
    USER_EXISTED(1001, "User đã tồn tại!"),
    USERNAME_INVALID(1003,"Họ tên không được để trống"),
    EMAIL_INVALID(1004,"Email Đã tồn tại"),
    USERNAME_NOT_EXISTED(1005,"User không tồn tại"),
    PASSWORD_INVALID(1007,"Lỗi mật khẩu"),
    USER_NOT_FOUND(1008, "Không tìm thấy người dùng"),

    // Chuyên viên
    SPECIALIST_NOT_FOUND(1101,"Không tìm thấy chuyên viên"),
    SPECIALIST_EXISTED(1102, "Chuyên viên đã tồn tại"),
    SPECIALIST_INVALID(1103, "Thông tin chuyên viên không hợp lệ"),

    // Dịch vụ
    SERVICE_NOT_FOUND(1201, "Không tìm thấy dịch vụ"),
    SERVICE_EXISTED(1202, "Dịch vụ đã tồn tại"),
    SERVICE_INVALID(1203, "Thông tin dịch vụ không hợp lệ"),

    // Đặt lịch hẹn
    APPOINTMENT_NOT_FOUND(1301, "Không tìm thấy lịch hẹn"),
    APPOINTMENT_CONFLICT(1302, "Khung giờ đã được đặt, vui lòng chọn giờ khác"),
    APPOINTMENT_INVALID(1303, "Thông tin đặt lịch không hợp lệ"),
    APPOINTMENT_ALREADY_COMPLETED(1304, "Lịch hẹn đã hoàn tất, không thể thay đổi"),

    // Phản hồi
    FEEDBACK_NOT_FOUND(1401, "Không tìm thấy phản hồi"),
    FEEDBACK_INVALID(1402, "Nội dung phản hồi không hợp lệ"),

    // Thanh toán
    PAYMENT_FAILED(1501, "Thanh toán thất bại"),
    PAYMENT_METHOD_INVALID(1502, "Phương thức thanh toán không hợp lệ"),

    // Token & xác thực
    JWT_TOKEN_EXPIRED(1601, "Token đã hết hạn"),
    JWT_TOKEN_INVALID(1602, "Token không hợp lệ"),
    JWT_TOKEN_MISSING(1603, "Thiếu token xác thực"),
    INVALID_CREDENTIALS(1604, "Mat khẩu hoặc email đăng nhập không đúng"),

    PASSWORD_CONFIRM_NOT_MATCH(1701,"Mật khẩu xác thực không đúng")
    ;


    private int code;
    private String message;
    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}