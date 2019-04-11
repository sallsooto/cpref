package sn.cperf.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table(name="notifications")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class Notification implements Serializable{
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	private String title;
	@Column(nullable=false)
	private String note;
	private String type;
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
	@Temporal(TemporalType.TIME)
	private Date storeAs;
	@Column(columnDefinition="boolean default false")
	private boolean seen=false;
	private String link;
	@ManyToOne
	@JoinColumn(name="target_id")
	@JsonManagedReference
	private User target;
	@ManyToOne
	@JoinColumn(name="sender_id")
	private User sender;
}
