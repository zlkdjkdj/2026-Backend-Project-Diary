package com.jaehyun.diary.controller;

import com.jaehyun.diary.domain.Diary;
import com.jaehyun.diary.repository.DiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diary")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryRepository diaryRepository;

    @PostMapping
    public Diary createDiary(@RequestBody Diary diary) {
        return diaryRepository.save(diary);
    }

    @GetMapping
    public List<Diary> getAllDiaries() {
        return diaryRepository.findAll();
    }

    @GetMapping("/search")
    public List<Diary> searchDiaries(@RequestParam String userId, @RequestParam String keyword) {
        return diaryRepository.findByUserIdAndContentContaining(userId, keyword);
    }

    @DeleteMapping("/{id}")
    public void deleteDiary(@PathVariable String id) {
        diaryRepository.deleteById(id);
    }
}
