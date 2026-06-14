package com.jaehyun.diary.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 작성자 권한 검증
//ownershipAspect(AOP)가 해당 메서드를 가로채어 소유권 검증 로직을 실행하도록 유도
@Target(ElementType.METHOD) // 이 어노테이션은 오직 클래스의 메서드에만 선언할 수 있음을 제한
@Retention(RetentionPolicy.RUNTIME) // 프로그램이 실행(런타임)되는 동안 해당 어노테이션 정보를 메모리에 유지하여 AOP에서 읽을 수 있게 함
public @interface CheckOwnership {
    // 추가적인 속성은 없으며 단지 런타임 훅(Hook) 용도로 쓰임
}
