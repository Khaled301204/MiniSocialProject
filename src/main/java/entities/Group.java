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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

@Entity
public class Group {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column
	@NotNull
	private String name;
	
	@Column
	private String description;
	
	@Column
	private boolean isOpen;
	
	@OneToOne
	@JoinColumn(name = "admin_id")
	private User admin;
	
	@OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<GroupMembership> members = new ArrayList<>();
	
	@OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<GroupPost> posts = new ArrayList<>();

	
	public int getId() {
		return id;
	}
	
	public void setName( String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setDescription( String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public void setAdmin(User admin) {
		this.admin = admin;
	}
	
	public User getAdmin() {
		return admin;
	}
	
	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}
	
	public boolean isOpen() {
		return isOpen;
	}
	
}
