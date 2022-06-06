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

@Document(collection = "requests") @Data
public class Request implements Serializable {

    @Id
    private ObjectId id;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime modifiedAt;

    private String owner;

    private String title;

    private ObjectId templateId;

    private ObjectId categoryId;

    private List<String> participants;

    private List<Share> shares;

    private List<BaseComp> components;

    boolean active;

    public Request(String owner, String title, ObjectId templateId, ObjectId categoryId,
                   List<String> participants, List<Share> shares, List<BaseComp> components, boolean active) {
        this.owner = owner;
        this.title = title;
        this.templateId = templateId;
        this.categoryId = categoryId;
        this.participants = participants;
        this.shares = shares;
        this.components = components;
        this.active = active;
    }
}
