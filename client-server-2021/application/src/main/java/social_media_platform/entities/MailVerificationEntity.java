package social_media_platform.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import social_media_platform.utils.CommonUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "mail_verification")
public class MailVerificationEntity implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified;

    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @PrePersist
    void defaults() {
        this.code = CommonUtils.generateCode();
        this.updatedAt = new Date();
        this.isVerified = false;
        this.createdAt = new Date();
    }

    @PreUpdate
    private void update() {
        this.updatedAt = new Date();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        MailVerificationEntity that = (MailVerificationEntity) obj;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
