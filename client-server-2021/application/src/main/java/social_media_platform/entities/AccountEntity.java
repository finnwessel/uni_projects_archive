package social_media_platform.entities;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "account")
public class AccountEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "firstname", nullable = false)
    private String firstname;

    @Column(name = "lastname", nullable = false)
    private String lastname;

    @Column(name = "public_profile", nullable = false)
    private Boolean publicProfile;

    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @PrePersist
    private void defaults() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.publicProfile = true;
    }

    @PreUpdate
    private void update() {
        this.updatedAt = new Date();
    }

    @OneToMany(
            mappedBy = "account",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<PostEntity> posts;

    @ManyToMany
    @JoinTable(
            name = "account_follows",
            joinColumns = @JoinColumn(name = "account"),
            inverseJoinColumns = @JoinColumn(name = "follows"))
    private List<AccountEntity> follows;

    @ManyToMany
    @JoinTable(
            name = "account_follows",
            joinColumns = @JoinColumn(name = "follows"),
            inverseJoinColumns = @JoinColumn(name = "account"))
    private List<AccountEntity> follower;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AccountEntity that = (AccountEntity) obj;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
