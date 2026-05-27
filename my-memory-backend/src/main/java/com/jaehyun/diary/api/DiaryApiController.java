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

@RestController
@RequestMapping("/api/diary")
@CrossOrigin(origins = "*")
public class DiaryApiController {

    @Autowired
    private DiaryService diaryService;
    @Autowired
    private FileService fileService;

    @PostMapping
    public DiaryForm createDiary(
            Principal principal,
            @RequestPart("diary") DiaryForm diaryForm,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        if (image != null && !image.isEmpty()) {
            String imageUrl = fileService.saveFile(image);
            diaryForm.setImageUrl(imageUrl);
        }
        return diaryService.createDiary(principal.getName(), diaryForm);
    }

    @GetMapping
    public List<DiaryForm> getAllDiaries(Principal principal) {
        return diaryService.getAllDiaries(principal.getName());
    }

    @GetMapping("/search")
    public List<DiaryForm> searchDiaries(Principal principal, @RequestParam("keyword") String keyword) {
        return diaryService.searchDiaries(principal.getName(), keyword);
    }

    @PutMapping("/{id}")
    public DiaryForm updateDiary(
            Principal principal,
            @PathVariable("id") String id,
            @RequestPart("diary") DiaryForm diaryForm,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        if (image != null && !image.isEmpty()) {
            String imageUrl = fileService.saveFile(image);
            diaryForm.setImageUrl(imageUrl);
        }
        return diaryService.updateDiary(principal.getName(), id, diaryForm);
    }

    @DeleteMapping("/{id}")
    public void deleteDiary(Principal principal, @PathVariable("id") String id) {
        diaryService.deleteDiary(principal.getName(), id);
    }
}
