package services;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import entities.Comment;
import entities.PostsLike;
import entities.Post;
import entities.User;

@Stateless
public class PostService {
	
	@PersistenceContext(unitName = "hello")
    private EntityManager em;

    // Create a post
    public Post createPost(User user, String content, String imageUrl) {
        Post post = new Post();
        post.setUser(user);
        post.setContent(content);
        post.setImageUrl(imageUrl);
        em.persist(post);
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
        if (!post.getUser().equals(editor))
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

    // Like a post (idempotent)
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
    }

    // Comment on a post
    public void addComment(User user, int postId, String content) {
        Post post = em.find(Post.class, postId);
        if (post == null)
            throw new IllegalArgumentException("Post not found.");
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setPost(post);
        comment.setContent(content);
        em.persist(comment);
    }

    // Get all comments for a post
    public List<Comment> getCommentsForPost(int postId) {
        return em.createQuery("SELECT c FROM Comment c WHERE c.post.id = :postId", Comment.class)
            .setParameter("postId", postId)
            .getResultList();
    }

    // Get a post by ID
    public Post find(int postId) {
        return em.find(Post.class,postId);
    }
}
