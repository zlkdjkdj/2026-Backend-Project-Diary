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

// 일기 API
@RestController
@RequestMapping("/api/diary")
public class DiaryApiController {

    @Autowired
    private DiaryService diaryService;

    @Autowired
    private FileService fileService;

    // 생성 처리
    @PostMapping
    public DiaryForm createDiary(
            Principal principal,
            @RequestPart("diary") DiaryForm diaryInputData,
            @RequestPart(value = "image", required = false) MultipartFile diaryImageFile) throws IOException {

        // S3 이미지 업로드
        if (diaryImageFile != null && !diaryImageFile.isEmpty()) { // 이미지 파일 존재 여부 확인, FileService를 통해 S3에 업로드하고 업로드된
                                                                   // public URL을 가져옴
            String imageUrl = fileService.saveFile(diaryImageFile); // 이미지 저장 매서드 호출
            diaryInputData.setAttachedPhotoUrl(imageUrl); // 이미지 url 설정
        }

        return diaryService.createDiary(principal.getName(), diaryInputData); // 서비스로 넘겨 db에 최종 저장
    }

    // 목록 조회 처리
    @GetMapping
    public List<DiaryForm> getAllDiaries(Principal principal) {
        return diaryService.getAllDiaries(principal.getName());
    }

    // 검색 처리
    @GetMapping("/search")
    public List<DiaryForm> searchDiaries(Principal principal, @RequestParam("keyword") String searchKeyword) {
        return diaryService.searchDiaries(principal.getName(), searchKeyword);
    }

    // 수정 처리
    @PutMapping("/{id}")
    public DiaryForm updateDiary(
            Principal principal,
            @PathVariable("id") String id,
            @RequestPart("diary") DiaryForm diaryInputData,
            @RequestPart(value = "image", required = false) MultipartFile diaryImageFile) throws IOException {

        // S3 이미지 교체
        if (diaryImageFile != null && !diaryImageFile.isEmpty()) {
            String imageUrl = fileService.saveFile(diaryImageFile);
            diaryInputData.setAttachedPhotoUrl(imageUrl);
        }

        return diaryService.updateDiary(principal.getName(), id, diaryInputData);
    }

    // 삭제 처리
    @DeleteMapping("/{id}") // HTTP DELETE /api/diary/{id} 요청 매핑
    public void deleteDiary(Principal principal, @PathVariable("id") String id) {
        diaryService.deleteDiary(principal.getName(), id); // 서비스로 보내서 db에서 삭제 처리
    }
}
