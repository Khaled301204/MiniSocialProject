package entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class GroupPost {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String content;

    private String imageUrl;

    @ManyToOne
    private UserGroup group;

    @ManyToOne
    private User user;
    
    public int getId() {
    	return id;
    }
    
    public void setContent(String content) {
    	this.content = content;
    }
    
    public String getContent() {
	   return content;
    }
    
    public void setImageURL(String url) {
	   this.imageUrl = url;
    }
   
    public String getImageURL() {
	   return imageUrl;
    }
    
    public void setGroup(UserGroup group) {
	   this.group = group;
    }
   
    public UserGroup getGroup() {
	   return group;
    }
    
	public void setUser(User user) {
		this.user = user;
	}
	   
	public User getUser() {
		return user;
    }
	
}
