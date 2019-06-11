package sn.cperf.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Table(name="files")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class DBFile implements Serializable{
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	@Column(nullable=false)
	private String name;
	@Column(nullable=false)
	private String type;
	@Lob
	@Column(nullable=false)
	private byte[] data;
	@Column(columnDefinition="boolean default false")
	private boolean defaultUserAvatar = false;
}
