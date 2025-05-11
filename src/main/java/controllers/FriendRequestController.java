package controllers;

import java.util.List;

import javax.inject.Inject;
import javax.websocket.server.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import entities.FriendRequest;
import entities.User;
import services.FriendRequestService;
import services.UserService;

@Path("/friends")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FriendRequestController {
	
	@Inject private UserService userService;
    @Inject private FriendRequestService friendRequestService;

    // Send a friend request
    @POST
    @Path("/request")
    public Response sendRequest(@QueryParam("senderId") int senderId,
                                @QueryParam("receiverId") int receiverId) {
        User sender = userService.getUserById(senderId);
        User receiver = userService.getUserById(receiverId);
        if (sender == null || receiver == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        try {
            friendRequestService.sendRequest(sender, receiver);
            return Response.ok().build();
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        }
    }

    // Accept a friend request
    @POST
    @Path("/accept/{requestId}")
    public Response accept(@PathParam("requestId") int requestId, @QueryParam("userId") int userId) {
        User receiver = userService.getUserById(userId);
        try {
            friendRequestService.acceptRequest(requestId, receiver);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    // Reject a friend request
    @POST
    @Path("/reject/{requestId}")
    public Response reject(@PathParam("requestId") int requestId, @QueryParam("userId") int userId) {
        User receiver = userService.getUserById(userId);
        try {
            friendRequestService.rejectRequest(requestId, receiver);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    // See all friends for a user
    @GET
    @Path("/{userId}/all")
    public List<User> friends(@PathParam("userId") int userId) {
        User user = userService.getUserById(userId);
        return friendRequestService.getFriends(user);
    }

    // See all pending requests for a user
    @GET
    @Path("/{userId}/pending")
    public List<FriendRequest> pending(@PathParam("userId") int userId) {
        User user = userService.getUserById(userId);
        return friendRequestService.getPendingToAccept(user);
    }
}
