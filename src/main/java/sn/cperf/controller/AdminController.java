package sn.cperf.controller;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import sn.cperf.dao.GroupRepository;
import sn.cperf.dao.NotificationRepository;
import sn.cperf.dao.RoleRepository;
import sn.cperf.dao.UserRepository;
import sn.cperf.model.Group;
import sn.cperf.model.Role;
import sn.cperf.model.User;

@Controller
@RequestMapping("/Admin")
@Secured("ROLE_admin")
public class AdminController {
	@Autowired RoleRepository roleRepository;
	@Autowired GroupRepository groupRepository;
	@Autowired UserRepository userRepository;
	@Autowired NotificationRepository notificationRepository;
	
	@GetMapping("/Role")
	public String getRoleView(@RequestParam(name="id", defaultValue="0") Long id, Model model) {
		try {
			Role role = new Role();
			if(id != null && id >0) {
				Optional<Role> optRole = roleRepository.findById(id);
				if(optRole.isPresent())
					role = optRole.get();
			}
			model.addAttribute("form", role);
			model.addAttribute("roles", roleRepository.findByOrderByIdDesc());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "role";
	}
	
	@PostMapping("/Role")
	public String editRole(@Valid @ModelAttribute("form") Role form, BindingResult bind, Model model) {
		try {
			String errorMsg = "";
			if(bind.hasErrors()) {
				bind.getAllErrors().forEach(e->{
					System.out.println(e.getDefaultMessage());
				});
				errorMsg="Ce formulaire a des érreurs";
			}else {
				Role role = new Role();
				if(form.getId() != null) {
					role = roleRepository.getOne(form.getId());
					role.setDescription(form.getDescription());
				}else {
					if(roleRepository.findByRole(form.getRole()) != null) {
						errorMsg ="Ce droit existe déjà!";
					}else {
						role.setDescription(form.getDescription());
						role.setRole(form.getRole());
					}
				}
				if(errorMsg.equals("")) {
					if(roleRepository.save(role) != null) {
						if(form.getId() != null)
							model.addAttribute("successMsg", "Profile modifié!");
						else
							model.addAttribute("successMsg", "Profile enregistré!");	
					}else {
						model.addAttribute("errorMsg", "profile non édité, veuillez recommencer!");
					}
				}else {
					model.addAttribute("errorMsg", errorMsg);
				}
			}
		} catch (Exception e) {
			model.addAttribute("errorMsg", "Echèc de l'opération, veuillez recommencer!");
			e.printStackTrace();
		}
		model.addAttribute("roles", roleRepository.findByOrderByIdDesc());
		return "role";
	}
	
	@GetMapping("/Group")
	public String getUserGroupView(@RequestParam(name="id", defaultValue="0") Long id, Model model) {
		Group group = new Group();
		if(id != null && id >0) {
			Optional<Group> optgroup = groupRepository.findById(id);
			if(optgroup.isPresent())
				group = optgroup.get();
		}
		model.addAttribute("form", group);
		model.addAttribute("roles", roleRepository.findAll());
		model.addAttribute("users", userRepository.findAll());
		model.addAttribute("groups", groupRepository.findAll());
		return "group";
	}
	
	@PostMapping("/Group")
	public String editGroup(@Valid @ModelAttribute("form") Group group, BindingResult bind,Model model) {	
		try {
			if(bind.hasErrors()) {
				bind.getAllErrors().forEach(e->{System.err.println(e.getDefaultMessage());});
			}else {
				boolean isUpdateOperation = (group.getId() != null && group.getId()>0) ? true : false;
				if(groupRepository.save(group) !=  null) {
					if(isUpdateOperation)
						model.addAttribute("successMsg", "Groupe modifié !");
					else
						model.addAttribute("successMsg", "Groupe enregistré");
				}else {
					model.addAttribute("errorMsg", "Données non enregistrées, veuillez recommencer !");
				}
			}
		} catch (Exception e) {
			model.addAttribute("errorMsg", "Opération échouée !");
			e.printStackTrace();
		}
		model.addAttribute("users", userRepository.findAll());
		model.addAttribute("roles", roleRepository.findAll());
		model.addAttribute("groups", groupRepository.findAll());
		return "group";
	}
	
	@GetMapping("/User")
	public String getUserListView() {
		return "users";
	}
	
	// gets users in json datas
	@GetMapping("/getUsersJson")
	@ResponseBody
	public Page<User> getUsersJson(@RequestParam(name="searchKey",defaultValue="") String searchKey,
			@RequestParam(name="page",defaultValue="0") int page,@RequestParam(name="size",defaultValue="5") int size) {
		if(searchKey != null && !searchKey.equals(""))
			return userRepository.findByFirstnameLikeOrLastnameLikeIgnoreCase("%"+searchKey+"%","%"+searchKey+"%", new PageRequest(page, size));
		return userRepository.findAll(new PageRequest(page, size));
	}
	
	@GetMapping("/User/Statut")
	@ResponseBody
	public boolean changeUserStatus(@RequestParam("userId") Long userId, @RequestParam("status") boolean status,
			HttpServletRequest request) {
		try {
			User user = userRepository.getOne(userId);
			if (user != null) {
				user.setValid(status);
			}
			if (userRepository.save(user) != null) {
//				String history_event = "";
//				if (status) {
//					final String appLoginUri = "http://" + request.getServerName() + ":" + request.getServerPort()
//							+ request.getContextPath() + "/login";
//					mailService.sendUserMailWheneHirAcountIsActived(appLoginUri, user.getNomComplet(),
//							user.getUsername());
//					 history_event = "Activation du compte de l'utilisateur : "+user.getNomComplet();
//				}else {
//					 history_event = "Désactivation du compte de l'utilisateur : "+user.getNomComplet();
//				}
//				getransService.storeHistory(history_event, "/Admin/User");
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@GetMapping("/User/Edit/")
	public String getUserEditView(@RequestParam("uid") Long id, Model model) {
		User user = null;
		try {
			user = userRepository.getOne(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addAttribute("roles", roleRepository.findAll());
		model.addAttribute("groups", groupRepository.findAll());
		model.addAttribute("user", user);
		return "user";
	}
	
	@PostMapping("/User/Edit/")
	public String editUser(@RequestParam("uid") Long id, @Valid @ModelAttribute("user") User userForm, Model model) {
		try {
			if(userForm.getId() != null && userForm.getId()>0) {
				Optional<User> optUser = userRepository.findById(userForm.getId());
				if(optUser.isPresent()) {
					User user = optUser.get();
					if(userForm.getRoles() != null)
						user.setRoles(userForm.getRoles());
//					if(userForm.getGroupes() != null)
//						user.setGroupes(userForm.getGroupes());
					if(userRepository.save(user) != null)
						model.addAttribute("successMsg", "Mise à jour appliquée !");
					else
						model.addAttribute("errorMsg", "Mise à jour non appliquée, veuillez recommencer !");
				}else {
					model.addAttribute("errorMsg", "Impossible de retrouver l'utilisateur !");
				}
			}
		} catch (Exception e) {
			model.addAttribute("errorMsg", "Mise à jour intérrompue !");
			e.printStackTrace();
		}
		model.addAttribute("roles", roleRepository.findAll());
		model.addAttribute("groups", groupRepository.findAll());
		return "user";
	}
}
