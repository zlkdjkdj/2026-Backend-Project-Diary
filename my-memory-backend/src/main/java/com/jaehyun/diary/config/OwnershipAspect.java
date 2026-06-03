package com.jaehyun.diary.config;

import com.jaehyun.diary.entity.DiaryEntity;
import com.jaehyun.diary.repository.DiaryRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class OwnershipAspect {

    @Autowired
    private DiaryRepository diaryRepository;

    @Before("@annotation(com.jaehyun.diary.config.CheckOwnership) && args(email, id, ..)")
    public void verifyOwnership(JoinPoint joinPoint, String email, String id) {
        DiaryEntity diary = diaryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("일기가 존재하지 않습니다. ID: " + id));
        if (!diary.getUserId().equals(email)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
    }
}
