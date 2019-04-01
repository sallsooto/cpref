package sn.cperf.controller;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import sn.cperf.dao.PasswordRepository;
import sn.cperf.dao.UserRepository;
import sn.cperf.form.PasswordForm;
import sn.cperf.form.UserForm;
import sn.cperf.model.Password;
import sn.cperf.model.User;
import sn.cperf.service.MailService;
import sn.cperf.service.StorageService;

@Controller
public class PageController {
	@Autowired UserRepository userRepository;
	@Autowired StorageService storageService;
	@Autowired PasswordRepository passwordRepository;
	@Autowired MailService mailService;
	
	@GetMapping("/")
	public String index() {
		return "index";
	}
	@GetMapping("/403")
	public String error403() {
		return "error/403";
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	@GetMapping("/register")
	public String getRegisterView(Model model) {
		model.addAttribute("user", new UserForm());
		return "register";
	}
	
	@PostMapping("/register")
	public String register(@Valid @ModelAttribute("user") UserForm form, BindingResult bind,Model model,HttpServletRequest request) {
		if(bind.hasErrors()) {
			bind.getAllErrors().forEach(err ->{
				System.out.println(err.getDefaultMessage());
			});
		}else {
			User user = null;
			try { user = userRepository.findByEmail(form.getEmail());} catch (Exception e) {}
			if(user == null) {
				try { user = userRepository.findByUsername(form.getUsername());} catch (Exception e) {}
				if(user == null) {
					user = new User();
					user.setAdresse(form.getAdresse());
					user.setEmail(form.getEmail());
					user.setUsername(form.getUsername());
					user.setFirstname(form.getFirstname());
					user.setLastname(form.getLastname());
					user.setPhone(form.getPhone());
					user.setPassword(new BCryptPasswordEncoder().encode(form.getPassword()));
					if(form.getPhoto() != null) {
						try {user.setPhoto(storageService.storeAvatar(form.getPhoto()));} catch (Exception e1) {e1.printStackTrace();}
						if(user.getPhoto()== null || user.getPhoto().equals(""))
							user.setPhoto("user.png");
					}
					try {
						// ajout
						if (userRepository.save(user) != null) {
							model.addAttribute("successMsg", "Compte créé, un email vous sera envoyé après sa validation sous les 48h!");
							try {
								final String attachementUri = "http://" + request.getServerName() + ":"
										+ request.getServerPort() + request.getContextPath() + "/Admin/User";
								mailService.sendHtmlMailForAdminOnCreatedUserAccount(attachementUri,
										(user.getFirstname() + " " + user.getLastname()));
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							model.addAttribute("errorMsg", "Inscription échouée, veuillez recommencez !");
						}
						// fin ajout
					} catch (Exception e) {
						model.addAttribute("errorMsg", "opération échouée, veuillez recommencez !");
					}
				}else {
					model.addAttribute("errorMsg", "Utilisez un autre login");
				}
			}else {
				model.addAttribute("errorMsg", "Adrèsse email déjà utilisé!");
			}
		}
		return "register";
	}

	@GetMapping("/resetPassword/{token}")
	public String getResetPasswordMailVeiw(@PathVariable("token") String token, Model model,
			@RequestParam(name="notFoudEmail", defaultValue="false") boolean notFoudEmail,
			@RequestParam(name="msg", defaultValue="") String msg,@RequestParam(name="error", defaultValue="") String error) {
		PasswordForm form = new PasswordForm();
		if (!token.toLowerCase().equals("email")) {
			Password password = passwordRepository.findByToken(token);
			if (password != null)
				form.setEmail(password.getEmail());
		}
		if(msg.equals(""))
			msg = "Rénitialisation du mot de passe";
		model.addAttribute("userEmail", form.getEmail());
		model.addAttribute("notFoudEmail", notFoudEmail);
		model.addAttribute("msg", msg);
		model.addAttribute("error", error);
		model.addAttribute("passwordForm", form);
		model.addAttribute("token", token);
		return "password";
	}
	@PostMapping("/resetPassword/{token}")
	public String resetPassword(@PathVariable("token") String token,@Valid PasswordForm form, HttpServletRequest request) {
		try {
			if(form.getEmail() == null ||form.getEmail().equals("")) {
				return "redirect:/resetPassword/email";
			}else {
				User user = userRepository.findByEmail(form.getEmail());
				if(user == null || !user.isValid()) {
					return "redirect:/resetPassword/email?notFoudEmail="+true;
				}else {
					Password pass = passwordRepository.findByEmail(user.getEmail());
					if(pass == null) {
						pass = new Password();
						pass.setEmail(user.getEmail());
						token = UUID.randomUUID().toString();
						pass.setToken(token);
						pass = passwordRepository.save(pass);
						if(pass != null) {
							String resetUrl = "http://" + request.getServerName() + ":" + request.getServerPort() 
												+ request.getContextPath() + "/resetPassword/"+token;
							String message = "Vous avez lancé une opération de rénitialisation du mot de passe de votre compte "
									+ "C'perf, <a href='"+resetUrl+"'> Cliquez ici pour terminer pour poursuivre l'opération</a>";
							mailService.sendHtmlTextMail(pass.getEmail(), "C'perf Réinitialisation du mot de passe", message);
							String msg = "Veuillez consulter votre adresse email, nous vous avons envoyé un lien de validation";
							return "redirect:/resetPassword/email?msg="+msg;
						}
					}else{
						System.out.println(form.getPassword() + "  " + form.getPasswordConfirm());
						if(form.getPassword() == null || form.getPasswordConfirm() == null) {
							passwordRepository.delete(pass);
							String error = "Vous n'avez pas cliqué sur le bon lien dans votre courier, veuillez rcommencer !";
							return "redirect:/resetPassword/email?error="+error;
						}else if(form.getPassword().equals("") || form.getPasswordConfirm().equals("")
								|| !form.getPasswordConfirm().equals(form.getPassword())) {
							String error = "Assurez vous d'avoir saisi deux mot de passes identiques";
							return "redirect:/resetPassword/"+token+"?error="+error;
						}else {
							user.setPassword(new BCryptPasswordEncoder().encode(form.getPassword()));
							userRepository.save(user);
							passwordRepository.delete(pass);
							return "redirect:/";
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "password";
	}
}
