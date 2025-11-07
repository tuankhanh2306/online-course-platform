package edu.uth.online_course_platform.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class FeedbackResponse {
    private Long feedbackId;
    private int rating;
    private String comment;
    private String studentName;
    private LocalDateTime createdAt;
}