package sn.cperf.form;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sn.cperf.model.Role;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class UserForm {
	private Long id;
	private String firstname;
	private String lastname;
	private String username;
	private String password;
	private String email;
	private String adresse;
	private String phone;
	private MultipartFile photo;
	private boolean valid = false;
	private List<Role> roles;
}
