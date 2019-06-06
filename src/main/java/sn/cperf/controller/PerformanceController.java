package sn.cperf.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

import sn.cperf.dao.IndicateurRepository;
import sn.cperf.dao.IndicatorDataCollectorRepository;
import sn.cperf.dao.ObjectifRepository;
import sn.cperf.dao.TypeObjectifRepository;
import sn.cperf.dao.UserRepository;
import sn.cperf.model.Indicateur;
import sn.cperf.model.IndicatorDataCollector;
import sn.cperf.model.Objectif;
import sn.cperf.model.TypeObjectif;
import sn.cperf.model.User;
import sn.cperf.service.CperfService;
import sn.cperf.service.IndicatorService;
import sn.cperf.util.IndicatorDataCollectorUtil;

@Controller
@RequestMapping("/Performance")
public class PerformanceController {
	@Autowired
	UserRepository userRepository;
	@Autowired
	CperfService cperfService;
	@Autowired
	ObjectifRepository objectfiRepository;
	@Autowired
	TypeObjectifRepository typeObjectifRepository;
	@Autowired
	IndicatorService indicatorService;
	@Autowired
	IndicatorDataCollectorRepository indicatorDataCollectorRepository;
	@Autowired
	IndicateurRepository indicateurRepository;

	@GetMapping("/")
	public String getPerfomanceView(@RequestParam(name = "uid", defaultValue = "0") Long userId, Model model) {
		User user = null;
		if (userId != null && userId > 0) {
			try {
				user = userRepository.getOne(userId);
			} catch (Exception e) {
			}
		}
		User loged = null;
		try {loged = cperfService.getLoged();} catch (Exception e) {}
		model.addAttribute("user", user);
		model.addAttribute("loged", loged);
		model.addAttribute("isAdmin",loged != null && loged.hasRole("admin") ? true : false);
		return "perfomance";
	}

	@GetMapping("/getObjectifByUserJson")
	@ResponseBody
	public Map<String, Object> getObjectifByUserJson(@RequestParam(name = "uid", defaultValue = "0") Long userId,
			@RequestParam(name = "tobid", defaultValue = "0") Long typeObjectifId,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "7") int size) {
		User user = null;
		Map<String, Object> datas = new HashMap<>();
		Page<Objectif> objectifs = null;
		try {
			if (userId != null && userId > 0) {
				Optional<User> opUser = userRepository.findById(userId);
				if (opUser.isPresent()) {
					user = new User();
					user = opUser.get();
				}
			}
		} catch (Exception e) {
		}
		if (user == null)
			user = cperfService.getLoged();
		if (user != null) {
			datas.put("user", user);
			TypeObjectif typeObjectif = null;
			try {
				if (typeObjectifId != null && typeObjectifId > 0) {
					Optional<TypeObjectif> opTypeObjectif = typeObjectifRepository.findById(typeObjectifId);
					if (opTypeObjectif.isPresent()) {
						typeObjectif = new TypeObjectif();
						typeObjectif = opTypeObjectif.get();
					}
				}
			} catch (Exception e1) {

			}
			if (typeObjectif != null) {
				try {
					if(user.getGroupes() == null || user.getGroupes().isEmpty())
						objectifs = objectfiRepository.findByFonctionAndType(user.getFonction(),typeObjectif, PageRequest.of(page, size));
					else	
						objectifs = objectfiRepository.serachByTypeAndFonctionOrGroupIn(typeObjectif,user.getFonction(), user.getGroupes(), PageRequest.of(page, size));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else {
				try {
					if(user.getGroupes() == null || user.getGroupes().isEmpty())
						objectifs = objectfiRepository.findByFonction(user.getFonction(), PageRequest.of(page, size));
					else
						objectifs = objectfiRepository.findByFonctionOrGroupIn(user.getFonction(), user.getGroupes(),PageRequest.of(page, size));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (objectifs != null) {
			datas.put("totalPages", objectifs.getTotalPages());
			List<Objectif> datas_objectifs = new ArrayList<>();
			for (Objectif obj : objectifs.getContent()) {
				if (obj.getIndicators() != null && !obj.getIndicators().isEmpty()) {
					for (Indicateur ind : obj.getIndicators()) {
						ind.setDataResultEditable(indicatorService.checkIndicatorDataResultEditable(ind));
					}
				}
				datas_objectifs.add(obj);
			}
			datas.put("content", datas_objectifs);
		}
		return datas;
	}

	@GetMapping("/getEditIndicatorDataCollectorJson")
	@ResponseBody
	public IndicatorDataCollector getEditIndicatorDataCollectorJson(
			@RequestParam("stringDataCollector") String stringDataCollector,
			@RequestParam("indicatorId") Long indicatorId) {

		try {
			if (indicatorId != null && indicatorId > 0) {
				Indicateur indicator = new Indicateur();
				Optional<Indicateur> opIndicator = indicateurRepository.findById(indicatorId);
				if (opIndicator.isPresent())
					indicator = opIndicator.get();
				if (indicator != null && indicator.getId() != null) {
					System.err.println("je suis là");
					ObjectMapper om = new ObjectMapper();
					IndicatorDataCollectorUtil indicatorDataCollectorUtil = om.readValue(stringDataCollector,
							IndicatorDataCollectorUtil.class);
					if (indicatorDataCollectorUtil != null) {
						IndicatorDataCollector indicatorDataCollector = new IndicatorDataCollector();
						indicatorDataCollector.setId(indicatorDataCollectorUtil.getId());
						indicatorDataCollector.setDataNumber(indicatorDataCollectorUtil.getDataNumber());
						indicatorDataCollector.setDataText(indicatorDataCollectorUtil.getDataText());
						indicatorDataCollector.setStartDate(indicatorDataCollectorUtil.getStartDate());
						indicatorDataCollector.setMaxDate(indicatorDataCollectorUtil.getMaxDate());
						indicatorDataCollector.setIndicator(indicator);
						return indicatorDataCollectorRepository.save(indicatorDataCollector);
					}
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@GetMapping("/changeDataCollectorArchieStatus")
	@ResponseBody
	public boolean changeDataCollectorArchieStatus(@RequestParam("collectorId") Long dataCollectorId,
			@RequestParam(name="status", defaultValue="false") boolean status) {
		try {
			IndicatorDataCollector collector = indicatorDataCollectorRepository.getOne(dataCollectorId);
			if(collector != null) {
				collector.setArchive(status);
				return (indicatorDataCollectorRepository.save(collector) != null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	@GetMapping("/getUsersJson")
	@ResponseBody
	public List<User> getUsersJson(){
		return userRepository.findAll();
	}
	
	@GetMapping("/getUserByLastOrFirstnameJson")
	@ResponseBody
	List<User> getUserByLastOrFirstnameJson(@RequestParam("lastOrFirstname") String lastOrFirstname){
		List<User> users = new ArrayList<>();
		try {
			users = userRepository.findByFirstnameLikeIgnoreCaseOrLastnameLikeIgnoreCase("%"+lastOrFirstname+"%", "%"+lastOrFirstname+"%");
			if(users == null || users.isEmpty())
				users = userRepository.findAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return users;
	}
	
	@GetMapping("/getTypesObjectifsJson")
	@ResponseBody
	List<TypeObjectif> getTypesObjectifsJson(){
		return typeObjectifRepository.findAll();
	}
}
