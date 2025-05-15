package services;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import entities.UserGroup;
import notification.NotificationEvent;
import entities.GroupMembership;
import entities.GroupPost;
import entities.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class GroupService {
	@EJB
	private NotificationProducer p;
	@PersistenceContext(unitName = "hello")
    private EntityManager em;

	public Map<String, Object> createGroup(User admin, String name, String description, boolean isOpen) {
	    UserGroup group = new UserGroup();
	    group.setName(name);
	    group.setDescription(description);
	    group.setOpen(isOpen);
	    group.setAdmin(admin);
	    em.persist(group);

	    GroupMembership membership = new GroupMembership();
	    membership.setGroup(group);
	    membership.setUser(admin);
	    membership.setStatus("MEMBER");
	    membership.setRole("ADMIN");
	    em.persist(membership);

	    Map<String, Object> map = new HashMap<>();
	    map.put("groupId", group.getId());
	    map.put("groupName", group.getName());
	    map.put("description", group.getDescription());
	    map.put("isOpen", group.isOpen());
	    map.put("adminId", admin.getId());
	    return map;
	}
    
    public UserGroup getGroupById(int groupId) {
    	return em.find(UserGroup.class,groupId);
    }

    // Request to join a group
    public void requestToJoinGroup(User user, int groupId) {
        UserGroup group = em.find(UserGroup.class, groupId);
        if (group == null)
            throw new IllegalArgumentException("Group not found.");

        TypedQuery<Long> existQuery = em.createQuery(
            "SELECT count(m) FROM GroupMembership m WHERE m.group = :group AND m.user = :user", Long.class
        ).setParameter("group", group)
         .setParameter("user", user);

        if (existQuery.getSingleResult() > 0)
            throw new IllegalStateException("Already a member or request pending.");

        GroupMembership membership = new GroupMembership();
        membership.setGroup(group);
        membership.setUser(user);
        if (group.isOpen()) {
        	em.persist(membership);
        	List<User> members = getGroupMembers(group.getId());
            for (User member : members) {
                p.sendNotification(new NotificationEvent("join group", member.getId() , member.getName()," has joined : "+group.getName()));
            }
            membership.setStatus("MEMBER");
            membership.setRole("MEMBER");
        } else {
            membership.setStatus("PENDING");
            membership.setRole("MEMBER");
            em.persist(membership);
        }
    }

    public void approveMembership(int membershipId, User admin) {
        GroupMembership membership = em.find(GroupMembership.class, membershipId);
        if (membership == null)
            throw new IllegalArgumentException("Membership not found.");
        UserGroup group = membership.getGroup();
        if (group.getAdmin().getId()!=admin.getId())
            throw new SecurityException("Only admin can approve.");
        membership.setStatus("MEMBER");
        em.merge(membership);
        p.sendNotification(new NotificationEvent("Approve membership", membership.getUser().getId() , membership.getUser().getName(), admin.getName()+" approved your membership"));
        List<User> members = getGroupMembers(group.getId());
        for (User member : members) {
            p.sendNotification(new NotificationEvent("join group", member.getId() , member.getName(),membership.getUser().getName()+" has joined : "+group.getName()));
        }
    }
    
    public void rejectMembership(int membershipId, User admin) {
        GroupMembership membership = em.find(GroupMembership.class, membershipId);
        if (membership == null)
            throw new IllegalArgumentException("Membership not found.");
        UserGroup group = membership.getGroup();
        if (group.getAdmin().getId() != admin.getId())
            throw new SecurityException("Only admin can reject.");
        em.remove(membership);
        p.sendNotification(new NotificationEvent("Reject membership",membership.getUser().getId(),membership.getUser().getName(),admin.getName() + " rejected your request to join " + group.getName()));
    }

    public void removeUserFromGroup(int groupId, int userId, User admin) {
        UserGroup group = em.find(UserGroup.class, groupId);
        if (group == null)
            throw new IllegalArgumentException("Group not found.");
        if (group.getAdmin().getId()!=admin.getId())
            throw new SecurityException("Only admin can remove.");
        TypedQuery<GroupMembership> query = em.createQuery(
            "SELECT m FROM GroupMembership m WHERE m.group = :group AND m.user.id = :userId", GroupMembership.class
        ).setParameter("group", group)
         .setParameter("userId", userId);
        List<GroupMembership> result = query.getResultList();
        if (result.isEmpty())
            throw new IllegalArgumentException("Membership not found.");
        em.remove(result.get(0));
//        p.sendNotification(new NotificationEvent("remove member", query.getSingleResult().getUser().getId() , query.getSingleResult().getUser().getName(), admin.getName()+" removed you from group : "+group.getName()));

    }
    
    public void leaveGroup(int groupId, int userId) {
        UserGroup group = em.find(UserGroup.class, groupId);
        if (group == null)
            throw new IllegalArgumentException("Group not found.");

        TypedQuery<GroupMembership> query = em.createQuery(
            "SELECT m FROM GroupMembership m WHERE m.group = :group AND m.user.id = :userId", GroupMembership.class
        ).setParameter("group", group)
         .setParameter("userId", userId);

        List<GroupMembership> result = query.getResultList();
        if (result.isEmpty())
            throw new IllegalArgumentException("Membership not found.");
        em.remove(result.get(0));
        List<User> members = getGroupMembers(query.getSingleResult().getUser().getId());
        for (User member : members) {
            p.sendNotification(new NotificationEvent("leave group", member.getId() , member.getName()," has left : "+group.getName()));
        }
    }

    // Promote another user to admin
    public void promoteToAdmin(int groupId, int userId, User admin) {
        UserGroup group = em.find(UserGroup.class, groupId);
        if (group == null)
            throw new IllegalArgumentException("Group not found.");
        if (group.getAdmin().getId()!=admin.getId())
            throw new SecurityException("Only admin can promote.");
        // Transfer admin role
        User newAdmin = em.find(User.class, userId);
        if (newAdmin == null)
            throw new IllegalArgumentException("User not found.");

        group.setAdmin(newAdmin);
        em.merge(group);

        // Set their membership role to ADMIN
        TypedQuery<GroupMembership> query = em.createQuery(
            "SELECT m FROM GroupMembership m WHERE m.group = :group AND m.user = :user", GroupMembership.class
        ).setParameter("group", group)
         .setParameter("user", newAdmin);
        GroupMembership membership = query.getSingleResult();
        membership.setRole("ADMIN");
        em.merge(membership);
        p.sendNotification(new NotificationEvent("Promotion", userId, newAdmin.getName(), "You were Promoted to be group Admin"));
    }

    // Create a post in the group (only by member)
    public void postInGroup(User user, int groupId, String content, String imageUrl) {
        UserGroup group = em.find(UserGroup.class, groupId);
        if (group == null)
            throw new IllegalArgumentException("Group not found.");

        // check membership
        TypedQuery<GroupMembership> query = em.createQuery(
            "SELECT m FROM GroupMembership m WHERE m.group = :group AND m.user = :user AND m.status = 'MEMBER'",
            GroupMembership.class)
            .setParameter("group", group)
            .setParameter("user", user);
        if (query.getResultList().isEmpty())
            throw new SecurityException("Not a group member.");
        GroupPost post = new GroupPost();
        post.setContent(content);
        post.setImageURL(imageUrl); // typo safe
        post.setGroup(group);
        post.setUser(user);
        em.persist(post);
        List<User> members = getGroupMembers(group.getId());
        for (User member : members) {
            p.sendNotification(new NotificationEvent("group post", member.getId() , member.getName(),user.getName()+" has posted in : "+group.getName()));
        }
    }

    // get all groups user is a member of
    public List<UserGroup> getGroupsForUser(User user) {
        return em.createQuery(
            "SELECT m.group FROM GroupMembership m WHERE m.user = :user AND m.status = 'MEMBER'", UserGroup.class
        ).setParameter("user", user).getResultList();
    }

    // get all group posts
    public List<GroupPost> getPostsForGroup(int groupId) {
        return em.createQuery(
            "SELECT p FROM GroupPost p WHERE p.group.id = :groupId ORDER BY p.id DESC", GroupPost.class
        ).setParameter("groupId", groupId).getResultList();
    }

    public void deleteGroup(int groupId, User admin) {
        UserGroup group = em.find(UserGroup.class, groupId);
        if (group == null)
            throw new IllegalArgumentException("Group not found.");
        if (group.getAdmin().getId()!=admin.getId())
            throw new SecurityException("Only admin can delete.");
        em.remove(group);
    }
    
    public List<Map<String, Object>> getGroupMembersMap(int groupId) {
        UserGroup group = em.find(UserGroup.class, groupId);
        if (group == null) return List.of();

        int adminId = group.getAdmin().getId();
        String adminName = group.getAdmin().getName();

        // You may need to fetch memberships with a query if not using eager fetching
        List<GroupMembership> memberships = em.createQuery(
            "SELECT m FROM GroupMembership m WHERE m.group.id = :groupId", GroupMembership.class
        ).setParameter("groupId", groupId).getResultList();

        List<Map<String, Object>> result = new ArrayList<>();
        for (GroupMembership gm : memberships) {
            User member = gm.getUser();
            Map<String, Object> map = new HashMap<>();
            map.put("groupId", group.getId());
            map.put("groupName", group.getName());
            map.put("memberId", member.getId());
            map.put("memberName", member.getName());
            map.put("adminId", adminId);
            map.put("adminName", adminName);
            result.add(map);
        }
        return result;
    }
    
    public List<User> getGroupMembers(int groupId) {
        List<GroupMembership> memberships = em.createQuery(
            "SELECT m FROM GroupMembership m WHERE m.group.id = :groupId AND m.status = 'MEMBER'", GroupMembership.class
        ).setParameter("groupId", groupId)
         .getResultList();

        List<User> members = new ArrayList<>();
        for (GroupMembership gm : memberships) {
            members.add(gm.getUser());
        }
        return members;
    }

    public UserGroup find(int id) {
        return em.find(UserGroup.class, id);
    }
}
