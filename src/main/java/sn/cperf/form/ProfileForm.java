package sn.cperf.form;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sn.cperf.model.User;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class ProfileForm {
	private Long id;
	private String firstname;
	private String lastname;
	private String username;
	private String password;
	private String email;
	private String adresse;
	private String phone;
	private String fonction;
	private String objectif;
	private String activite;
	private String photo;
	private User userSup;
	private MultipartFile file;
}
