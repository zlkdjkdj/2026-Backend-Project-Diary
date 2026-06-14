package com.jaehyun.diary.config;

import com.jaehyun.diary.entity.DiaryEntity;
import com.jaehyun.diary.repository.DiaryRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// @CheckOwnership 메서드 자원 소유권 검증 AOP 컴포넌트
// 사용자의 이메일이 일치하는지 보안 검증하는 객체
@Aspect // AOP(관점 지향 프로그래밍) 적용 클래스 명시
@Component // 스프링 빈 등록
public class OwnershipAspect {

    @Autowired
    private DiaryRepository diaryRepository;

    // 대상 메서드 실행 전 작성자와 요청자 일치 여부 대조
    @Before("@annotation(CheckOwnership) && args(email, id, ..)")
    public void verifyOwnership(JoinPoint joinPoint, String email, String id) {
        // 대상 일기 ID를 통해 DB에서 해당 일기 엔티티를 조회. 없으면 예외 발생
        DiaryEntity diary = diaryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("일기가 존재하지 않습니다. ID: " + id));

        // 엔티티에 기록된 원작성자의 이메일(getAuthorEmail)과 현재 요청자 이메일(email)을 비교
        if (!diary.getAuthorEmail().equals(email)) {
            // 일치하지 않으면 소유권이 없으므로 권한 부족 예외(403 Forbidden으로 전환됨)를 던져 메서드 실행 원천 차단
            throw new IllegalArgumentException("권한이 없습니다.");
        }
    }
}
