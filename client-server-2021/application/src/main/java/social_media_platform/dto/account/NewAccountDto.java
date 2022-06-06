package social_media_platform.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class NewAccountDto {
    private String username;
    private String email;
    private String firstname;
    private String lastname;
    private String password;
}
