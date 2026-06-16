# 1단계: 빌드 환경 (Gradle 8.5 & JDK 17)
FROM gradle:8.5-jdk17 AS build
WORKDIR /app

# Gradle 의존성 캐시를 위해 빌드 스크립트 먼저 복사
COPY build.gradle settings.gradle ./
# 소스 코드 복사
COPY src ./src

# 테스트를 제외하고 빌드 (실행 가능한 jar 파일 생성)
RUN gradle build -x test --no-daemon

# 2단계: 실행 환경 (가벼운 JDK 17 Alpine 이미지)
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# 빌드된 jar 파일을 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 파일 업로드를 위한 디렉토리 생성
RUN mkdir -p uploads

# 포트 노출
EXPOSE 8089

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
