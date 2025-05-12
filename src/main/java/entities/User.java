package entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;

@Entity
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column
	@NotBlank
	private String name;
	
	@Column
	@NotBlank
	private String email;
	
	@Column
	private String bio;
	
	@Column
	@NotBlank
	private String password;
	
	@Column
	@NotBlank
	private String role;
	
	@OneToMany(mappedBy = "user")
	private List<Post> posts= new ArrayList<Post>();
	
	@OneToMany(mappedBy = "user")
	private List<Comment> comment= new ArrayList<Comment>();
	
	@OneToMany(mappedBy = "user")
	private List<PostsLike> like= new ArrayList<PostsLike>();
	
	@OneToMany(mappedBy = "sender", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
	private List<FriendRequest> sentRequests = new ArrayList<>();

	@OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
	private List<FriendRequest> receivedRequests = new ArrayList<>();
	
	@OneToMany(mappedBy = "admin")
	private List<UserGroup> createdGroups = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	private List<GroupMembership> groupMemberships = new ArrayList<>();
	
	public User() {
		
	}
	public int getId() {
		return id;
	}
	public void setName(String Name) {
		this.name = Name;
	}
	public String getName() {
		return name;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getEmail() {
		return email;
	}
	public void setBio(String bio) {
		this.bio = bio;
	}
	public String getBio() {
		return bio;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPassword() {
		return password;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getRole() {
		return role;
	}
	public void setSentRequests(List <FriendRequest> sentRequests) {
		this.sentRequests = sentRequests;
	}
	public List<FriendRequest> getSentRequests() {
		return sentRequests;
	}
	public void setReceivedRequests(List <FriendRequest> receivedRequests) {
		this.receivedRequests = receivedRequests;
	}
	public List<FriendRequest> getReceivedRequests() {
		return receivedRequests;
	}
	
}
