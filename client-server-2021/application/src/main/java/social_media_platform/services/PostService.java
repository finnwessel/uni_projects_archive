package social_media_platform.services;

import social_media_platform.dto.post.NewPostDto;
import social_media_platform.dto.post.PostDto;
import social_media_platform.dto.post.UpdatePostDto;
import social_media_platform.entities.AccountEntity;
import social_media_platform.entities.PostEntity;

import javax.ejb.Stateful;
import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Stateful
public class PostService {

    @PersistenceContext(unitName = "default", type = PersistenceContextType.EXTENDED)
    private EntityManager em;
    private long id;

    /**
     * Returns all posts
     *
     * @return all posts
     */
    public List<PostDto> getPosts() {
        TypedQuery<PostEntity> query = em.createQuery("select p from PostEntity p order by p.createdAt desc", PostEntity.class);
        return query.getResultStream()
                .filter(Objects::nonNull)
                .map(PostDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Return all posts of accounts that an account is following
     *
     * @param id account id
     * @return the posts of accounts the account is following
     */
    public List<PostDto> getPostsOfFollowing(long id) {
        TypedQuery<PostEntity> query = em.createQuery("select p from PostEntity p join p.account.follower f where f.id = :id order by p.createdAt desc", PostEntity.class);
        query.setParameter("id", id);
        return query.getResultStream()
                .filter(Objects::nonNull)
                .map(PostDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Returns a post by its id
     *
     * @param id post id
     * @return the post
     */
    public PostDto getPostById(long id) {
        PostEntity postEntity = em.find(PostEntity.class, id);

        if (postEntity == null) {
            return null;
        } else {
            return new PostDto(postEntity);
        }
    }

    /**
     * Returns the posts of an account specified by its id
     *
     * @param id account id
     * @return the posts of the account
     */
    public List<PostDto> getPostFromAccountWithId(long id) {
        this.id = id;
        TypedQuery<PostEntity> query = em.createQuery("select p from PostEntity p where p.account.id = :id order by p.createdAt desc",
                PostEntity.class);
        query.setParameter("id", id);

        return query.getResultStream()
                .filter(Objects::nonNull)
                .map(PostDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Creates a new post for an account
     *
     * @param account_id account id
     * @param newPostDto a new post
     */
    public void createPost(long account_id, NewPostDto newPostDto) {
        PostEntity post = new PostEntity();
        AccountEntity account = em.getReference(AccountEntity.class, account_id);
        post.setAccount(account);
        post.setContent(newPostDto.getContent());
        em.persist(post);
    }

    /**
     * Updates a post
     *
     * @param id post id
     * @param postDto the post
     */
    public void updatePost(long id, UpdatePostDto postDto) {
        PostEntity post = em.getReference(PostEntity.class, id);
        if (post != null) {
            post.setContent(postDto.content);
        }
    }

    /**
     * Deletes a post
     *
     * @param id post id
     * @return the deleted post
     */
    public long deletePost(long id) {
        PostEntity post = em.find(PostEntity.class, id);
        if (post == null) {
            return -1;
        } else {
            long postId = post.getId();
            em.remove(post);
            return postId;
        }
    }
}
