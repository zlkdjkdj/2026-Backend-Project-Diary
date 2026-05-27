package com.jaehyun.diary.api;

import com.jaehyun.diary.service.BackupService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class BackupApiController {

    @Autowired
    private BackupService backupService;

    @GetMapping("/backup")
    public void downloadBackup(HttpServletResponse response) throws IOException {
        response.setContentType("application/zip");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"diary_backup.zip\"");
        backupService.createBackup(response.getOutputStream());
    }

    @PostMapping("/restore")
    public ResponseEntity<String> restoreBackup(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("파일이 존재하지 않거나 비어 있습니다.");
        }
        try {
            backupService.restoreBackup(file.getInputStream());
            return ResponseEntity.ok("백업 데이터가 성공적으로 복원되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("백업 복원 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
