package sn.cperf.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="objectifs")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class Objectif implements Serializable{
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	@Column(nullable=false)
	private String name;
	@ManyToOne
	@JoinColumn(name="fonction_id")
	@JsonManagedReference
	private Fonction fonction;
	@ManyToOne
	@JoinColumn(name="type_id")
	@JsonManagedReference(value="objectif-type-mov")
	private TypeObjectif type;
	@ManyToOne
	@JoinColumn(name="group_id")
	@JsonManagedReference
	private Group group;
	private Integer deley;
	@ManyToOne
	@JoinColumn(name="task_id")
	@JsonManagedReference(value="objectif-task-mov")
	@JsonIgnoreProperties({"allUsers","users"})
	private Task task;
	@OneToMany(fetch=FetchType.LAZY, mappedBy="objectif")
	@JsonManagedReference(value="objectif-indicators-mov")
	private List<Indicateur> indicators;
	
	public double getPerformPercente() {
		double perform = 0, sumParentIndicator = 0;
		try {
			for(Indicateur indicator : indicators) {
				// check if is a parent indicator
				if(indicator.isDataResultEditable()) { 
					perform = perform + indicator.getPerformancePurcente();
					sumParentIndicator++;
				}
			}
			if(perform > 0 && sumParentIndicator > 0)
				perform = perform / sumParentIndicator;
			// formattage du nombre 
			BigDecimal bdec, bdec2;
			bdec = new BigDecimal(Double.toString(perform));
			bdec2 = bdec.setScale(2,RoundingMode.FLOOR);
			perform = bdec2.doubleValue();
		} catch (Exception e) {
			System.out.println("cest ici l'exception");
			e.printStackTrace();
		}
		return perform;
	}
}
