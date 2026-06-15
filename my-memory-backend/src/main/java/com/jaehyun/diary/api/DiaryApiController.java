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

// 일기 API 컨트롤러
@RestController
@RequestMapping("/api/diary")
@CrossOrigin(origins = "*")
public class DiaryApiController {

    @Autowired
    private DiaryService diaryService;

    @Autowired
    private FileService fileService;

    // 일기 생성
    @PostMapping
    public DiaryForm createDiary(
            Principal principal,
            @RequestPart("diary") DiaryForm diaryInputData,
            @RequestPart(value = "image", required = false) MultipartFile diaryImageFile) throws IOException {

        // 첨부 이미지가 있을 경우 S3에 저장 후 URL을 DTO에 매핑
        if (diaryImageFile != null && !diaryImageFile.isEmpty()) {
            String imageUrl = fileService.saveFile(diaryImageFile);
            diaryInputData.setAttachedPhotoUrl(imageUrl);
        }

        return diaryService.createDiary(principal.getName(), diaryInputData);
    }

    // 일기 목록 조회
    @GetMapping
    public List<DiaryForm> getAllDiaries(Principal principal) {
        return diaryService.getAllDiaries(principal.getName());
    }

    // 일기 검색
    @GetMapping("/search")
    public List<DiaryForm> searchDiaries(Principal principal, @RequestParam("keyword") String searchKeyword) {
        return diaryService.searchDiaries(principal.getName(), searchKeyword);
    }

    // 일기 수정
    @PutMapping("/{id}")
    public DiaryForm updateDiary(
            Principal principal,
            @PathVariable("id") String id,
            @RequestPart("diary") DiaryForm diaryInputData,
            @RequestPart(value = "image", required = false) MultipartFile diaryImageFile) throws IOException {

        // 이미지 변경 시 새로운 이미지를 S3에 저장 후 URL 교체
        if (diaryImageFile != null && !diaryImageFile.isEmpty()) {
            String imageUrl = fileService.saveFile(diaryImageFile);
            diaryInputData.setAttachedPhotoUrl(imageUrl);
        }

        return diaryService.updateDiary(principal.getName(), id, diaryInputData);
    }

    // 일기 삭제
    @DeleteMapping("/{id}")
    public void deleteDiary(Principal principal, @PathVariable("id") String id) {
        diaryService.deleteDiary(principal.getName(), id);
    }
}
