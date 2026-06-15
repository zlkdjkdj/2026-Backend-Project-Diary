package com.jaehyun.diary.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 작성자 권한 검증
//ownershipAspect(AOP)가 해당 메서드를 가로채어 소유권 검증 로직을 실행하도록 유도
@Target(ElementType.METHOD) 
@Retention(RetentionPolicy.RUNTIME) 
public @interface CheckOwnership {
   
}
