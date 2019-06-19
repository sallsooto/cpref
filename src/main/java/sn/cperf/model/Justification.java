package sn.cperf.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Table(name="justifications")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class Justification implements Serializable{
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	@Column(length=100, nullable=false)
	private String cause;
	@Column(nullable=false)
	private String content;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="impact_id")
	@JsonManagedReference
	private Impact impact;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="process_id")
	@JsonBackReference
	private Processus process;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="task_id")
	@JsonBackReference
	private Task task;
	public String getTranslatedCause() {
		String translated = "";
		if(cause != null) {
			if(cause.toLowerCase().trim().equals("finished_expired"))
				translated = "Process traité tardivement";
			else if(cause.toLowerCase().trim().equals("unfinished_expired"))
				translated = "Process non traité et delaie expiré";
			else if(cause.toLowerCase().trim().equals("process_canceld") || cause.toLowerCase().trim().equals("canceled"))
				translated = "Process annulé";
			else if(cause.toLowerCase().trim().equals("unlunched"))
				translated = "Process non démarré";
			else if(cause.toLowerCase().trim().equals("t_canceled"))
				translated = "Tâche annulée";
			else if(cause.toLowerCase().trim().equals("completed"))
				translated = "Tâche traité"; 
			else if(cause.toLowerCase().trim().equals("t_finishedlate"))
				translated = "Tâche finie tardivement";
			else if(cause.toLowerCase().trim().equals("t_unfinished"))
				translated = "Tâche démarrée et non términée";
			else if(cause.toLowerCase().trim().equals("t_unlunched") || cause.toLowerCase().trim().equals("valid"))
				translated = "Tâche non démarrée";    
			else
				translated = cause;
		}
		return translated;
	}
}
