package sn.cperf.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import sn.cperf.dao.UserRepository;
import sn.cperf.form.ProfileForm;
import sn.cperf.model.User;
import sn.cperf.service.CperfService;
import sn.cperf.service.StorageService;

@Controller
@RequestMapping("/User")
public class UserController {
	@Autowired UserRepository userRepository;
	@Autowired CperfService cperfService;
	@Autowired StorageService storageService;
	
	@GetMapping("/Profile")
	public String getProfileView(@RequestParam(name="uid", defaultValue="0") Long userId, Model model) {
		try {
			User loged = cperfService.getLoged();
			User user = new User();
			ProfileForm userForm = new ProfileForm();
			if(userId != null && userId >0 && loged.hasRole("admin")){
				Optional<User> optUser = userRepository.findById(userId);
				if(optUser.isPresent()) {
					user = optUser.get();
					userForm.setId(user.getId());
					userForm.setLastname(user.getLastname());
					userForm.setFirstname(user.getFirstname());
					userForm.setActivite(user.getActivite());
					userForm.setFonction(user.getFonction());
					userForm.setPhoto(user.getPhoto());
					userForm.setObjectif(user.getObjectif());
					userForm.setEmail(user.getEmail());
					userForm.setPhone(user.getPhone());
					userForm.setUserSup(user.getUserSup());
				}
			}else {
				userForm.setId(loged.getId());
				userForm.setLastname(loged.getLastname());
				userForm.setFirstname(loged.getFirstname());
				userForm.setActivite(loged.getActivite());
				userForm.setFonction(loged.getFonction());
				userForm.setPhoto(loged.getPhoto());
				userForm.setObjectif(loged.getObjectif());
				userForm.setEmail(loged.getEmail());
				userForm.setPhone(loged.getPhone());
				userForm.setUserSup(loged.getUserSup());
			}
			if(loged.hasRole("admin"))
				model.addAttribute("isAdmin",true);
			else
				model.addAttribute("isAdmin",false);
		    model.addAttribute("user",userForm);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "profile";
	}
	
	@PostMapping("/Profile")
	public String editProfile(@RequestParam("isAdmin") boolean isAdmin, @Valid @ModelAttribute("user") ProfileForm userForm,
							BindingResult bind,Model model) {
		model.addAttribute("isAdmin", isAdmin);
		try {
			if(bind.hasErrors()) {
				bind.getAllErrors().forEach(e->{
					System.err.println(e.getDefaultMessage());
				});
			}else {
				User user = userRepository.getOne(userForm.getId());
				User userByEmail = userRepository.findByEmail(userForm.getEmail());
				if(userByEmail == null || (userByEmail != null && user.getId() == userByEmail.getId())) {
					if(userForm.getFirstname() != null && !userForm.getFirstname().equals(""))
						user.setFirstname(userForm.getFirstname());
					if(userForm.getLastname() != null && !userForm.getLastname().equals(""))
						user.setLastname(userForm.getLastname());
					if(userForm.getEmail() != null && !userForm.getEmail().equals(""))
						user.setEmail(userForm.getEmail());
					if(userForm.getPhone() != null && !userForm.getPhone().equals(""))
						user.setPhone(userForm.getPhone());
					if(isAdmin) {
						if(userForm.getFonction() != null)
							user.setFonction(userForm.getFonction());
						if(userForm.getActivite() != null)
							user.setActivite(userForm.getActivite());
						if(userForm.getObjectif() != null)
							user.setObjectif(userForm.getObjectif());
					}
					if(userForm.getFile() != null) {
						try {user.setPhoto(storageService.storeAvatar(userForm.getFile(),new String[] { "jpg", "jpeg", "png", "gif", "svg","ico" }));} catch (Exception e1) {e1.printStackTrace();}
						if(user.getPhoto()== null || user.getPhoto().equals("")){
							if(userForm.getPhoto() == null || userForm.getPhoto().equals(""))
								user.setPhoto("user.png");
							else
								user.setPhoto(userForm.getPhoto());
						}
					}
					userForm.setPhoto(user.getPhoto());
					if(userRepository.save(user) != null)
						model.addAttribute("successMsg", "Mise à jour du profile appliquée !");
					else
						model.addAttribute("errorMsg", "Mise à jour échouée, veuillez recommencer!");
				}else {
					model.addAttribute("errorMsg", "Vous ne pouvez pas utiliser cette adresse email");
				}
			}
		} catch (Exception e) {
			model.addAttribute("errorMsg", "Opération échouée !");
			e.printStackTrace();
		}
		return "profile";
	}
}
