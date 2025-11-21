package edu.uth.online_course_platform.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
@Getter
@Setter
@AllArgsConstructor
public class UpdateCourseRequest {
    // Giảng viên có thể cập nhật tiêu đề, mô tả và giá cả

    private String title;

    private String description;

    private BigDecimal price;

    private MultipartFile imageFile;


}