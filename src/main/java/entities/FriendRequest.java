package entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class FriendRequest {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @Column(nullable=false)
    private String status;
    
    public int getId() {
    	return id;
    }
    
    public void setSender(User sender) {
    	this.sender = sender;
    }
    public User getSender() {
    	return sender;
    }
    public void setReceiver(User receiver) {
    	this.receiver = receiver;
    }
    public User getReceiver() {
    	return sender;
    }
    public void setStatus(String status) {
    	this.status = status;
    }
    public String getStatus() {
    	return status;
    }
}
