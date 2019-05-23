package sn.cperf.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Table(name="tasks")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class Task implements Serializable{
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	private String name;
	@Column(length=100)
	private String type;
	@Column(length=100)
	private String status;
	private String description;
	private String fileDescriptionPath;
	@ManyToOne
	@JoinColumn(name="parent_id")
	@JsonBackReference
	private Task parent;
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY,mappedBy="parent")
	@JsonBackReference
	private List<Task> chirlds;
	@ManyToOne
	@JoinColumn(name="section_id")
	@JsonBackReference
	private ProcessSection section;
	@ManyToOne
	@JoinColumn(name="group_id")
	@JsonBackReference
	private Group group;
	@Column(columnDefinition="int(11) default 0")
	private int nbYears=0;
	@Column(columnDefinition="int(11) default 0")
	private int nbMonths=0;
	@Column(columnDefinition="int(11) default 0")
	private int nbDays=0;
	@Column(columnDefinition="int(11) default 1")
	private int nbHours=1;
	@Column(columnDefinition="int(11) default 0")
	private int nbMinuites=0;  
	@ManyToMany(cascade=CascadeType.PERSIST, fetch=FetchType.LAZY)
	@JoinTable(name="users_tasks",
		joinColumns = {@JoinColumn(name="task_id")},
		inverseJoinColumns = {@JoinColumn(name="user_id")}
	)
	@JsonManagedReference
	private List<User> users;
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY,mappedBy="task")
	@JsonManagedReference
	private List<Justification> justifications;
	@OneToMany(mappedBy="task", fetch=FetchType.LAZY)
	@JsonBackReference
	private List<Objectif> objectifs;
	public  List<User> getAllUsers() {
		List<User> allUsers = new ArrayList<User>();
		try {
			if(this.getUsers() != null) {
				for(User user : this.getUsers()) {
					if(!userFounded(allUsers, user))
						allUsers.add(user);
				}
			}
			if(this.getGroup() != null && this.getGroup().getUsers() != null) {
				for(User user : this.getGroup().getUsers()) {
					if(!userFounded(allUsers, user))
						allUsers.add(user);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return allUsers;
	}
	
	private boolean userFounded(List<User> users, User user) {
		if(!users.isEmpty() && user != null) {
			for(User u : users) {
				if(u.getId() == user.getId())
					return true;
			 }
		}
		return false;
	}
	
	public boolean isProcessLunched() {
		try {
			return this.getSection().getProcess().getStartAt() !=  null;
		} catch (Exception e) {
		}
		return false;
	}
	
	public boolean isProcessExpired() {
		if(this.getSection() != null && this.getSection().getProcess() != null) {
			return this.getSection().getProcess().isExpired();
		}
		return false;
	}
	public Long getProcessId() {
		if(this.getSection() != null && this.getSection().getProcess() != null)
			return this.getSection().getProcess().getId();
		return null;
	}
}
