package com.dongyang.basicProject.controller;

import com.dongyang.basicProject.dto.ArticleForm;
import com.dongyang.basicProject.entity.ArticleEntity;
import com.dongyang.basicProject.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequestMapping("/articles")
@Controller
public class ArticleController {

    private final ArticleRepository articleRepository;


    @Autowired
    public ArticleController(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }


    // 1. 게시글 목록 조회
    @GetMapping
    public String index(Model model) {
        Iterable<ArticleEntity> result = articleRepository.findAll();
        model.addAttribute("articleList", result);
        return "article/index";
    }

    // 2. 새 글 작성 폼
    @GetMapping("/new")
    public String newArticleForm() {
        return "article/new";
    }

    // 3. 게시글 생성
    @PostMapping("/create")
    public String createArticle(ArticleForm form) {
        ArticleEntity article = form.toEntity();
        ArticleEntity successEntity = articleRepository.save(article);
        return "redirect:/articles/" + successEntity.getId();
    }

    // 4. 게시글 상세 조회
    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        ArticleEntity result = articleRepository.findById(id).orElse(null);
        model.addAttribute("showEntity", result);
        return "article/show";
    }

    // 5. 수정 페이지 보기
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        ArticleEntity articleEntity = articleRepository.findById(id).orElse(null);
        model.addAttribute("article", articleEntity);
        return "article/edit";
    }

    // 6. 수정 데이터 반영
    @PostMapping("/update")
    public String update(ArticleForm form) {
        ArticleEntity saved = articleRepository.save(form.toEntity());
        return "redirect:/articles/" + saved.getId();
    }

    // 7. 삭제 처리
    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes rttr) {
        articleRepository.deleteById(id);
        rttr.addFlashAttribute("msg", "삭제가 완료되었습니다.");
        return "redirect:/articles";
    }
}