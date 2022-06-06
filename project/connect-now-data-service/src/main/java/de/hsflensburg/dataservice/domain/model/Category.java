package de.hsflensburg.dataservice.domain.model;

import lombok.Data;
import nonapi.io.github.classgraph.json.Id;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "categories") @Data
public class Category implements Serializable {

    @Id
    private ObjectId id;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime modifiedAt;

    private CategoryType type;

    private Map<String, String> name;

    private String color;

    public Category(CategoryType type, Map<String, String> name, String color) {
        this.type = type;
        this.name = name;
        this.color = color;
    }
}
