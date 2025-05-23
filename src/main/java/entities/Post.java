package entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

@Entity
public class Post {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private int id;
	
	@Column
	@NotNull
	private String content;
	
	@Column
	private String imageUrl;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	 
	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
	private List<Comment> comment = new ArrayList<Comment>();
	
	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
	private List<PostsLike> like = new ArrayList<PostsLike>();
	 
	 public Post() {
		 
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

	public String getImageUrl() { 
	   return imageUrl; 
	}
	    
	public void setImageUrl(String imageUrl) { 
	   this.imageUrl = imageUrl; 
	}

	public User getUser() { 
	   return user; 
	}
	    
	public void setUser(User user) { 
	   this.user = user; 
	}
	 
}
