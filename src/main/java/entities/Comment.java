package entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;


@Entity
public class Comment {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
    private int id;
	
	@Column
	@NotNull
	private String content;
	
	@ManyToOne()
	@JoinColumn(name = "post_id")
	private Post post;

	@ManyToOne()
	@JoinColumn(name = "user_id")
	private User user;
	
	public Comment() {
		
	}
	
	public int getId() { 
		return id; 
	}

    public String getContent() { 
    	return content; 
    }
    
    public void setContent(String content) { 
    	this.content = content; 
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
