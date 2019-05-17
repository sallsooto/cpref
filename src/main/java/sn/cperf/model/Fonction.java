package sn.cperf.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Table(name="fonctions")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class Fonction implements Serializable{
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	@Column(length=100, unique=true, nullable=false)
	private String name;
	private String description;
	@OneToMany(mappedBy="fonction", fetch=FetchType.LAZY)
	@JsonBackReference
	private List<User> users;
	@OneToMany(mappedBy="fonction",fetch=FetchType.LAZY)
	@JsonManagedReference
	private List<Objectif> objectis;
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.name;
	}
}
