package services;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import entities.UserGroup;
import entities.GroupMembership;
import entities.GroupPost;
import entities.User;

import java.util.List;

@Stateless
public class GroupService {

	@PersistenceContext(unitName = "hello")
    private EntityManager em;

    // Create group and add admin as first member
    public UserGroup createGroup(User admin, String name, String description, boolean isOpen) {
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
        
        return group;
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
            membership.setStatus("MEMBER");
            membership.setRole("MEMBER");
        } else {
            membership.setStatus("PENDING");
            membership.setRole("MEMBER");
        }
        em.persist(membership);
        // Optionally: send notification (JMS etc) if group.isOpen()==false
    }

    // Approve membership request (by admin)
    public void approveMembership(int membershipId, User admin) {
        GroupMembership membership = em.find(GroupMembership.class, membershipId);
        if (membership == null)
            throw new IllegalArgumentException("Membership not found.");
        UserGroup group = membership.getGroup();
        if (group.getAdmin().getId()!=admin.getId())
            throw new SecurityException("Only admin can approve.");
        membership.setStatus("MEMBER");
        em.merge(membership);
        // Optionally: send notification (JMS etc)
    }

    // Remove a member from group (by admin)
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

        // (Optional safety) Don't let the admin leave if they are the only admin; up to your business rules!
        em.remove(result.get(0));
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
    }

    // List all groups user is a member of
    public List<UserGroup> getGroupsForUser(User user) {
        return em.createQuery(
            "SELECT m.group FROM GroupMembership m WHERE m.user = :user AND m.status = 'MEMBER'", UserGroup.class
        ).setParameter("user", user).getResultList();
    }

    // List all group posts
    public List<GroupPost> getPostsForGroup(int groupId) {
        return em.createQuery(
            "SELECT p FROM GroupPost p WHERE p.group.id = :groupId ORDER BY p.id DESC", GroupPost.class
        ).setParameter("groupId", groupId).getResultList();
    }

    // Delete group (by admin)
    public void deleteGroup(int groupId, User admin) {
        UserGroup group = em.find(UserGroup.class, groupId);
        if (group == null)
            throw new IllegalArgumentException("Group not found.");
        if (group.getAdmin().getId()!=admin.getId())
            throw new SecurityException("Only admin can delete.");
        em.remove(group);
    }

    // Get group by ID
    public UserGroup find(int id) {
        return em.find(UserGroup.class, id);
    }
}
