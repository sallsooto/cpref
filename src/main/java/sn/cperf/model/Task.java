package sn.cperf.model;

import java.io.Serializable;
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
	@JsonManagedReference
	private Task parent;
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY,mappedBy="parent")
	@JsonBackReference
	private List<Task> chirlds;
	@ManyToOne
	@JoinColumn(name="section_id")
	@JsonManagedReference
	private ProcessSection section;
	@ManyToOne
	@JoinColumn(name="group_id")
	private Group group;
	@ManyToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinTable(name="users_tasks",
		joinColumns = {@JoinColumn(name="task_id")},
		inverseJoinColumns = {@JoinColumn(name="user_id")}
	)
	@JsonManagedReference
	private List<User> users;
}
