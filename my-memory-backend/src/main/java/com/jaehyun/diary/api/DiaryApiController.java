package com.jaehyun.diary.api;

import com.jaehyun.diary.dto.DiaryForm;
import com.jaehyun.diary.service.DiaryService;
import com.jaehyun.diary.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

// 일기 도메인 핵심 CRUD 연산 REST API 컨트롤러
// 일기 관련 HTTP 요청(CRUD)을 받아 서비스(DiaryService, FileService)로 분기
@RestController
@RequestMapping("/api/diary")
@CrossOrigin(origins = "*")
public class DiaryApiController {

    // diaryService
    @Autowired
    private DiaryService diaryService;

    // fileService
    @Autowired
    private FileService fileService;

    // creatediary
    @PostMapping
    public DiaryForm createDiary(
            Principal principal, // 현재 로그인 한 사용자 정보 객체
            @RequestPart("diary") DiaryForm diaryInputData,
            @RequestPart(value = "image", required = false) MultipartFile diaryImageFile) throws IOException {

        // 이미지 파일 검사
        if (diaryImageFile != null && !diaryImageFile.isEmpty()) {
            // 접근 가능한 URL 경로(imageUrl)를 반환
            String imageUrl = fileService.saveFile(diaryImageFile);
            // URL을 DB 저장을 위해 DTO에 세팅
            diaryInputData.setAttachedPhotoUrl(imageUrl);
        }

        // 인증된 사용자 이메일 과  DTO를 DiaryService에 넘겨 DB에 최종 저장
        return diaryService.createDiary(principal.getName(), diaryInputData);
    }

    // 인증 사용자 작성 전체 일기 목록 조회
    @GetMapping
    public List<DiaryForm> getAllDiaries(Principal principal) {
        // 이메일 넘겨 전체 일기 목록 조회
        return diaryService.getAllDiaries(principal.getName());
    }

    // 특정 키워드 포함 사용자 일기 검색
    @GetMapping("/search")
    public List<DiaryForm> searchDiaries(Principal principal, @RequestParam("keyword") String searchKeyword) {
        // diaryService에 식별자와 검색어를 넘겨 조건에 맞는 데이터만 반환받음
        return diaryService.searchDiaries(principal.getName(), searchKeyword);
    }

    // 기존 일기 데이터 수정 (새로운 이미지 업로드 포함)
    @PutMapping("/{id}") // 일기의 고유 ID
    public DiaryForm updateDiary(
            Principal principal,
            @PathVariable("id") String id,
            @RequestPart("diary") DiaryForm diaryInputData, // 수정될 내용이 담긴 폼 데이터 DTO 객체
            @RequestPart(value = "image", required = false) MultipartFile diaryImageFile) throws IOException { // diaryImageFile

        // 기존 이미지 경로 덮어쓰기
        if (diaryImageFile != null && !diaryImageFile.isEmpty()) {
            String imageUrl = fileService.saveFile(diaryImageFile);
            diaryInputData.setAttachedPhotoUrl(imageUrl);
        }

        // 인증된 사용자 이메일, 타겟 일기 ID, 수정된 DTO를 서비스로 넘겨 수정 작업 지시
        return diaryService.updateDiary(principal.getName(), id, diaryInputData);
    }

    // 일기 삭제
    @DeleteMapping("/{id}")
    public void deleteDiary(Principal principal, @PathVariable("id") String id) {
        // 서비스 단에 사용자 이메일과 삭제 대상 ID를 넘겨 레코드 영구 삭제 지시
        diaryService.deleteDiary(principal.getName(), id);
    }
}
