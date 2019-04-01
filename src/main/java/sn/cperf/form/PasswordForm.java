package sn.cperf.form;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Data
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class PasswordForm {
	private String email;
	private String password;
	private String passwordConfirm;
}