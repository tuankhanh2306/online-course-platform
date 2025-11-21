package edu.uth.online_course_platform.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // Lưu ý: Dùng @Controller, KHÔNG dùng @RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        // Chuyển hướng về file html mong muốn
        return "forward:/pages/login.html";
    }

}