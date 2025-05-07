package entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.wildfly.common.annotation.NotNull;

@Entity
public class Comment {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
    private int id;
	
	@Column
	@NotNull
	private String content;
	
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "post_id",referencedColumnName = "id")
	private Post post;

	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "user_id",referencedColumnName = "id")
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
