package com.dongyang.basicProject.service;

import com.dongyang.basicProject.dto.ArticleForm;
import com.dongyang.basicProject.entity.ArticleEntity;
import com.dongyang.basicProject.repository.ArticleRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ArticleService {
    @Autowired
    private ArticleRepository articleRepository;

    public void update(Long id, ArticleForm form){
        ArticleEntity updateEntity = form.toEntity();
        ArticleEntity target = articleRepository.findById(id).orElse(null);
        target.patch(updateEntity);
        articleRepository.save(form.toEntity());
    }

    @Transactional
    public void transactionTest(List<ArticleForm> dtos){

        List<ArticleEntity> list = new ArrayList<ArticleEntity>();

        for(int i=0; i < dtos.size() ; i++){
            ArticleForm form = dtos.get(i);
            ArticleEntity entity = form.toEntity();
            list.add(entity);
        }
        articleRepository.saveAll(list);

        try{
            articleRepository.findById(-1L).orElseThrow();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void delete(Long id){
        articleRepository.deleteById(id);

    }

    public ArticleEntity show(Long id) {
        return articleRepository.findById(id).orElse(null);
    }

    public List<ArticleEntity> index() {
        return (List<ArticleEntity>) articleRepository.findAll();
    }

    public ArticleEntity create(ArticleForm form) {
        ArticleEntity article = form.toEntity();

        if (article.getId() != null) {
            return null;
        }

        return articleRepository.save(article);
    }
}