package controllers;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import entities.Comment;
import entities.Post;
import entities.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import services.PostService;
import services.UserService;

@Path("posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostController {
	
	@Inject
    private PostService postService;

    @Inject
    private UserService userService;

    @POST
    @Path("/create")
    public Response createPost(@QueryParam("userId") int userId, Post postInput) {
        User user = userService.getUserById(userId);
        if (user == null)
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        Post post = postService.createPost(user, postInput.getContent(), postInput.getImageUrl());

        Map<String, Object> map = new HashMap<>();
        map.put("content", post.getContent());
        map.put("imageURL", post.getImageUrl());
        return Response.ok(map).build();
    }

    @GET
    @Path("/getPost/{id}")
    public Map<String, Object> getPostById(@PathParam("id") int id) {
        Post post = postService.find(id);
        if (post == null) return null;
        Map<String, Object> map = new HashMap<>();
        map.put("username", post.getUser().getName());
        map.put("content", post.getContent());
        map.put("imageURL", post.getImageUrl());
        return map;
    }

    @GET
    @Path("/feed")
    public List<Map<String, Object>> getFeed(@QueryParam("userId") int userId) {
        User user = userService.getUserById(userId);
        if (user == null) return List.of();

        List<Post> posts = postService.getFeed(user);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Post post : posts) {
            Map<String, Object> map = new HashMap<>();
            map.put("content", post.getContent());
            map.put("imageURL", post.getImageUrl());
            result.add(map);
        }
        return result;
    }

    @PUT
    @Path("/{id}")
    public Response updatePost(@PathParam("id") int id, @QueryParam("userId") int userId, Post updatedData) {
        User user = userService.getUserById(userId);
        try {
            postService.editPost(id, updatedData.getContent(), updatedData.getImageUrl(), user);
            return Response.ok().build();
        } catch (SecurityException se) {
            return Response.status(Response.Status.FORBIDDEN).entity(se.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deletePost(@PathParam("id") int id, @QueryParam("userId") int userId) {
        User user = userService.getUserById(userId);
        try {
            postService.deletePost(id, user);
            return Response.ok().build();
        } catch (SecurityException se) {
            return Response.status(Response.Status.FORBIDDEN).entity(se.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/{postId}/like")
    public Response likePost(@PathParam("postId") int postId, @QueryParam("userId") int userId) {
        User user = userService.getUserById(userId);
        if (user == null)
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        try {
            postService.likePost(user, postId);
            return Response.ok().entity("Post liked").build();
        } catch (IllegalStateException ise) {
            return Response.status(Response.Status.CONFLICT).entity(ise.getMessage()).build();
        }
    }

    @POST
    @Path("/{postId}/comment")
    public Response commentPost(@PathParam("postId") int postId, @QueryParam("userId") int userId, String content) {
        User user = userService.getUserById(userId);
        if (user == null)
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        try {
            postService.addComment(user, postId, content);
            return Response.ok().entity("Commented on the post").build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{postId}/comments")
    public List<Map<String, Object>> getComments(@PathParam("postId") int postId) {
        return postService.getCommentsForPostMapped(postId);
    }
}
