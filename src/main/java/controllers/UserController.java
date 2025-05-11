package controllers;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import entities.User;
import services.UserService;

@Path("users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserController {

    @Inject
    private UserService userService;

    @POST
    @Path("register")
    public Response addUser(User user) {
        if(userService.registerUser(user)!=null) {
        	return Response.ok("User registered successfully.").build();
        }
        else {
        	return Response.status(400).build();
        }
    }

    @GET
    @Path("getUser/{id}")
    public User getUser(@PathParam("id") int id) {
        return userService.getUserById(id);
    }

    @GET
    @Path("getAllUsers")
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    @PUT
    @Path("{id}/update")
    public User updateUser(@PathParam("id") int id, User userData) {
        return userService.updateUser(id, userData);
    }
    
    @POST
    @Path("/login")
    public Response login(User user) {
        User loggedInUser = userService.login(user.getEmail(), user.getPassword());
        return Response.ok(loggedInUser).build();
    }

}

