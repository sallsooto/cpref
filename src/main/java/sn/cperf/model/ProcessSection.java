package sn.cperf.model;

import java.io.Serializable;
import java.util.ArrayList;
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

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="process_sections")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class ProcessSection implements Serializable{
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	@Column(length=100, nullable=false)
	private String name;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="process_id")
	@JsonBackReference
	private Processus process;
	@ManyToOne
	@JoinColumn(name="group_id")
	private Group group;
	@OneToMany(mappedBy="section")
	@JsonBackReference
	private List<Task> tasks;
	
	public List<User> users(){
		List<User> users = new ArrayList<>();
		if(this.getTasks() != null) {
			for(Task task : this.getTasks()) {
				List<User> taskUsers = task.getAllUsers();
				//getting users 
				if(taskUsers != null) {
					for(User taskuser : taskUsers) {
						if(!users.contains(taskuser))
							users.add(taskuser);
					}
				}
			}
		}
		return users;
	}
}
