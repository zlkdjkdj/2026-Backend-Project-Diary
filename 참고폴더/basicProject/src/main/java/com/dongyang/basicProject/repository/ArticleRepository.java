package com.dongyang.basicProject.repository;

import com.dongyang.basicProject.entity.ArticleEntity;
import org.springframework.data.repository.CrudRepository;

public interface ArticleRepository extends CrudRepository<ArticleEntity, Long > {

}
