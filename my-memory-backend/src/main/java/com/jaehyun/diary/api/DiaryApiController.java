package com.jaehyun.diary.api;

import com.jaehyun.diary.dto.DiaryDto;
import com.jaehyun.diary.service.DiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diary")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DiaryApiController {

    private final DiaryService diaryService;

    @PostMapping
    public DiaryDto createDiary(@RequestBody DiaryDto diaryDto) {
        return diaryService.createDiary(diaryDto);
    }

    @GetMapping
    public List<DiaryDto> getAllDiaries() {
        return diaryService.getAllDiaries();
    }

    @GetMapping("/search")
    public List<DiaryDto> searchDiaries(@RequestParam String userId, @RequestParam String keyword) {
        return diaryService.searchDiaries(userId, keyword);
    }

    @PutMapping("/{id}")
    public DiaryDto updateDiary(@PathVariable String id, @RequestBody DiaryDto diaryDto) {
        return diaryService.updateDiary(id, diaryDto);
    }

    @DeleteMapping("/{id}")
    public void deleteDiary(@PathVariable String id) {
        diaryService.deleteDiary(id);
    }
}
