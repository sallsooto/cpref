package sn.cperf.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class Organigramme {
	private Long id;
	private Long parentId; 
	private String Prenom;
	private String Nom;
	private String Fonction;
	private String Telephone;
	private String Email;
	private String Adresse;
	private String image;
	private String objectif;
}
