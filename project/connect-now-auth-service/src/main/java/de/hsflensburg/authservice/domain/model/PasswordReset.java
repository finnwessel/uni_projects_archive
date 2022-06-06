package de.hsflensburg.authservice.domain.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "passwordResetRequests") @Data
public class PasswordReset {
    @Id
    private ObjectId id;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime modifiedAt;

    @Indexed
    private String subject;
    @Indexed(unique = true)
    private String resetRequestId;

    private LocalDateTime expires;

    public PasswordReset(String subject, String resetRequestId, LocalDateTime expires) {
        this.subject = subject;
        this.resetRequestId = resetRequestId;
        this.expires = expires;
    }

}
