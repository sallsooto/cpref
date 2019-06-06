package sn.cperf.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sn.cperf.util.TaskStatus;


@Entity
@Table(name="process")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class Processus implements Serializable{
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	private String label;
	private String description;
	@ManyToOne
	@JoinColumn(name="editor_id")
	private User editor;
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern="dd/MM/yyyy")
	private Date previewStartDate;
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern="dd/MM/yyyy")
	private Date previewFinishDate;
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern="dd/MM/yyyy")
	private Date totalTime;
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern="dd/MM/yyyy")
	private Date startDate;
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern="dd/MM/yyyy")
	private Date finishDate;
	@OneToMany(mappedBy="process")
	@JsonManagedReference
	List<ProcessSection> sections;
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
	private Date startAt;
	@Column(columnDefinition="boolean default true")
	private boolean valid=true;
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY,mappedBy="process")
	@JsonManagedReference
	private List<Justification> justifications;
	
	public Date getMaxDate() {
		if(startAt != null) {
			int nbYears =0, nbMonths=0, nbDays=0, nbHours=0, nbMinutes=0;
			if(this.getSections() != null && !this.getSections().isEmpty()) {
				for(ProcessSection section : this.getSections()) {
					if(section.getTasks() != null && !section.getTasks().isEmpty()) {
						for(Task task : section.getTasks()) {
							if(task.getStartAt() != null) {
								Calendar taskCalander = Calendar.getInstance();
								taskCalander.setTime(task.getStartAt());
								nbYears = nbYears + taskCalander.get(GregorianCalendar.YEAR);
								nbMonths = nbMonths + taskCalander.get(GregorianCalendar.MONTH);
								nbDays = nbDays+ taskCalander.get(GregorianCalendar.DAY_OF_MONTH);
								nbHours = nbHours + taskCalander.get(GregorianCalendar.HOUR);
								nbMinutes = nbMinutes + taskCalander.get(GregorianCalendar.MINUTE);
								
							}
							nbYears = nbYears + task.getNbYears();
							nbMonths = nbMonths + task.getNbMonths();
							nbDays = nbDays+task.getNbDays();
							nbHours = nbHours + task.getNbHours();
							nbMinutes = nbMinutes + task.getNbMinuites();
						}
					}
				}
			}
			Calendar calander = Calendar.getInstance();
			calander.setTime(startAt);
			calander.set(GregorianCalendar.YEAR, calander.get(GregorianCalendar.YEAR) + nbYears);
			calander.set(GregorianCalendar.MONTH, calander.get(GregorianCalendar.MONTH) + nbMonths);
			calander.set(GregorianCalendar.DAY_OF_MONTH, calander.get(GregorianCalendar.DAY_OF_MONTH) + nbDays);
			calander.set(GregorianCalendar.HOUR, calander.get(GregorianCalendar.HOUR) + nbHours);
			calander.set(GregorianCalendar.MINUTE, calander.get(GregorianCalendar.MINUTE) + nbMinutes);
			return calander.getTime();
		}
		return null;
	}
	
	public boolean  getIsFinished() {
		int noCompletedTaskFinded = 0;
		if(this.startAt != null) {
			if( this.getSections() != null &&  !this.getSections().isEmpty()) {
				for(ProcessSection section : this.getSections()) {
					if(section.getTasks() != null && !section.getTasks().isEmpty()) {
						for(Task task : section.getTasks()) {
							if(!task.getStatus().toLowerCase().equals(TaskStatus.COMPLETED.toString().toLowerCase())
								&& !task.getStatus().toLowerCase().equals(TaskStatus.CANCELED.toString().toLowerCase())) {
								noCompletedTaskFinded++;
								return false;
							}
						}
					}
				}
			}
		}
		return noCompletedTaskFinded<=0;
	}
	
	public List<User> getAllUsers(){
		List<User> processUsers = new ArrayList<>();
		if(this.getSections() != null && !this.getSections().isEmpty()) {
			for(ProcessSection section : this.getSections()) {
				if(section.getTasks() != null && !section.getTasks().isEmpty()) {
					for(Task task : section.getTasks()) {
						if(task.getAllUsers() != null && !task.getAllUsers().isEmpty()) {
							for(User user : task.getAllUsers()) {
								if(!processUsers.contains(user)) {
									processUsers.add(user);
								}
							}
						}
					}
				}
			}
		}
		return processUsers;
	}
	
	public boolean isExpired() {
		if(this.startAt != null && !getIsFinished()) {
			return this.getMaxDate().compareTo(new Date())<0;
		}
		return false;
	}
	
	public List<Task> getTasks(){
		List<Task> tasks = new ArrayList<>();
		if(sections != null && !sections.isEmpty()) {
			for(ProcessSection section : sections) {
				if(section.getTasks() != null && !section.getTasks().isEmpty()) {
					for(Task task : section.getTasks()) {
						if(!tasks.contains(task))
							tasks.add(task);
					}
				}
					
			}
		}
		return tasks;
	}
}
