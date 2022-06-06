package social_media_platform.API;

import social_media_platform.dto.post.NewPostDto;
import social_media_platform.dto.post.PostDto;
import social_media_platform.dto.post.UpdatePostDto;
import social_media_platform.filter.JwtFilter;
import social_media_platform.filter.JwtVerification;
import social_media_platform.filter.Role;
import social_media_platform.services.PostService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

@Stateless
@Path("/post")
public class Post {

    @Inject
    private PostService postService;

    /**
     * Returns the posts of the current users followers
     *
     * @param sC a security context
     * @return the posts of the users followers
     */
    @GET
    @Path("following")
    @JwtVerification({Role.User, Role.Admin})
    @Produces("application/json")
    public List<PostDto> getPostsOfFollower(@Context SecurityContext sC) {
        JwtFilter.User user = (JwtFilter.User) sC.getUserPrincipal();
        return postService.getPostsOfFollowing(user.getId());
    }

    /**
     * Returns all posts
     *
     * @return all posts
     */
    @GET
    @Produces("application/json")
    public List<PostDto> getPosts() {
        return postService.getPosts();
    }

    /**
     * Returns the post with a given id
     *
     * @param id a post id
     * @return the corresponding post
     */
    @GET
    @JwtVerification
    @Path("{id}")
    @Produces("application/json")
    public PostDto getPostById(@PathParam("id") int id) {
        return postService.getPostById(id);
    }

    /**
     * creates a new post
     *
     * @param sC a security context
     * @param newPostDto the new post
     */
    @POST
    @JwtVerification
    @Produces("application/json")
    public void createPost(@Context SecurityContext sC, NewPostDto newPostDto) {
        JwtFilter.User user = (JwtFilter.User) sC.getUserPrincipal();
        postService.createPost(user.getId(), newPostDto);
    }

    /**
     * updates a post
     *
     * @param id a post id
     * @param postDto the post to update
     */
    @PUT
    @JwtVerification({Role.Admin})
    @Path("{id}")
    @Produces("application/json")
    public void updatePost(@PathParam("id") int id, UpdatePostDto postDto) {
        postService.updatePost(id, postDto);
    }

    /**
     * deletes a post
     *
     * @param id a post id
     * @return the deleted post
     */
    @DELETE
    @JwtVerification({Role.Admin})
    @Path("{id}")
    @Produces("application/json")
    public long deletePost(@PathParam("id") int id) {
        return postService.deletePost(id);
    }
}
