package sn.cperf.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="groupes")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class Group implements Serializable{
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	@Column(length=100,unique=true,nullable=false)
	private String name;
	@ManyToOne
	@JoinColumn(name="role_id")
	private Role role;
	@ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
	@JoinTable(name="groupes_users",
	joinColumns= {@JoinColumn(name="groupe_id")},
	inverseJoinColumns= {@JoinColumn(name="user_id")})
	@JsonBackReference
	private List<User> users;
}
