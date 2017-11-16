package com.jpanchenko.blogserver.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Objects;

@Data
@Builder
@Document(collection = "posts")
public class Post {
    @Id
    private String id;
    private String title;
    private String body;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date date = new Date();

    @java.beans.ConstructorProperties({"id", "title", "body", "date"})
    public Post(String id, String title, String body, Date date) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.date = Objects.nonNull(date) ? date : new Date();
    }
}
