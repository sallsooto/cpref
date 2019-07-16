package sn.cperf.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="calendar")
@NoArgsConstructor @AllArgsConstructor
@Setter
public class WorkCalendar implements Serializable{
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	@Column(nullable=false, unique=true)
	private Integer dayIndex;
	@Column(length=50)
	private String dayName;
	private Integer startHour;
	private Integer startMinutes;
	private Integer workHours;
	private Integer workMinutes;
	private Integer pauseHours;
	private Integer pauseMinutes;
	public Long getId() {
		return id;
	}
	public Integer getDayIndex() {
		return dayIndex;
	}
	public String getDayName() {
		return dayName;
	}
	public Integer getStartHour() {
		
		return startHour;
	}
	public Integer getStartMinutes() {
		return startMinutes;
	}
	public Integer getWorkHours() {
		return workHours != null ? workHours : 0;
	}
	public Integer getWorkMinutes() {
		return workMinutes != null ? workMinutes : 0;
	}
	public Integer getPauseHours() {
		return pauseHours != null ? pauseHours : 0;
	}
	public Integer getPauseMinutes() {
		return pauseMinutes != null ? pauseMinutes : 0;
	}
}
