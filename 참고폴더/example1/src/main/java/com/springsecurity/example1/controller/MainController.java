package com.springsecurity.example1.controller;

import com.springsecurity.example1.config.PrincipalDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    // 메인 페이지
    @GetMapping("/")
    public String index(Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {

        // 로그인 되었다면 principalDetails가 null이 아님
        if (principalDetails != null) {
            // 모델에 "userName"이라는 키값으로 닉네임을 담음
            // 머스타치는 이 값이 있냐 없냐로 화면을 다르게 보여줌
            model.addAttribute("userName", principalDetails.getNickname());
            //관리자 여부
            model.addAttribute("isAdmin", principalDetails.isAdmin());
        }
        return "index";
    }

    @GetMapping("/intro")
    public String intro(Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails != null) {
            model.addAttribute("userName", principalDetails.getNickname());
            //관리자 여부
            model.addAttribute("isAdmin", principalDetails.isAdmin());
        }
        return "intro";
    }

    @GetMapping("/order")
    public String order(Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails != null) {
            model.addAttribute("userName", principalDetails.getNickname());
            //관리자 여부
            model.addAttribute("isAdmin", principalDetails.isAdmin());
        }
        return "order";
    }

    @GetMapping("/admin")
    public String admin(Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails != null) {
            model.addAttribute("userName", principalDetails.getNickname());
            //관리자 여부
            model.addAttribute("isAdmin", principalDetails.isAdmin());
        }
        return "admin";
    }

}