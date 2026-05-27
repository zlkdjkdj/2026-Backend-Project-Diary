package com.dongyang.basicProject.api;

import java.util.List;
import com.dongyang.basicProject.entity.ArticleEntity;
import com.dongyang.basicProject.service.ArticleService;
import com.dongyang.basicProject.dto.ArticleForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class FirstArticleController {
    @Autowired
    ArticleService articleService;

    @PatchMapping("/api/articles/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody ArticleForm form) {
        articleService.update(id, form);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/apiarticles/{id}")
    public ResponseEntity delete(@PathVariable Long id){
        articleService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).build();

    }
    @PostMapping("/api/transaction")
    public ResponseEntity transactionTest(@RequestBody List<ArticleForm> dtos){
        articleService.transactionTest(dtos);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

    }




    @GetMapping("/api/articles/{id}")
    public ArticleEntity show(@PathVariable Long id){
        return articleService.show(id);
    }

    @GetMapping("/api/articles")
    public List<ArticleEntity> index() {
        return articleService.index();
    }

    @PostMapping("/api/articles")
    public ResponseEntity<ArticleEntity> create(@RequestBody ArticleForm form){
        ArticleEntity created = articleService.create(form);
        return (created != null) ?
                ResponseEntity.status(HttpStatus.OK).body(created) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}