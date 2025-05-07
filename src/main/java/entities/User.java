package entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Email;

@Entity
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private int id;
	
	@Column
	@NotBlank
	private String Name;
	
	@Column
	@NotBlank
	@Email
	private String Email;
	
	@Column
	private String Bio;
	
	@Column
	@NotBlank
	private String Password;
	
	@Column
	@NotBlank
	private String role;
	
	@OneToMany(mappedBy = "user")
	private List<Post> posts= new ArrayList<Post>();
	
	@OneToMany(mappedBy = "user")
	private List<Comment> comment= new ArrayList<Comment>();
	
	@OneToMany(mappedBy = "user")
	private List<Like> like= new ArrayList<Like>();
	
	public User() {
		
	}
	
	
	public int getId() {
		return id;
	}
	public void setName(String Name) {
		this.Name = Name;
	}
	public String getName() {
		return Name;
	}
	public void setEmail(String email) {
		this.Email = email;
	}
	public String getEmail() {
		return Email;
	}
	public void setBio(String bio) {
		this.Bio = bio;
	}
	public String getBio() {
		return Bio;
	}
	public void setPassword(String password) {
		this.Password = password;
	}
	public String getPassword() {
		return Password;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getRole() {
		return role;
	}
}
