package social_media_platform.beans;

import lombok.Data;
import social_media_platform.dto.post.NewPostDto;
import social_media_platform.dto.post.PostDto;
import social_media_platform.services.PostService;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Data
@Named("post")
@RequestScoped
public class PostBean implements Serializable {

    @Inject
    private PostService service;

    private NewPostDto new_post;

    // Return String for OneSelectButton UI component
    private String postFilter;

    @PostConstruct
    private void init() {
        new_post = new NewPostDto();
        postFilter = "all";
    }

    /**
     * Creates a post
     *
     * @param account_id an account id
     */
    public void createPost(Long account_id) {
        service.createPost(account_id, new_post);
        new_post = new NewPostDto();
    }

    /**
     * Deletes a post
     *
     * @param post_id a post id
     */
    public void deletePost(Long post_id) {
        service.deletePost(post_id);
    }

    /**
     * Returns posts depending on a filter (own, following or all)
     *
     * @param account_id an account id
     * @return posts
     */
    // Returns posts depending on filter
    public List<PostDto> getPosts(Long account_id) {
        switch (postFilter) {
            case "own":
                return service.getPostFromAccountWithId(account_id);
            case "following":
                return service.getPostsOfFollowing(account_id);
            default:
                return service.getPosts();
        }
    }
}
