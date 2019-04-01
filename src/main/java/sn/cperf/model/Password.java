package sn.cperf.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="passwords")
@Data
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class Password implements Serializable{
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	@Column(unique=true, nullable=false, length=30)
	private String email;
	@Column(nullable=false)
	private String token;
}
