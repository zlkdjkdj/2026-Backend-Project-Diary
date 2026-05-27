package com.springsecurity.example1.controller;

import com.springsecurity.example1.dto.JoinDTO;
import com.springsecurity.example1.service.JoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
@Controller
@RequiredArgsConstructor
public class MemberController {
    private final JoinService joinService;

    // 회원가입 페이지 띄우기
    @GetMapping("/join")
    public String joinForm() {
        return "join"; // templates/join.mustache
    }

    // 회원가입 진행 (POST)
    @PostMapping("/joinProc")
    public String joinProcess(JoinDTO joinDTO) {

        System.out.println("회원가입 요청: " + joinDTO.getLoginId());

        joinService.joinProcess(joinDTO);

        // 가입 완료 후 로그인 페이지로 이동
        return "redirect:/login";
    }
    // 로그인 페이지 매핑
    @GetMapping("/login")
    public String loginForm() {
        return "login"; // templates/login.mustache
    }

}
