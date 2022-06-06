package de.hsflensburg.dataservice.domain.model;

import de.hsflensburg.dataservice.domain.model.components.BaseComp;
import lombok.Data;
import nonapi.io.github.classgraph.json.Id;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "offers") @Data
public class Offer implements Serializable {

    @Id
    private ObjectId id;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime modifiedAt;

    private String owner;

    private String title;

    private ObjectId categoryId;

    private boolean visible;

    private List<BaseComp> components;

    public Offer(String owner, String title, ObjectId categoryId, boolean visible, List<BaseComp> components) {
        this.owner = owner;
        this.title = title;
        this.categoryId = categoryId;
        this.visible = visible;
        this.components = components;
    }
}
