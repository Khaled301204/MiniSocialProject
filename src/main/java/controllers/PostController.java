package controllers;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import entities.Comment;
import entities.Like;
import entities.Post;

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

import services.PostService;

@Path("posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostController {
	
	@Inject
    private PostService postService;
	
	 @POST
	 @Path("/createPost")
	 public Post createPost(Post post) {
		    return postService.createPost(post);
		}
	 
	 @GET
	 @Path("/getbyid/{id}")
	 public Response getPostById(int id) {
		 Post post = postService.getPostById(id);
		 if (post == null) {
		      return Response.status(Response.Status.NOT_FOUND).build();
		    }
		    return Response.ok(post).build();	    
		    }
	 
	 @GET
	 public List<Post> getAllPosts() {
		 return postService.getAllPosts();
	 }
	 
	 @PUT
	 @Path("/{id}")
	 public Post updatePost(@PathParam("id") int id, Post updatedData) {
	    return postService.updatePost(id, updatedData);
	 }
	 
	 @DELETE
	 @Path("/{id}")
	 public String deletePost(@PathParam("id") int id) {
		 boolean deleted = postService.deletePost(id);
	     if (!deleted) {
	    	 return "Post not found";
	     }
	     return "Post deleted successfully";
	 }
	 
	 @POST
	 @Path("/{postId}/like/{userId}")
	 public String likePost(@PathParam("postId") int postId, @PathParam("userId") int userId) {
		 Like like = postService.likePost(postId, userId);
	     if (like == null) {
	    	 return "User already liked this post or not found";
	     }
	     return "Post liked";
	 }
	 
	 @POST
	 @Path("/{postId}/comment/{userId}")
	 public String commentPost(@PathParam("postId") int postId, @PathParam("userId") int userId, String content) {
		 Comment comment = postService.commentOnPost(postId, userId, content);
	     if (comment == null) {
	    	 return "Post or user not found";
	     }
	     return "commented on the post";
	 }
}
