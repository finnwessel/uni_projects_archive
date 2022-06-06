package de.hsflensburg.dataservice.domain.model;

import de.hsflensburg.dataservice.domain.model.components.BaseComp;
import lombok.Data;
import nonapi.io.github.classgraph.json.Id;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "templates") @Data
public class Template {

    @Id
    private ObjectId id;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime modifiedAt;

    private Map<String, String> title;

    private ObjectId categoryId;

    private List<BaseComp> components;

    public Template(Map<String, String> title, ObjectId categoryId, List<BaseComp> components) {
        this.title = title;
        this.categoryId = categoryId;
        this.components = components;
    }
}
