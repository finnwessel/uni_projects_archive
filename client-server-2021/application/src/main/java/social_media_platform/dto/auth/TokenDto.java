package social_media_platform.dto.auth;

import lombok.Data;

import java.io.Serializable;

@Data
public class TokenDto implements Serializable {
    private String token;

    public TokenDto(String token) {
        this.token = token;
    }
}
