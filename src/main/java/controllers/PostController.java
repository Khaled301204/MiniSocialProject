package controllers;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import entities.Comment;
import entities.Post;
import entities.User;

import java.util.List;

import javax.inject.Inject;
import javax.websocket.server.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
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
        return Response.ok(post).build();
    }

    @GET
    @Path("/{id}")
    public Response getPostById(@PathParam("id") int id) {
        Post post = postService.find(id);
        if (post == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(post).build();
    }

    @GET
    @Path("/feed")
    public List<Post> getFeed(@QueryParam("userId") int userId) {
        User user = userService.getUserById(userId);
        if (user == null) return List.of();
        return postService.getFeed(user);
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
    public Response commentPost(@PathParam("postId") int postId, @QueryParam("userId") int userId, @QueryParam("content") String content) {
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
    public List<Comment> getComments(@PathParam("postId") int postId) {
        return postService.getCommentsForPost(postId);
    }
}
