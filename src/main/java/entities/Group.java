package entities;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

@Entity
public class Group {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column
	@NotNull
	private String Name;
	
	@Column
	private String Description;
	
	@Column
	private String groupType;
	
	@OneToOne
	@JoinColumn(name = "admin_id")
	private User Admin;
	
	@ManyToMany
	@JoinTable(name="UserXGroup",
	joinColumns = @JoinColumn(name = "member_id"),
	inverseJoinColumns = @JoinColumn(name = "group_id"))
	private List<User> members;
	
	public int getId() {
		return id;
	}
	
	public void setName( String name) {
		this.Name = name;
	}
	public String getName() {
		return this.Name;
	}
	public void setDescription( String description) {
		this.Description = description;
	}
	public String getDescription() {
		return this.Description;
	}
	public void setAdmin(User admin) {
		this.Admin = admin;
	}
	public User getAdmin() {
		return Admin;
	}
	public void setGroupType(String type) {
		this.groupType = type;
	}
	
	public String getGroupType() {
		return groupType;
	}
	
}
