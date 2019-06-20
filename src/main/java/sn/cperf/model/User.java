package sn.cperf.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javassist.bytecode.stackmap.TypeData.ClassName;

@Entity
@Table(name="users")
public class User implements Serializable,UserDetails{
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String firstname;
	private String lastname;
	@Column(length = 100, unique = true, nullable = false)
	private String username;
	@Column(nullable = false)
	private String password;
	@Column(length = 100, unique = true, nullable = false)
	private String email;
	private String adresse;
	private String phone;
	private String activite;
	@Column(columnDefinition="boolean default true")
	private boolean organigramme = true;
	
	@ManyToOne(cascade=CascadeType.PERSIST)
	@JoinColumn(name="fonction_id")
	@JsonManagedReference
	private Fonction fonction;
	private String photo;
	@ManyToOne(cascade=CascadeType.PERSIST)
	@JsonManagedReference
	private User userSup;
	@OneToMany(fetch=FetchType.LAZY,mappedBy="userSup")
	@JsonBackReference
	private List<User> users;
	public User getUserSup() {
		return userSup;
	}
	public void setUserSup(User userSup) {
		this.userSup = userSup;
	}
	public List<User> getUsers() {
		return users;
	}
	public void setUsers(List<User> users) {
		this.users = users;
	}
	@Column(columnDefinition = "boolean default false", name="valid")
	private boolean valid = false;
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "users_roles", 
		joinColumns = { @JoinColumn(name = "user_id") },
		inverseJoinColumns = {@JoinColumn(name = "role_id") })
	@JsonManagedReference
	private List<Role> roles;
	@ManyToMany(mappedBy="users")
	private List<Group> groupes;
	@JoinColumn(name="photo_id")
	@ManyToOne(fetch=FetchType.LAZY,cascade=CascadeType.PERSIST)
	@JsonManagedReference
	private DBFile DbPhoto;

	public DBFile getDbPhoto() {
		return DbPhoto;
	}
	public void setDbPhoto(DBFile dbPhoto) {
		DbPhoto = dbPhoto;
	}
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> autorities = new ArrayList<>();
		this.getRoles().forEach(role ->{
			autorities.add(new SimpleGrantedAuthority(role.getRole().toUpperCase()));
		});
		this.getGroupes().forEach(g->{
			autorities.add(new SimpleGrantedAuthority(g.getRole().getRole().toUpperCase()));
		});
		// set default autority for all users
		autorities.add(new SimpleGrantedAuthority("user"));
		return autorities;
	}
	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return this.password;
	}
	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return this.username;
	}
	public User(Long id, String firstname, String lastname, String username, String password, String email,
			String adresse, String phone, String photo, boolean valid, List<Role> roles) {
		super();
		this.id = id;
		this.firstname = firstname;
		this.lastname = lastname;
		this.username = username;
		this.password = password;
		this.email = email;
		this.adresse = adresse;
		this.phone = phone;
		this.photo = photo;
		this.valid = valid;
		this.roles = roles;
	}
	public User() {
		super();
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAdresse() {
		return adresse;
	}
	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	public List<Role> getRoles() {
		List<Role> distinctsRoles = new ArrayList<>();
		if(!roles.isEmpty()) {
			for(Role role : roles) {
				if(!distinctsRoles.contains(role))
					distinctsRoles.add(role);
			}
		}
		return distinctsRoles;
	}
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public Fonction getFonction() {
		return fonction;
	}
	public void setFonction(Fonction fonction) {
		this.fonction = fonction;
	}
	public boolean isOrganigramme() {
		return organigramme;
	}
	public void setOrganigramme(boolean organigramme) {
		this.organigramme = organigramme;
	}
	
	public boolean hasRole(String role) {
		for(Role r : this.getRoles()) {
			if(r.getRole().equals(role) || r.getRole().equals(role.toUpperCase()) ||
			   r.getRole().equals("ROLE_"+role) || r.getRole().equals(("ROLE_"+role).toUpperCase()))
				return true;
		}
		if(this.getAuthorities() != null) {
			for(GrantedAuthority auth : this.getAuthorities()) {
				if(auth.getAuthority().equals(role) || auth.getAuthority().equals(role.toUpperCase()) ||
				   auth.getAuthority().equals("ROLE_"+role) || auth.getAuthority().equals(("ROLE_"+role).toUpperCase()))
					return true;
			}
		}
		return false;
	}
	public List<Group> getGroupes() {
		return groupes;
	}
	public void setGroupes(List<Group> groupes) {
		this.groupes = groupes;
	}
	
	public String getActivite() {
		return activite;
	}
	public void setActivite(String activite) {
		this.activite = activite;
	}
	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return valid;
	}
}
