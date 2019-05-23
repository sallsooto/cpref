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
@Table(name="types_indicateurs")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TypeIndicator implements Serializable{
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	@Column(nullable=false, length=100,unique=true)
	private String type;
	@Column(columnDefinition="boolean default true")
	private boolean withExpectedNumberResult = true;
	@Column(columnDefinition="boolean default true")
	private boolean valid = true;
}
