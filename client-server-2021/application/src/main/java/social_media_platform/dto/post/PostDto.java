package social_media_platform.dto.post;

import lombok.Data;
import social_media_platform.dto.account.AccountDto;
import social_media_platform.entities.PostEntity;

import java.util.Date;

@Data
public class PostDto {
    private long id;
    private String content;
    private AccountDto account;
    private Date createdAt;

    public PostDto(PostEntity postEntity) {
        this.id = postEntity.getId();
        this.content = postEntity.getContent();
        this.account = new AccountDto(postEntity.getAccount());
        this.createdAt = postEntity.getCreatedAt();
    }
}
