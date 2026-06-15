package com.jaehyun.diary.config;

import com.jaehyun.diary.entity.DiaryEntity;
import com.jaehyun.diary.repository.DiaryRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.springframework.lang.NonNull;

// 소유권 검증 Aspect
@Aspect
@Component
public class OwnershipAspect {

    @Autowired
    private DiaryRepository diaryRepository;

    // @CheckOwnership 어노테이션이 달린 메소드 호출 전 파라미터(email, id)를 가로채어 검증
    @Before("@annotation(CheckOwnership) && args(email, id, ..)")
    public void verifyOwnership(JoinPoint joinPoint, @NonNull String email, @NonNull String id) {
        DiaryEntity diary = diaryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("일기가 존재하지 않습니다. ID: " + id));

        // DB에 저장된 일기 원작성자 이메일과 현재 요청자 이메일 비교
        if (!diary.getAuthorEmail().equals(email)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
    }
}
