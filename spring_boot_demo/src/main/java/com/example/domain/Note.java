package com.example.domain;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import java.util.Date;

@Data
@Entity
@NamedQuery(name="Note.withTitle",
query = "select n from Note n where n.title=?1")
public class Note {
    @Id
    @GeneratedValue
    private Long id;

    private String title;

    private String content;

    private Date createdAt;

    private Date updateAt;

    public Note(String title, String content, Date createdAt, Date updateAt) {
        super();
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updateAt = updateAt;
    }
}