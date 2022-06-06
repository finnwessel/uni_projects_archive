package social_media_platform.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import social_media_platform.entities.AccountEntity;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AccountDto {

    private long id;
    private String username;
    private String email;
    private String firstname;
    private String lastname;
    private boolean publicProfile;

    public AccountDto(AccountEntity account) {
        this.id = account.getId();
        this.username = account.getUsername();
        this.email = account.getEmail();
        this.firstname = account.getFirstname();
        this.lastname = account.getLastname();
        this.publicProfile = account.getPublicProfile();
    }
}
