package sn.cperf.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="indicateurs")
@NoArgsConstructor @AllArgsConstructor
public class Indicateur implements Serializable{
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Getter @Setter
	private Long id;
	@Column(nullable=false)
	@Getter @Setter
	private String label;
	@Getter @Setter
	private String question;
	@Column(columnDefinition="boolean default true")
	@Getter @Setter
	private boolean questionResolvableByActor = true;
	@Getter @Setter
	private Double expectedNumberResult;
	@Getter @Setter
	private String expectedTextResult;
	@Column(length=25)
	@Getter @Setter
	private String expectedResultUnite;
	@ManyToOne
	@JoinColumn(name="type_id")
	@Getter @Setter
	private TypeIndicator type;
	@ManyToOne
	@JoinColumn(name="objectif_id")
	@JsonBackReference
	@Getter @Setter
	private Objectif objectif;
	@ManyToOne(cascade=CascadeType.PERSIST)
	@JoinColumn(name="parent_id")
	@JsonBackReference(value="parent-mov")
	@Getter @Setter
	private Indicateur parent;
	@OneToMany(fetch=FetchType.LAZY,mappedBy="parent",cascade=CascadeType.ALL)
	@JsonBackReference(value="chirlds-mov")
	@Getter @Setter
	private List<Indicateur> chirlds;
	@OneToMany(mappedBy="indicator")
	@JsonManagedReference
	@Getter @Setter
	private List<IndicatorDataCollector> dataCollectors;
	@Transient
	@Getter @Setter
	private boolean dataResultEditable = false;
	@Transient
	@Setter
	private Date  maxDataEditionDate;
	@Transient
	@Setter
	private Date  startDataEditionDate;
	
	
	public Date getMaxDataEditionDate() {
		if(isDataResultEditable()) {
			Date startDate = getStartDataEditionDate();
			if(startDate != null) {
				if(getObjectif() != null && getObjectif().getDeley() != null) {
					Calendar calander = Calendar.getInstance();
					calander.setTime(startDate);
					if(getType().getType().toLowerCase().contains("jour"))
						calander.set(Calendar.DAY_OF_MONTH, calander.get(Calendar.DAY_OF_MONTH) + getObjectif().getDeley());
					else if(getType().getType().toLowerCase().contains("moi"))
						calander.set(Calendar.MONTH, calander.get(Calendar.MONTH) + getObjectif().getDeley());
					else
						calander.set(Calendar.YEAR, calander.get(Calendar.YEAR) + getObjectif().getDeley());
					if(calander.getTime().compareTo(new Date()) <=0)
						return calander.getTime();
				}
			}
		}
		return new Date();
	}


	public Date getStartDataEditionDate() {
		if(isDataResultEditable()) {
			if(this.getDataCollectors() != null && !this.getDataCollectors().isEmpty()) {
				IndicatorDataCollector lastDataResult = this.getDataCollectors().get(this.getDataCollectors().size()-1);
				if(lastDataResult != null && lastDataResult.getMaxDate() != null) {
					return lastDataResult.getMaxDate();
				}
			}else {
				if(this.getObjectif() != null && this.getObjectif().getTask() != null) {
					Task task = this.getObjectif().getTask();
					if(task.getSection() != null && task.getSection().getProcess() != null) {
						Processus process = task.getSection().getProcess();
						return process.getStartAt();
					}
				}
			}
		}
		return null;
	}
	
}
