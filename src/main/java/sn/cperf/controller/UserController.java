package sn.cperf.controller;

import java.nio.file.Path;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import sn.cperf.dao.FonctionRepository;
import sn.cperf.dao.UserRepository;
import sn.cperf.form.ProfileForm;
import sn.cperf.model.DBFile;
import sn.cperf.model.User;
import sn.cperf.service.CperfService;
import sn.cperf.service.DBFileService;
import sn.cperf.service.StorageService;

@Controller
@RequestMapping("/User")
public class UserController {
	@Autowired
	UserRepository userRepository;
	@Autowired
	CperfService cperfService;
	@Autowired
	StorageService storageService;
	@Autowired
	FonctionRepository fonctionRepository;
	@Autowired
	DBFileService dbFileService;

	@GetMapping("/Profile")
	public String getProfileView(@RequestParam(name = "uid", defaultValue = "0") Long userId, Model model) {
		try {
			User loged = cperfService.getLoged();
			User user = new User();
			ProfileForm userForm = new ProfileForm();
			if (userId != null && userId > 0 && loged.hasRole("admin")) {
				Optional<User> optUser = userRepository.findById(userId);
				if (optUser.isPresent()) {
					user = optUser.get();
					userForm.setId(user.getId());
					userForm.setUsername(user.getUsername());
					userForm.setPassword(user.getPassword());
					userForm.setLastname(user.getLastname());
					userForm.setFirstname(user.getFirstname());
					userForm.setFonction(user.getFonction());
					userForm.setPhoto(user.getPhoto());
					userForm.setEmail(user.getEmail());
					userForm.setPhone(user.getPhone());
					userForm.setUserSup(user.getUserSup());
				}
			} else {
				userForm.setId(loged.getId());
				userForm.setUsername(loged.getUsername());
				userForm.setPassword(loged.getPassword());
				userForm.setLastname(loged.getLastname());
				userForm.setFirstname(loged.getFirstname());
				userForm.setFonction(loged.getFonction());
				userForm.setPhoto(loged.getPhoto());
				userForm.setEmail(loged.getEmail());
				userForm.setPhone(loged.getPhone());
				userForm.setUserSup(loged.getUserSup());
			}
			if (loged.hasRole("admin"))
				model.addAttribute("isAdmin", true);
			else
				model.addAttribute("isAdmin", false);
			model.addAttribute("user", userForm);
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addAttribute("fonctions", fonctionRepository.findAll(Sort.by(Order.desc("id"))));
		return "profile";
	}

	@PostMapping("/Profile")
	public String editProfile(@RequestParam("isAdmin") boolean isAdmin,
			@Valid @ModelAttribute("user") ProfileForm userForm, BindingResult bind, Model model) {
		model.addAttribute("isAdmin", isAdmin);
		try {
			if (bind.hasErrors()) {
				bind.getAllErrors().forEach(e -> {
					System.err.println(e.getDefaultMessage());
				});
			} else {
				User user = userRepository.getOne(userForm.getId());
				User userByEmail = userRepository.findByEmail(userForm.getEmail());
				User userByUsername = userRepository.findByUsername(userForm.getUsername());
				if (userByEmail == null || (userByEmail != null && user.getId() == userByEmail.getId())) {
					if (userByUsername == null || (userByUsername != null && user.getId() == userByUsername.getId())) {
						if (userForm.getFirstname() != null && !userForm.getFirstname().equals(""))
							user.setFirstname(userForm.getFirstname());
						if (userForm.getLastname() != null && !userForm.getLastname().equals(""))
							user.setLastname(userForm.getLastname());
						if (userForm.getEmail() != null && !userForm.getEmail().equals(""))
							user.setEmail(userForm.getEmail());
						if (userForm.getPhone() != null && !userForm.getPhone().equals(""))
							user.setPhone(userForm.getPhone());
						if (isAdmin) {
							if (userForm.getFonction() != null)
								user.setFonction(userForm.getFonction());
						}
						if(userForm.getUsername() != null && !userForm.getUsername().equals(""))
							user.setUsername(userForm.getUsername().trim());
						if(userForm.getPassword() != null && !userForm.getPassword().equals("")
							&& !userForm.getPassword().toLowerCase().trim().equals(user.getPassword().toLowerCase().trim())) {
							user.setPassword(new BCryptPasswordEncoder().encode(userForm.getPassword()));
						}
						if (userForm.getFile() != null) {
							if (dbFileService.checkExtensions(userForm.getFile().getOriginalFilename(),
									new String[] { "jpg", "jpeg", "png", "gif", "svg", "ico" })) {
								DBFile dbPhoto = null;
								try {
									dbPhoto = dbFileService
											.find(user.getDbPhoto() != null ? user.getDbPhoto().getId() : null);
								} catch (Exception e) {
								}
								user.setDbPhoto(dbFileService.storeOrUpdateFile(userForm.getFile(),
										dbPhoto != null ? dbPhoto.getId() : null,
										dbPhoto != null ? dbPhoto.isDefaultUserAvatar() : false));
								// storing on disk
								if (user.getDbPhoto() == null) {
									try {
										user.setPhoto(storageService.storeAvatar(userForm.getFile(),
												new String[] { "jpg", "jpeg", "png", "gif", "svg", "ico" }));
									} catch (Exception e1) {
										e1.printStackTrace();
									}
									if (user.getPhoto() == null || user.getPhoto().equals("")) {
										if (userForm.getPhoto() == null || userForm.getPhoto().equals(""))
											user.setPhoto("user.png");
										else
											user.setPhoto(userForm.getPhoto());
									}
								}
							}
						}
						userForm.setPhoto(user.getDbPhoto() != null ? user.getDbPhoto().getName() : user.getPhoto());
						if (userRepository.save(user) != null)
							model.addAttribute("successMsg", "Mise à jour du profile appliquée !");
						else
							model.addAttribute("errorMsg", "Mise à jour échouée, veuillez recommencer!");
					} else {
						model.addAttribute("errorMsg", "Vous ne pouvez pas utiliser ce nom d'utilisateur ou login !");
					}
				} else {
					model.addAttribute("errorMsg", "Vous ne pouvez pas utiliser cette adresse email !");
				}
			}
		} catch (Exception e) {
			model.addAttribute("errorMsg", "Opération échouée !");
			e.printStackTrace();
		}
		model.addAttribute("fonctions", fonctionRepository.findAll(Sort.by(Order.desc("id"))));
		return "profile";
	}

	@RequestMapping(value = "/getUserPhoto", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Resource> getUserPhoto(@RequestParam(name = "uid", defaultValue = "0") Long userId) {
		try {
			User user = new User();
			try {
				if (userId != null && userId > 0) {
					Optional<User> opU = userRepository.findById(userId);
					if (opU.isPresent())
						user = opU.get();
				} else {
					user = cperfService.getLoged();
				}
			} catch (Exception e) {
			}
			if (user.getDbPhoto() != null && user.getDbPhoto().getId() != null
					&& dbFileService.find(user.getDbPhoto().getId()) != null) {
				return dbFileService.downloadImageFile(user.getDbPhoto());
			} else if (dbFileService.getDefaultUserAvatar() != null) {
				return dbFileService.downloadImageFile(dbFileService.getDefaultUserAvatar());
			} else {
				String fileName = (user != null && user.getPhoto() != null) ? user.getPhoto() : "user.png";
				Path file = storageService.getResolveFilePathWithEnDirConfig("spring.file.avatar-dir", fileName);
				System.out.println(file);
				Resource resource = new UrlResource(file.toUri());
				if (resource.exists() || resource.isReadable()) {
					return ResponseEntity.ok().body(resource);
				} else {
					System.out.println("no file");
				}
			}
		} catch (Exception e) {
			System.err.println("monn erreur");
			System.out.println(e);
		}
		return null;
	}

	@RequestMapping(value = "/getDefaultUserPhoto", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Resource> getDefaultUserPhoto() {
		try {
			if (dbFileService.getDefaultUserAvatar() != null) {
				return dbFileService.downloadImageFile(dbFileService.getDefaultUserAvatar());
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}
}
