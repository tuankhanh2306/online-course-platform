package edu.uth.online_course_platform.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    // Đường dẫn thư mục lưu ảnh (lấy từ application.properties)
    @Value("${file.upload-dir}")
    private String uploadDir;

    // Đường dẫn base URL để truy cập ảnh (lấy từ application.properties)
    // Ví dụ: /images/
    @Value("${file.access-path}")
    private String accessPath;

    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "https://via.placeholder.com/400x200?text=No+Image"; // Ảnh mặc định
        }

        try {
            // Tạo thư mục nếu chưa tồn tại
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Tạo tên file duy nhất để tránh trùng lặp
            String originalFileName = file.getOriginalFilename();
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString() + fileExtension;

            // Lưu file vào thư mục
            Path filePath = uploadPath.resolve(newFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Trả về đường dẫn tương đối để truy cập (ví dụ: /images/abc-123.jpg)
            return accessPath + newFileName;

        } catch (IOException ex) {
            throw new RuntimeException("Could not store file. Please try again!", ex);
        }
    }
}