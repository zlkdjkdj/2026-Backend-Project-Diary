package com.jaehyun.diary.api;

import com.jaehyun.diary.service.BackupService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

// 데이터 백업 및 복원 REST API 컨트롤러
// 전체 시스템 백업 ZIP 파일을 다운로드 제공 + 복원기능 엔드포인트 객체
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class BackupApiController {

    @Autowired
    private BackupService backupService;

    // 전체 시스템 데이터 ZIP 파일 생성 및 클라이언트 제공
    // response = 백업 생성중  ZIP 스트림을 클라이언트에게 다운로드시키기 위한 HTTP
    @GetMapping("/backup")
    public void downloadBackup(HttpServletResponse response) throws IOException {
        // 응답 콘텐츠 타입을 압축 파일(ZIP) 형식으로 지정, 브라우저 인식
        response.setContentType("application/zip");
        // HTTP 헤더에 설정 및 다운로드될 파일명 지정
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"diary_backup.zip\"");

        // 출력 스트림(OutputStream)에 백업 데이터를 기록(ZIP 포맷)
        backupService.createBackup(response.getOutputStream());
    }

    // 클라이언트 업로드 ZIP 파일 기반 시스템 데이터 복원
    @PostMapping("/restore")
    public ResponseEntity<String> restoreBackup(@RequestParam("file") MultipartFile backupZipFile) {
        // 에러 반환
        if (backupZipFile.isEmpty()) {
            return ResponseEntity.badRequest().body("파일이 존재하지 않거나 비어 있습니다.");
        }

        try {
            // 파일의 입력 스트림(InputStream)을 읽어와서 backupService의 복원 로직에 전달
            backupService.restoreBackup(backupZipFile.getInputStream());
            // 복원 성공
            return ResponseEntity.ok("백업 데이터가 성공적으로 복원되었습니다.");
        } catch (Exception e) {
            // 복원 실패
            return ResponseEntity.internalServerError().body("백업 복원 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
