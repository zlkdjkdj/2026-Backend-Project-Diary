package com.dongyang.basicProject.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity

public class ArticleEntity {

    @Id
    @GeneratedValue
    private Long id;
    @Column
    private String title;
    @Column
    private String contents;

    public void patch(ArticleEntity updateDate) {
        if (updateDate.title != null) {
            this.title = updateDate.title;
        }
        if (updateDate.contents != null) {
            this.title = updateDate.contents;
        }
    }
}
