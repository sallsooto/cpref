package sn.cperf.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Option;

import sn.cperf.dao.FonctionRepository;
import sn.cperf.dao.UserRepository;
import sn.cperf.model.Fonction;
import sn.cperf.model.Indicateur;
import sn.cperf.model.Objectif;
import sn.cperf.model.User;
import sn.cperf.service.StorageService;
import sn.cperf.util.Organigramme;

@Controller
@RequestMapping("/Organigramme")
@Secured("ROLE_admin")
public class OrganigrammeController {
	@Autowired
	UserRepository userRepository;
	@Autowired
	StorageService storageService;
	@Autowired FonctionRepository fonctionRepository;

	@GetMapping("/")
	public String organigramme() {
		return "organigramme";
	}

	// getting hierarchy
	@GetMapping("/Hierarchy")
	@ResponseBody
	public List<Organigramme> getHierachy(HttpServletRequest request) {
		List<Organigramme> datas = new ArrayList<>();
		try {
			List<User> users = userRepository.findByUserSupIsNullAndOrganigrammeTrue();
			users.forEach(user -> {
				loadDatas(datas, user,request);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		addFictifOgranigramme(datas);
		return datas;
	}

	// isert hierarchy node
	@GetMapping("/Hierarchy/insert")
	@ResponseBody
	public Long updateHierarch(@RequestParam("pid") Long parentId, HttpServletRequest request) {
		ObjectMapper om = new ObjectMapper();
		try {
			User parentUser = null;
			Optional<User> optionalParentUser = null;
			if (parentId != null) {
				optionalParentUser = userRepository.findById(parentId);
				if (optionalParentUser.isPresent())
					parentUser = optionalParentUser.get();
				User user = new User();
				user.setUserSup(parentUser);
				try {
					user.setPassword(new BCryptPasswordEncoder().encode("passer"));
				} catch (Exception e) {
				}
				String username = "1";
				try {
					username = (userRepository.findByOrderByIdDesc().get(0).getId() + 1) + username;
				} catch (Exception e) {
				}
				user.setUsername("user"+username);
				user.setEmail("user"+username+"@cperf.org");
				user.setPhoto("user.png");
				try {
					if (userRepository.save(user) != null) {
						System.out.println(user.getId());
						return user.getId();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// updating hierarchy
	@PostMapping("/Hierarchy/update")
	@ResponseBody
	public void updateHierarch(@RequestBody String organigramString) {
		ObjectMapper om = new ObjectMapper();
		try {
			Organigramme org = om.readValue(organigramString, Organigramme.class);
			User parentUser = null;
			Optional<User> optionalUser = null;
			User user = null;
			System.out.println("id : " + org.getId());
			if (org.getId() != null) {
				optionalUser = userRepository.findById(org.getId());
			}
			if (optionalUser == null || !optionalUser.isPresent()) {
				user = new User();
				String username = "1";
				try {
					username = (userRepository.findByOrderByIdDesc().get(0).getId() + 1) + username;
				} catch (Exception e) {
				}
				user.setUsername(username);
				user.setPassword(new BCryptPasswordEncoder().encode("passer"));
			} else {
				user = optionalUser.get();
				user.setId(org.getId());
				user.setUsername(user.getUsername());
			}
			try {
				parentUser = userRepository.getOne(org.getParentId());
			} catch (Exception e) {
			}
			user.setLastname(org.getNom());
			user.setFirstname(org.getPrenom());
			user.setAdresse(org.getAdresse());
			user.setUserSup(parentUser);
			user.setPhone(org.getTelephone());
			user.setEmail(org.getEmail());
			if(org.getFonction() != null && !org.getFonction().equals("") && !org.getFonction().equals("null")
					&& !org.getFonction().toLowerCase().trim().equals("sans fonction")
					&& !org.getFonction().toLowerCase().trim().equals("fonction")){
				Fonction fonction = null;
				try {
					 fonction = fonctionRepository.findByNameIgnoreCase(org.getFonction());
				} catch (Exception e) {
				}
				if(fonction == null) {
					fonction = new Fonction();
					fonction.setName(org.getFonction());
				}
				try {
					user.setFonction(fonctionRepository.save(fonction));
				} catch (Exception e) {
					System.out.println("la la foncton");
				}
			}
			System.out.println("Fonction " + org.getFonction());
//			try {
//				user.setPhoto(org.getImage());
//				user.setPhoto((org.getImage().substring(org.getImage().lastIndexOf("/") + 1, org.getImage().length())));
//			} catch (Exception e) {
//				// e.printStackTrace();
//			}
			try {
				userRepository.save(user);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			System.err.println("c'est moi !");
			e.printStackTrace();
		}
	}



	@GetMapping("/ChangeOrgValue/")
	@ResponseBody
	public void changerUserOrgannigrammeValue(@RequestParam("uid") Long userId,
			@RequestParam(name = "stat", defaultValue = "false") boolean orgstatus, HttpServletRequest request) {
		try {
			List<Organigramme> orgs = new ArrayList<>();
			Optional<User> optInitUser = userRepository.findById(userId);
			if (optInitUser.isPresent()) {
				User initUser = optInitUser.get();
				try {
					if(!orgstatus) {
						User otherParentUser = null;
						Optional<User> optOtherParentUser = userRepository.findFirst1ByUserSupIsNullAndIdNotOrderById(initUser.getId());
						if(optOtherParentUser.isPresent()) {
						    otherParentUser = optOtherParentUser.get();
						    System.err.println(otherParentUser.getId());
						}
						// findin all chirlds if existings
						loadDatas(orgs, initUser,request);
						for (Organigramme org : orgs) {
							Optional<User> optUser = userRepository.findById(org.getId());
							if (optUser.isPresent()) {
								User user = optUser.get();
								try {
									if(user.getId() != initUser.getId())
										user.setUserSup(otherParentUser);
									else
										initUser.setOrganigramme(orgstatus);
									userRepository.save(user);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}else {
						try {
							initUser.setOrganigramme(orgstatus);
							userRepository.save(initUser);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@GetMapping("/Others/")
	public String others() {
		return "organigramme_others_users";
	}
	
	@GetMapping("/OthersJson/")
	@ResponseBody
	public Page<User> othersJson(@RequestParam(name="searchKey",defaultValue="") String searchKey,
			@RequestParam(name="page",defaultValue="0") int page,@RequestParam(name="size",defaultValue="10") int size, HttpServletRequest request) {
		if(searchKey != null && !searchKey.equals(""))
			return userRepository.searchOthersForOrganigramme(searchKey, new PageRequest(page, size));
		return userRepository.searchOthersForOrganigramme(new PageRequest(page, size));
	}

	private List<Organigramme> loadDatas(List<Organigramme> datas, User user, HttpServletRequest request) {
		try {
			if (user != null && user.isOrganigramme()) {
				datas.add(formateOraganigrammeData(user,request));
				if (!user.getUsers().isEmpty()) {
					user.getUsers().forEach(u -> {
						// datas.add(formateOraganigrammeData(u));
						loadDatas(datas, u,request);
					});
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return datas;
	}

	private Organigramme formateOraganigrammeData(User user, HttpServletRequest request) {
		Organigramme org = new Organigramme();
		org.setAdresse(user.getAdresse());
		org.setEmail(ralaceWhenNullOrBlank(user.getEmail(), "Sans Email", ""));
		org.setTelephone(ralaceWhenNullOrBlank(user.getPhone(), "Sans téléphone", ""));
		org.setId(user.getId());
		org.setParentId((user.getUserSup() != null) ? user.getUserSup().getId() : null);
		org.setPrenom(ralaceWhenNullOrBlank(user.getFirstname(), "Prenom", ""));
		org.setNom(ralaceWhenNullOrBlank(user.getLastname(), "Nom", ""));
		String photoUri = "http://" + request.getServerName() + ":"
				+ request.getServerPort() + request.getContextPath() + "/User/getUserPhoto/?uid="+user.getId();
		//org.setImage(StringUtils.cleanPath("/images/avatars/" + user.getPhoto()));
		org.setImage(photoUri);
		if(user.getFonction() != null) {
			org.setFonction(ralaceWhenNullOrBlank(user.getFonction().getName(), "Sans fonction", ""));
			List<Objectif> objectifs = user.getFonction().getObjectis();
			if(!objectifs.isEmpty()) {
				String objectifString = "";
				for(Objectif obj : objectifs) {
					String objString = obj.getName();
					if(!obj.getIndicators().isEmpty()) {
						objString = objString + " (";
						for(Indicateur indicator : obj.getIndicators()) {
							objString = objString + " "+indicator.getLabel() + " , ";
						}
						objString = objString + ")";
					}
					objString = objString + " ; ";
					objectifString = objectifString +objString;
				}
				org.setObjectif(objectifString);
			}else {
				org.setObjectif("Aucun objectif définit");
			}
		}
		else {
			org.setFonction(ralaceWhenNullOrBlank(null, "Sans fonction", ""));
			org.setObjectif("Sans objectif : définis d'abord sa fonction avant tout !");
		}
		// "/"+storageService.getDefaultDir()
		
		return org;
	}

	private String ralaceWhenNullOrBlank(String text, String text_repalce_whene_null, String text_replace_when_blanck) {
		if (text == null)
			return text_repalce_whene_null;
		if (text.equals(""))
			return text_replace_when_blanck;
		return text;
	}

	private void addFictifOgranigramme(List<Organigramme> datas) {
		try {
			List<User> parent_users = userRepository.findByUserSupIsNullAndOrganigrammeTrue();
			List<User> chirlds_users = userRepository.findByUserSupIsNotNullAndOrganigrammeTrue();
			if (!parent_users.isEmpty() && chirlds_users.isEmpty()) {
				Organigramme org = new Organigramme();
				org.setAdresse(null);
				org.setEmail("Email");
				org.setTelephone("Téléphone");
				org.setId(parent_users.get(parent_users.size() - 1).getId() + 1);
				org.setParentId(parent_users.get(0).getId());
				org.setPrenom("Prenom");
				org.setNom("Nom");
				org.setFonction("Fonction");
				datas.add(org);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@GetMapping("/getFonctionIdJson")
	@ResponseBody
	public Long getFonctionIdJson(@RequestParam(name="uid") Long userId) {
		try {
			User user = userRepository.getOne(userId);
			if(user != null && user.getFonction() != null)
				return user.getFonction().getId();
		} catch (Exception e) {
		}
		return null;
	}
}
