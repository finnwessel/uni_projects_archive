package social_media_platform.dto.post;

import lombok.Data;

@Data
public class NewPostDto {
    private String content;

    public  NewPostDto() {}

    public NewPostDto(String content) {
        this.content = content;
    }
}
