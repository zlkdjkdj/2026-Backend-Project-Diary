package com.jaehyun.diary.config;

import com.jaehyun.diary.entity.DiaryEntity;
import com.jaehyun.diary.repository.DiaryRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.springframework.lang.NonNull;

// 소유권 검증 관점
@Aspect
@Component
public class OwnershipAspect {

    @Autowired
    private DiaryRepository diaryRepository;

    // 메소드 호출 전 검증
    @Before("@annotation(CheckOwnership) && args(email, id, ..)")
    public void verifyOwnership(JoinPoint joinPoint, @NonNull String email, @NonNull String id) {
        DiaryEntity diary = diaryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("일기가 존재하지 않습니다. ID: " + id));

        // 작성자 비교
        if (!diary.getAuthorEmail().equals(email)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
    }
}
