package edu.uth.online_course_platform.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class UpdateCourseRequest {
    // Giảng viên có thể cập nhật tiêu đề, mô tả và giá cả

    private String title;

    private String description;

    private BigDecimal price;

    private String imageUrl;
}