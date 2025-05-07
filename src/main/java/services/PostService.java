package services;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import entities.Comment;
import entities.Like;
import entities.Post;
import entities.User;

@Stateless
public class PostService {
	
	@PersistenceContext(unitName = "hello")
    private EntityManager em;
	
	public Post createPost(Post post) {
	    em.persist(post);
	    return em.find(Post.class, post.getId());
	}


	public Post getPostById(int id) {
        return em.find(Post.class, id);
    }
	
	public List<Post> getAllPosts() {
        TypedQuery<Post> query = em.createQuery("SELECT p FROM Post p ", Post.class);
        return query.getResultList();
    }
	
	public Post updatePost(int postId, Post newPostData) {
        Post existing = em.find(Post.class, postId);
        if (existing == null) return null;
        existing.setContent(newPostData.getContent());
        existing.setImageUrl(newPostData.getImageUrl());
        return em.merge(existing);
    }
	
	public boolean deletePost(int postId) {
        Post post = em.find(Post.class, postId);
        if (post != null) {
            em.remove(post);
            return true;
        }
        else {
        	return false;
        }
    }
	
	public Like likePost(int postId, int userId) {
        Post post = em.find(Post.class, postId);
        User user = em.find(User.class, userId);
        if (post == null || user == null) return null;

        // Optional: prevent duplicate likes
        TypedQuery<Like> q = em.createQuery("SELECT l FROM Like l WHERE l.post.id = :postId AND l.user.id = :userId", Like.class);
        q.setParameter("postId", postId);
        q.setParameter("userId", userId);
        if (!q.getResultList().isEmpty()) return null;

        Like like = new Like();
        like.setPost(post);
        like.setUser(user);
        em.persist(like);
        return like;
    }
	
	 public Comment commentOnPost(int postId, int userId, String content) {
	        Post post = em.find(Post.class, postId);
	        User user = em.find(User.class, userId);
	        if (post == null || user == null) return null;

	        Comment comment = new Comment();
	        comment.setContent(content);
	        comment.setPost(post);
	        comment.setUser(user);
	        em.persist(comment);
	        return comment;
	    }
}
