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
			Calendar calander = Calendar.getInstance();
			calander.setTime(startAt);
			if(this.getTasks() != null && !this.getTasks().isEmpty()) {
				List<Task> tasks = this.getTasks();
				for(Task task : tasks) {
					if(!task.getStatus().toLowerCase().equals(TaskStatus.CANCELED.toString().toLowerCase())) {
						if(task.getStartAt() != null) {
							Calendar taskCalander = Calendar.getInstance();
							Calendar tmpCalender = Calendar.getInstance();
							taskCalander.setTime(task.getStartAt());
							tmpCalender.setTime(startAt);
							// setting taskcalenderValues
//							taskCalander.add(GregorianCalendar.YEAR,Math.abs(task.getNbYears()));
//							taskCalander.add(GregorianCalendar.MONTH, Math.abs(task.getNbMonths()));
//							taskCalander.add(GregorianCalendar.DAY_OF_MONTH,Math.abs(task.getNbDays()));
//							taskCalander.add(GregorianCalendar.HOUR,Math.abs(task.getNbHours()));
//							taskCalander.add(GregorianCalendar.MINUTE,Math.abs(task.getNbMinuites()));
							// totales times values setting
							int totalNbYears = Math.abs(tmpCalender.get(Calendar.YEAR)-taskCalander.get(Calendar.YEAR)) + task.getNbYears();
							int totalNbMonths =  Math.abs(tmpCalender.get(Calendar.MONTH)-taskCalander.get(Calendar.MONTH)) + task.getNbMonths();
							int totalNbDays = Math.abs(tmpCalender.get(Calendar.DAY_OF_MONTH)-taskCalander.get(Calendar.DAY_OF_MONTH)) +task.getNbDays();
							int totalNbHours = Math.abs(tmpCalender.get(Calendar.HOUR)-taskCalander.get(Calendar.HOUR)) + task.getNbHours();
							int totalNbMinutes = Math.abs(tmpCalender.get(Calendar.MINUTE)-taskCalander.get(Calendar.MINUTE)) + task.getNbYears();
				    		// set calander values 
							System.err.println(task.getName() + " diff years : "+ totalNbYears);
							calander.add(GregorianCalendar.YEAR,totalNbYears );
							System.err.println(task.getName() + " diff months : "+totalNbMonths);
							calander.add(GregorianCalendar.MONTH, totalNbMonths);
							System.err.println(task.getName() + " diff days : "+ totalNbDays);
							calander.add(GregorianCalendar.DAY_OF_MONTH, totalNbDays);
							System.err.println(task.getName() + " diff hours : "+totalNbHours);
							calander.add(GregorianCalendar.HOUR,totalNbHours );
							System.err.println(task.getName() + " diff min : "+ totalNbMinutes);
							calander.add(GregorianCalendar.MINUTE,totalNbMinutes);
							tmpCalender = null;
						}else {
							calander.add(GregorianCalendar.YEAR, task.getNbYears());
							calander.add(GregorianCalendar.MONTH, task.getNbMonths());
							calander.add(GregorianCalendar.DAY_OF_MONTH, task.getNbDays());
							calander.add(GregorianCalendar.HOUR, task.getNbHours());
							calander.add(GregorianCalendar.MINUTE,task.getNbMinuites());
						}
					}
				}
			}
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
						boolean findedTask = false;
						for(Task finded : tasks) {
							if(finded.getId() == task.getId())
								findedTask = true;
							break;
						}
						if(!findedTask)
							tasks.add(task);
					}
				}
					
			}
		}
		return tasks;
	}
	
	public List<Task> getFinishedLateTasks(){
		List<Task> tasks = new ArrayList<>();
		for(Task task : getTasks()) {
			if(task.isFinishedLate() && !tasks.contains(task)
				&& !task.getStatus().toLowerCase().equals("canceled") && !task.getStatus().toLowerCase().equals("valid")) {
				tasks.add(task);
			}
		}
		return tasks;
	}
	
	public List<Task> getNoFinishedTasks(){
		List<Task> noFinishedTasks = new ArrayList<>();
		for(Task task : getTasks()) {
			if((task.getStartAt() != null || task.getStatus().toLowerCase().equals("started")) 
					&& task.getFinishAt() == null && !noFinishedTasks.contains(task)
				    && !task.getStatus().toLowerCase().equals("canceled")
				    && !task.getStatus().toLowerCase().equals("valid")) {
				noFinishedTasks.add(task);
			}
		}
		return noFinishedTasks;
	}
	
	public List<Task> getNoStartedTasks(){
		List<Task> noStartedTasks = new ArrayList<>();
		for(Task task : getTasks()) {
			if((task.getStartAt() == null || task.getStatus().toLowerCase().equals("valid")) && !noStartedTasks.contains(task)) {
				noStartedTasks.add(task);
			}
		}
		return noStartedTasks;
	}
}
