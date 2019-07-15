package sn.cperf.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tasks")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Task implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	@Column(length = 100)
	private String type;
	@Column(length = 100)
	private String status;
	private String lastStatus;
	private String description;
	private String fileDescriptionPath;
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "file_description_id")
	private DBFile dbFileDescription;
	@ManyToOne
	@JoinColumn(name = "parent_id")
	@JsonBackReference
	private Task parent;
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "parent")
	@JsonBackReference
	private List<Task> chirlds;
	@ManyToOne
	@JoinColumn(name = "section_id")
	@JsonBackReference
	private ProcessSection section;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id")
	@JsonBackReference
	private Group group;
	@Column(columnDefinition = "int(11) default 0")
	private int nbYears = 0;
	@Column(columnDefinition = "int(11) default 0")
	private int nbMonths = 0;
	@Column(columnDefinition = "int(11) default 0")
	private int nbDays = 0;
	@Column(columnDefinition = "int(11) default 1")
	private int nbHours = 1;
	@Column(columnDefinition = "int(11) default 0")
	private int nbMinuites = 0;
	@Column(columnDefinition = "boolean default true")
	private boolean maxTimeWithUnWorkTime = true;
	@Column(columnDefinition = "boolean default true")
	private boolean statusValid = true;
	@Column(columnDefinition = "boolean default true")
	private boolean lunchingByProcess = true;
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	private Date startAt;
	@Column(columnDefinition = "boolean default false")
	private boolean startConditional = false;
	@Column(columnDefinition = "boolean default true")
	private boolean yesCondition = true;
	private String textCondition;
	private Integer priorityLevel = 1;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "validator_id")
	@JsonManagedReference
	private User validator;
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "tasks_calendars", joinColumns = { @JoinColumn(name = "task_id") }, inverseJoinColumns = {
			@JoinColumn(name = "calendar_id") })
	@JsonManagedReference
	private List<WorkCalendar> daysWorkTimes;
	@ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	@JoinTable(name = "users_tasks", joinColumns = { @JoinColumn(name = "task_id") }, inverseJoinColumns = {
			@JoinColumn(name = "user_id") })
	@JsonManagedReference
	private List<User> users;
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "task")
	@JsonManagedReference
	private List<Justification> justifications;
	@OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
	@JsonBackReference
	private List<Objectif> objectifs;
	private Date finishAt;

	public List<User> getAllUsers() {
		List<User> allUsers = new ArrayList<User>();
		try {
			if (this.getUsers() != null) {
				for (User user : this.getUsers()) {
					if (!userFounded(allUsers, user))
						allUsers.add(user);
				}
			}
			if (this.getGroup() != null && this.getGroup().getUsers() != null) {
				for (User user : this.getGroup().getUsers()) {
					if (!userFounded(allUsers, user))
						allUsers.add(user);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return allUsers;
	}

	private boolean userFounded(List<User> users, User user) {
		if (!users.isEmpty() && user != null) {
			for (User u : users) {
				if (u.getId() == user.getId())
					return true;
			}
		}
		return false;
	}

	public boolean isProcessLunched() {
		try {
			return this.getSection().getProcess().getStartAt() != null;
		} catch (Exception e) {
		}
		return false;
	}

	public boolean isProcessExpired() {
		if (this.getSection() != null && this.getSection().getProcess() != null) {
			return this.getSection().getProcess().isExpired();
		}
		return false;
	}

	public Long getProcessId() {
		if (this.getSection() != null && this.getSection().getProcess() != null)
			return this.getSection().getProcess().getId();
		return null;
	}

	public String getProcessLabel() {
		if (this.getSection() != null && this.getSection().getProcess() != null)
			return this.getSection().getProcess().getLabel();
		return null;
	}

	public Date getMaxDate() {
		Calendar calendar = Calendar.getInstance();
		if (startAt != null) {
			calendar.setTime(startAt);
			calendar.add(GregorianCalendar.YEAR, Math.abs(nbYears));
			calendar.add(GregorianCalendar.MONTH, Math.abs(nbMonths));
			calendar.add(GregorianCalendar.DAY_OF_MONTH, Math.abs(nbDays));
			calendar.add(GregorianCalendar.HOUR, Math.abs(nbHours));
			calendar.add(GregorianCalendar.MINUTE, Math.abs(nbMinuites));
		} else {
			calendar.add(GregorianCalendar.YEAR, Math.abs(nbYears));
			calendar.add(GregorianCalendar.MONTH, Math.abs(nbMonths));
			calendar.add(GregorianCalendar.DAY_OF_MONTH, Math.abs(nbDays));
			calendar.add(GregorianCalendar.HOUR, Math.abs(nbHours));
			calendar.add(GregorianCalendar.MINUTE, Math.abs(nbMinuites));
		}
		if(maxTimeWithUnWorkTime)
			return addUnWorkTimeOnMaxDate(calendar.getTime());
		return calendar.getTime();
	}

	public boolean isFinishedLate() {
		if (finishAt != null) {
			Calendar calander = Calendar.getInstance();
			calander.setTime(getMaxDate());
			return finishAt.compareTo(getMaxDate()) > 0;
		}
		return false;
	}

	private Date addUnWorkTimeOnMaxDate(Date endDate) {
		// adding no wroks time on max date currentCalendar
		Date startDate = new Date();
		if (endDate != null && (endDate.compareTo(startDate)>=0)
				&& daysWorkTimes != null && !daysWorkTimes.isEmpty()) {
			int addMinutes = 0;
			Calendar startCalendar = Calendar.getInstance();
			startCalendar.setTime(startDate);
			Calendar endCalendar = Calendar.getInstance();
			endCalendar.setTime(endDate);
			Map<Integer, WorkCalendar> dataCalendars = new HashMap<>();
			daysWorkTimes.forEach(wc -> dataCalendars.put(wc.getDayIndex(), wc));
			while (endCalendar.getTime().compareTo(startCalendar.getTime()) >=0) {
				WorkCalendar wc = dataCalendars.get(startCalendar.get(Calendar.DAY_OF_WEEK));
					addMinutes = addMinutes + getUnWorkSumMinutes(wc,endDate);
				startCalendar.add(Calendar.DATE, 1);
			}
			endCalendar.set(Calendar.MINUTE, endCalendar.get(Calendar.MINUTE) + addMinutes);
			endDate = endCalendar.getTime();
		}
		return endDate;
	}

	private int getUnWorkSumMinutes(WorkCalendar wc,Date endDate) {
		int wcTotalMinutes = 0;
		if (wc != null) {
			if(wc.isFreeDay() || wc.getStartHour() == null) {
				wcTotalMinutes = wcTotalMinutes + (24*60);
			}else {
				Calendar  currentCalendar = Calendar.getInstance();
				Calendar  maxCalendar = Calendar.getInstance();
				maxCalendar.setTime(endDate);
				if(currentCalendar.get(Calendar.YEAR) == maxCalendar.get(Calendar.YEAR) 
					&& currentCalendar.get(Calendar.MONTH) == maxCalendar.get(Calendar.MONTH) && 
					currentCalendar.get(Calendar.DAY_OF_MONTH) == maxCalendar.get(Calendar.DAY_OF_MONTH)) {
					currentCalendar.add(Calendar.HOUR, wc.getWorkHours());
					currentCalendar.add(Calendar.MINUTE, wc.getWorkMinutes());
					if(currentCalendar.get(Calendar.HOUR) < maxCalendar.get(Calendar.HOUR)) {
						wcTotalMinutes = wcTotalMinutes + (wc.getPauseHours() * 60) + wc.getPauseMinutes();
					}
				}else {
					if (wc.getWorkHours() != null && wc.getWorkHours() > 0)
						wcTotalMinutes = wcTotalMinutes + Math.abs((24 * 60) - (wc.getWorkHours() * 60));
					if (wc.getWorkMinutes() != null && wc.getWorkMinutes() > 0)
						wcTotalMinutes = wcTotalMinutes - wc.getWorkMinutes();
					if (wc.getPauseHours() != null && wc.getPauseHours() > 0)
						wcTotalMinutes = wcTotalMinutes + (wc.getPauseHours() * 60);
					if (wc.getPauseMinutes() != null && wc.getPauseMinutes() > 0)
						wcTotalMinutes = wcTotalMinutes + wc.getWorkMinutes();
				}
			}
		}
		return wcTotalMinutes;
	}
}
