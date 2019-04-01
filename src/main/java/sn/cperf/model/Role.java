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
@Table(name="roles")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class Role implements Serializable {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	@Column(length=50, unique=true, nullable=false)
	private String role;
	private String description;
}
