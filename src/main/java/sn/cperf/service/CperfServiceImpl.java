package sn.cperf.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import sn.cperf.dao.ImpactRepository;
import sn.cperf.dao.ParamRepository;
import sn.cperf.dao.ProcessSectionRepository;
import sn.cperf.dao.RoleRepository;
import sn.cperf.dao.TypeIndicatorRepository;
import sn.cperf.dao.TypeObjectifRepository;
import sn.cperf.dao.UserRepository;
import sn.cperf.model.Impact;
import sn.cperf.model.Parametre;
import sn.cperf.model.Role;
import sn.cperf.model.TypeIndicator;
import sn.cperf.model.TypeObjectif;
import sn.cperf.model.User;

@Service
public class CperfServiceImpl implements CperfService {

	@Autowired
	UserRepository userRepository;
	@Autowired
	StorageService storageService;
	@Autowired
	ImpactRepository impactRepository;
	@Autowired
	TypeObjectifRepository typeObjectifRepository;
	@Autowired
	TypeIndicatorRepository typeIndicatorRepository;
	@Autowired
	ParamRepository paramrepository;
	@Autowired
	ProcessSectionRepository processSectionRepository;
	@Autowired
	RoleRepository roleRepository;

	@Override
	public User getLoged() {
		try {
			String username = SecurityContextHolder.getContext().getAuthentication().getName();
			return userRepository.findByUsername(username);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void doAllNecessaryOperationsAfterLunchApplication() {
		// create all neccessary directory on disk
		try {
			storageService.createDirectoryByEnvironnementConfig("spring.file.upload-dir");
			storageService.createDirectoryByEnvironnementConfig("spring.file.img-dir");
			storageService.createDirectoryByEnvironnementConfig("spring.file.avatar-dir");
			// store impact
			sotreImpact("time", "Temporel");
			sotreImpact("finacier", "Financière");
			sotreImpact("material", "Matériel");
			sotreImpact("personnel", "Personnel");
			// store type object
			storeObjectifType("regulary", "Régulier", "mois", true);
			storeObjectifType("ponctual", "Ponctuel", null, false);
			storeObjectifType("short_term", "A court terme", "mois", true);
			storeObjectifType("short_term_trimestriel", "A court terme trimestriel", null, true);
			storeObjectifType("long_term", "A long terme", "année(s)", true);
			// store type indicator
			storeIndicatorType("Quantitaif", true);
			storeIndicatorType("Qualitatif", false);

			// store parametres
			storeParametre("admin_email", "mohamed.ali@snsoftware.com", "Email adminstrateur");
			// storing roles
			storeRole("admin", "Administrateur");
			storeRole("procedure_explorer", "Exporateur de procéduire");
			// enable one user and set him on admin
			enableOneUser();
		} catch (Exception e) {
		}

		// sotre all
	}

//	@Override
//	public boolean userHasRole(User user, String role) {
//		for(GrantedAuthority auth : user.getAuthorities()) {
//			if(auth.getAuthority().equals(role) || auth.getAuthority().equals(role.toUpperCase())
//			   || auth.getAuthority().equals("ROLE_"+role) || auth.getAuthority().equals(("ROLE_"+role).toUpperCase()))
//				return true;
//		}
//		return false;
//	}

	private void sotreImpact(String slug, String name) {
		Impact impact = null;
		try {
			impact = impactRepository.findBySlug(slug);
		} catch (Exception e) {
		}
		if (impact == null) {
			impact = new Impact();
			impact.setSlug(slug);
			impact.setName(name);
			try {
				impactRepository.saveAndFlush(impact);
			} catch (Exception e) {
			}
		}
	}

	// store objectifType
	private void storeObjectifType(String slug, String description, String unite, boolean valid) {
		TypeObjectif tObj = null;
		try {
			tObj = typeObjectifRepository.findBySlug(slug);
		} catch (Exception e) {
		}
		if (tObj == null) {
			tObj = new TypeObjectif();
			tObj.setSlug(slug);
			tObj.setDescription(description);
			tObj.setUnite(unite);
			tObj.setValid(valid);
			try {
				typeObjectifRepository.saveAndFlush(tObj);
			} catch (Exception e) {
			}
		}
	}

	// store indicatorType
	private void storeIndicatorType(String type, boolean withExpectedNumberResult) {
		TypeIndicator tIndicator = null;
		try {
			tIndicator = typeIndicatorRepository.findByTypeIgnoreCase(type);
		} catch (Exception e) {
		}
		if (tIndicator == null) {
			tIndicator = new TypeIndicator();
			tIndicator.setType(type);
			tIndicator.setWithExpectedNumberResult(withExpectedNumberResult);
			try {
				typeIndicatorRepository.saveAndFlush(tIndicator);
			} catch (Exception e) {
			}
		}
	}

	private void storeParametre(String slug, String value, String label) {
		try {
			Parametre param = paramrepository.findBySlug(slug);
			if (param == null) {
				param = new Parametre();
				param.setSlug(slug);
				param.setParam(value);
				param.setLabel(label);
				paramrepository.save(param);
			}
		} catch (Exception e) {
		}
	}

	private void storeRole(String role, String description) {
		try {
			if (role != null && !role.equals("")) {
				Role dbRole = roleRepository.findByRole(role);
				if (dbRole == null) {
					dbRole = new Role();
					dbRole.setRole(role);
					dbRole.setDescription(description);
					roleRepository.saveAndFlush(dbRole);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void enableOneUser() {
		try {
			List<User> users = userRepository.findAll();
			if (users != null && !users.isEmpty()) {
				if (!dbHasValidAndAdminUser()) {
					User user = users.get(0);
					user.setValid(true);
					Role role = roleRepository.findByRole("admin");
					if (role != null) {
						List<Role> roles = new ArrayList<Role>();
						for (Role r : user.getRoles())
							roles.add(r);
						roles.add(role);
						user.setRoles(roles);
					}
				  userRepository.save(user); 
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean dbHasValidAndAdminUser() {
		try {
			List<User> validUsers = userRepository.findByValid(true);
			if (validUsers != null && !validUsers.isEmpty()) {
				for (User user : validUsers) {
					if (user.hasRole("admin"))
						return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
