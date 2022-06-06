package social_media_platform.dto.auth;

import lombok.Data;

import java.io.Serializable;

@Data
public class CredentialsDto implements Serializable {
    private String username;
    private String password;
}
