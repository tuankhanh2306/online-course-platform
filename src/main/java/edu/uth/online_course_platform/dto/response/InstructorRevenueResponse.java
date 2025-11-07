package edu.uth.online_course_platform.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class InstructorRevenueResponse {
    private long totalEnrollments; // Tổng số lượt đăng ký
    private BigDecimal totalRevenue;   // Tổng doanh thu
}