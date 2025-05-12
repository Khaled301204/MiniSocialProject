package entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class PostsLike {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
    private int id;
	
	@ManyToOne
	@JoinColumn(name = "post_id",referencedColumnName = "id")
	private Post post;

	@ManyToOne
	@JoinColumn(name = "user_id",referencedColumnName = "id")
	private User user;
	
	
	public PostsLike() {
		
	}
	
	public int getId() { 
		return id; 
	}
    
    public Post getPost() { 
    	return post; 
    }
    
    public void setPost(Post post) { 
    	this.post = post; 
    }

    public User getUser() { 
    	return user; 
    }
    
    public void setUser(User user) { 
    	this.user = user; 
    }

	
}
