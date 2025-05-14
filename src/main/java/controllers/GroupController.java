package controllers;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import entities.UserGroup;
import entities.GroupPost;
import entities.User;
import services.GroupService;
import services.UserService;

@Path("/groups")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GroupController {

	@Inject
    private GroupService groupService;

    @Inject
    private UserService userService;

    @POST
    @Path("/create")
    public Response createGroup(@QueryParam("creatorId") int creatorId, UserGroup groupInput) {
        User creator = userService.getUserById(creatorId);
        if (creator == null) return Response.status(Response.Status.NOT_FOUND).entity("Creator not found").build();
        Map<String, Object> group = groupService.createGroup(creator, groupInput.getName(), groupInput.getDescription(), groupInput.isOpen());
        return Response.ok(group).build();
    }

    @POST
    @Path("/{groupId}/join")
    public Response joinGroup(@PathParam("groupId") int groupId, @QueryParam("userId") int userId) {
        User user = userService.getUserById(userId);
        if (user == null) return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        try {
            groupService.requestToJoinGroup(user, groupId);
            UserGroup group = groupService.getGroupById(groupId);
            if(!group.isOpen()) {
            	return Response.ok("Request pending").build();
            }
            else {
            	return Response.ok("Joined the group successfully").build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/{groupId}/leave")
    public Response leaveGroup(@PathParam("groupId") int groupId, @QueryParam("userId") int userId) {
        try {
            groupService.leaveGroup(groupId, userId);
            return Response.ok("left the group").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
    
    @POST
    @Path("/{groupId}/remove")
    public Response removeMember(@PathParam("groupId") int groupId, @QueryParam("adminId") int adminId, @QueryParam("memberId") int memberId) {
        try {
            User admin = userService.getUserById(adminId);
            groupService.removeUserFromGroup(groupId, memberId, admin);
            return Response.ok("member removed").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/{groupId}/approve")
    public Response approveMembership(@PathParam("groupId") int groupId, @QueryParam("adminId") int adminId, @QueryParam("membershipId") int membershipId) {
        User admin = userService.getUserById(adminId);
        if (admin == null) return Response.status(Response.Status.NOT_FOUND).entity("Admin not found").build();
        try {
            groupService.approveMembership(membershipId, admin);
            return Response.ok("request approved").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/{groupId}/promote")
    public Response promoteToAdmin(@PathParam("groupId") int groupId, @QueryParam("userId") int userId, @QueryParam("adminId") int adminId) {
        User admin = userService.getUserById(adminId);
        if (admin == null) return Response.status(Response.Status.NOT_FOUND).entity("Admin not found").build();
        try {
            groupService.promoteToAdmin(groupId, userId, admin);
            return Response.ok("member promoted").build();
        } catch (Exception e) {
            return Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{groupId}")
    public Response deleteGroup(@PathParam("groupId") int groupId, @QueryParam("requesterId") int requesterId) {
        User admin = userService.getUserById(requesterId);
        if (admin == null) return Response.status(Response.Status.UNAUTHORIZED).entity("User not found").build();
        try {
            groupService.deleteGroup(groupId, admin);
            return Response.ok("Group deleted").build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        }
    }
    
    @POST
    @Path("/{groupId}/posts")
    public Response postInGroup(@PathParam("groupId") int groupId,@QueryParam("userId") int userId,GroupPost postInput) {
        User user = userService.getUserById(userId);
        if (user == null)
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        try {
            groupService.postInGroup(user, groupId, postInput.getContent(), postInput.getImageURL());
            return Response.ok("Posted in Group").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    // Extra: Get posts in group
    @GET
    @Path("/{groupId}/posts")
    public List<GroupPost> getPosts(@PathParam("groupId") int groupId) {
    		if(groupService.find(groupId)!=null) {
    			return groupService.getPostsForGroup(groupId);
    		}
    		else {
    			throw new SecurityException("Group doesnt exist");
    		}
    }
    
    @GET
    @Path("GetbyId/{groupId}")
    public UserGroup getGroup(@PathParam("groupId")  int groupId) {
    	return groupService.find(groupId);
    }
    @GET
    @Path("GetMembers/{groupId}")
    public List<Map<String, Object>> getGroupMembers(@PathParam("groupId")  int groupId) {
    	return groupService.getGroupMembersMap(groupId);
    }
}
