package controllers;

import java.util.List;

import javax.inject.Inject;
import javax.websocket.server.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import entities.User;
import services.ConnectionService;

@Path("/connections")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ConnectionController {

    @Inject
    private ConnectionService connectionService;

    @POST
    @Path("/request/{senderId}/{receiverId}")
    public Response sendRequest(@PathParam("senderId") int senderId, @PathParam("receiverId") int receiverId) {
        connectionService.sendFriendRequest(senderId, receiverId);
        return Response.ok().build();
    }

    @PUT
    @Path("/respond/{requestId}/{response}")
    public Response respondToRequest(@PathParam("requestId") int requestId, @PathParam("response") String response) {
        connectionService.respondToRequest(requestId, response);
        return Response.ok().build();
    }

    @GET
    @Path("/friends/{userId}")
    public Response getFriends(@PathParam("userId") int userId) {
        List<User> friends = connectionService.getFriends(userId);
        return Response.ok(friends).build();
    }
    
}

