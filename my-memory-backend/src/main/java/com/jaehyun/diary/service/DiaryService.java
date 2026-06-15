package com.jaehyun.diary.service;

import com.jaehyun.diary.config.CheckOwnership;
import com.jaehyun.diary.dto.DiaryForm;
import com.jaehyun.diary.entity.DiaryEntity;
import com.jaehyun.diary.repository.DiaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;

// 일기 curd 로직 수행
@Service
public class DiaryService {
    @Autowired
    private DiaryRepository diaryRepository;

    // 신규 일기 데이터베이스 생성 
    public DiaryForm createDiary(String email, DiaryForm diaryInputData) {
        // 작성된 날짜 정보
        LocalDate today = diaryInputData.getWrittenDate() != null ? diaryInputData.getWrittenDate() : LocalDate.now();
        // 해당 사용자가 지정된 날짜(today)에 이미 작성한 일기의 총 개수
        int todayCount = diaryRepository.countByAuthorEmailAndWrittenDate(email, today);

        // 하루 일기 갯수 제한
        if (todayCount >= 3) {
            throw new RuntimeException("하루에 작성할 수 있는 일기는 최대 3개입니다.");
        }

        diaryInputData.setAuthorEmail(email);
        // 최종 엔티티 객체
        DiaryEntity savedDiary = diaryRepository.save(diaryInputData.toEntity());
        // 저장된 엔티티(savedDiary)를 다시 DTO(DiaryForm) 형식으로 변환하여 컨트롤러로 반환
        return DiaryForm.fromEntity(savedDiary);
    }

    // 작성자 소유 전체 일기 데이터 조회
    public List<DiaryForm> getAllDiaries(String email) {
        // 이메일에 해당하는 엔티티 리스트를 조회
        // stream()을 사용하여 리스트의 요소들을 하나씩 순회할 수 있는 스트림 객체로 변환
        return diaryRepository.findByAuthorEmail(email).stream()
                // 트림을 순회하며 각 DiaryEntity 객체를 DiaryForm(DTO) 객체로 매핑
                .map(DiaryForm::fromEntity)
                // 변환된 DTO 객체들을 다시 List 자료구조로 수집하여 반환
                .collect(Collectors.toList());
    }

    // 특정 키워드 포함 일기 검색
    public List<DiaryForm> searchDiaries(String email, String searchKeyword) {
        // DB에서 내용(DiaryContent)에 특정 키워드가 포함된 엔티티들을 찾아 스트림으로 변환 후 DTO매핑 + 리스트 반환
        return diaryRepository.findByAuthorEmailAndDiaryContentContaining(email, searchKeyword).stream()
                .map(DiaryForm::fromEntity)
                .collect(Collectors.toList());
    }

    // 일기 수정
    @CheckOwnership
    public DiaryForm updateDiary(String email, String id, DiaryForm diaryInputData) {
        // 현재 사용자 이메일러 덮어 씌움
        diaryInputData.setAuthorEmail(email);
        // 수정할 내용을 담은 임시 엔티티 객체 생성
        DiaryEntity updatedDiary = diaryInputData.toEntity();
        // 타겟 식별자(id)를 명시, JPA가 update로 안삭허게 함
        updatedDiary.setDiaryId(id);
        // DB에 갱신 처리 후 반환된 엔티티를 DTO로 매핑하여 반환
        return DiaryForm.fromEntity(diaryRepository.save(updatedDiary));
    }

    // 대상 일기 데이터 삭제

    @CheckOwnership
    public void deleteDiary(String email, String id) {
        // diaryRepository를 호출하여 주어진 ID에 해당하는 일기 레코드를 DB에서 영구 삭제
        diaryRepository.deleteById(id);
    }
}
