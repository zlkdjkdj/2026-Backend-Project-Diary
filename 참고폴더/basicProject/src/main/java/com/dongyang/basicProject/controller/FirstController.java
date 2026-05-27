package com.dongyang.basicProject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FirstController {
    @GetMapping("/quote")
    public String randomQuote(Model model){
        String[] varQuotes={
                "삶이 있는 한 희망은 있다.(키케로)",
                "언제나 현재에 집중할 수 있다면 행복할 것이다. (파울로 코엘료)",
                "내 비장의 무기는 아직 손안에 있다. (나폴레옹)",
                "행운이란 준비와 기회를 만났을 때 나타난다. (세네카)",
                "안녕하세요"
        };
        int randInt=(int)(Math.random()* varQuotes.length);
        model.addAttribute("ranQuote", varQuotes[randInt]);
        return "quote";
    }


    @GetMapping("/")
    public String index(){
        System.out.println("index 메서드로 매핑");
        return "index";
    }

    @GetMapping("/hi")
    public String hello(Model model){
        model.addAttribute("name", "길재현");
        return "hello";
    }

    @GetMapping("/search")
    public String search(@RequestParam("mainSearch") String keyword){
        System.out.println("검색어 : " + keyword);
        return "";
    }



}
