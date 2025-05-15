package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import entities.Comment;
import entities.PostsLike;
import entities.Post;
import entities.User;
import notification.NotificationEvent;

@Stateless
public class PostService {
	@EJB
	private NotificationProducer p;
	@PersistenceContext(unitName = "hello")
    private EntityManager em;
	@Inject
	private UserService userService;

    public Post createPost(User user, String content, String imageUrl) {
        Post post = new Post();
        post.setUser(user);
        post.setContent(content);
        post.setImageUrl(imageUrl);
        em.persist(post);
        
        List<User> friends = userService.getFriends(user);
        for (User friend : friends) {
            p.sendNotification(new NotificationEvent("New Post", friend.getId() , friend.getName(), user.getName()+" has posted"));

        }
        return post;
    }

    // Edit post (only by owner!)
    public void editPost(int postId, String content, String imageUrl, User editor) {
        Post post = em.find(Post.class, postId);
        if (post == null)
            throw new IllegalArgumentException("Post not found.");
        if (post.getUser().getId()!=editor.getId())
            throw new SecurityException("You can only edit your own post.");
        post.setContent(content);
        post.setImageUrl(imageUrl);
        em.merge(post);
    }

    // Delete a post (only by owner!)
    public void deletePost(int postId, User editor) {
        Post post = em.find(Post.class, postId);
        if (post == null)
            throw new IllegalArgumentException("Post not found.");
        if (post.getUser().getId()!=editor.getId())
            throw new SecurityException("You can only delete your own post.");
        em.remove(post);
    }

    // Get user's feed: their posts + posts by friends (most recent first)
    public List<Post> getFeed(User user) {
        // Fetch friends' ids (avoid infinite loops - get just references)
        TypedQuery<Integer> friendIdsQuery = em.createQuery(
            "SELECT CASE WHEN fr.sender = :user THEN fr.receiver.id ELSE fr.sender.id END " +
            "FROM FriendRequest fr WHERE (fr.sender = :user OR fr.receiver = :user) AND fr.status = 'ACCEPTED'", Integer.class
        ).setParameter("user", user);
        List<Integer> friendIds = friendIdsQuery.getResultList();

        // Find posts by user and their friends
        TypedQuery<Post> feedQuery = em.createQuery(
            "SELECT p FROM Post p WHERE p.user = :user OR p.user.id IN :friendIds ORDER BY p.id DESC", Post.class
        ).setParameter("user", user)
         .setParameter("friendIds", friendIds.isEmpty() ? List.of(-1) : friendIds); // avoid empty IN clause

        return feedQuery.getResultList();
    }

    public void likePost(User user, int postId) {
        Post post = em.find(Post.class, postId);
        if (post == null)
            throw new IllegalArgumentException("Post not found.");
        // Check for existing like
        Long count = em.createQuery(
            "SELECT COUNT(l) FROM PostsLike l WHERE l.post = :post AND l.user = :user",Long.class
        ).setParameter("post", post)
         .setParameter("user", user)
         .getSingleResult();
        
        if (count > 0) throw new IllegalStateException("Already liked.");
        PostsLike like = new PostsLike();
        like.setUser(user);
        like.setPost(post);
        em.persist(like);
        p.sendNotification(new NotificationEvent("Liked Post", post.getUser().getId() , post.getUser().getName(), user.getName()+" liked your Post"));
    }

    public void addComment(User user, int postId, String content) {
        Post post = em.find(Post.class, postId);
        if (post == null)
            throw new IllegalArgumentException("Post not found.");
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setPost(post);
        comment.setContent(content);
        em.persist(comment);
        p.sendNotification(new NotificationEvent("Comment on Post", post.getUser().getId() , post.getUser().getName(), user.getName()+" Commented : "+content));
    }

    public List<Map<String, Object>> getCommentsForPostMapped(int postId) {
        List<Comment> comments = em.createQuery(
            "SELECT c FROM Comment c WHERE c.post.id = :postId", Comment.class)
            .setParameter("postId", postId)
            .getResultList();

        List<Map<String, Object>> result = new ArrayList<>();
        for (Comment comment : comments) {
            Map<String, Object> map = new HashMap<>();
            map.put("commentId", comment.getId());
            map.put("content", comment.getContent());
            map.put("username", comment.getUser().getName());
            result.add(map);
        }
        return result;
    }

    // Get a post by ID
    public Post find(int postId) {
        return em.find(Post.class,postId);
    }
}
