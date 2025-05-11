package entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class GroupMembership {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Group group;

    @Column
    private String status;
    
    @Column
    private String role;
    
    public int getId() {
    	return id;
    }
    
    public void setUser(User user) {
    	this.user = user;
    }
    
    public User getUser() {
    	return user;
    }
    
    public void setGroup(Group group) {
    	this.group = group;
    }
    
    public Group getGroup() {
    	return group;
    }
    
    public void setStatus(String status) {
    	this.status = status;
    }
    
    public String getStatus() {
    	return status;
    }
    public void setRole(String role) {
    	this.role = role;
    }
    
    public String getRole() {
    	return role;
    }
}
