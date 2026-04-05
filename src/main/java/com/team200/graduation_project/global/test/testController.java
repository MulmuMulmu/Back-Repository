package com.team200.graduation_project.global.test;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class testController {

    @GetMapping("/test/health-check")
    public ResponseEntity<String> get() {
        return ResponseEntity.ok("Server is runnung!");
    }

}
