package com.jaehyun.diary.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 소유권 검증
@Target(ElementType.METHOD) 
@Retention(RetentionPolicy.RUNTIME) 
public @interface CheckOwnership {
   
}
