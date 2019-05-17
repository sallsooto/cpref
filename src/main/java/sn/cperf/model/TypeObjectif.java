package sn.cperf.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="types_objectifs")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class TypeObjectif implements Serializable{
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	@Column(nullable=false, length=50, unique=true)
	private String slug;
	@Column(nullable=false)
	private String description;
	private String unite;
	@Column(columnDefinition="boolean default true")
	private boolean valid=true;
	@OneToMany(mappedBy="type")
	@JsonBackReference
	private List<Objectif> objectifs;
	
}
