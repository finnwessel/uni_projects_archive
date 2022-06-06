package de.hsflensburg.authservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tokens") @Data
public class Token implements Serializable {

    @Id
    private ObjectId id;

    private String subject;
    private String tokenId;
    private Date expires;

    public Token(String subject, String tokenId, Date expires) {
        this.subject = subject;
        this.tokenId = tokenId;
        this.expires = expires;
    }
}
