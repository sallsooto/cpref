package sn.cperf.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import sn.cperf.dao.FonctionRepository;
import sn.cperf.dao.GroupRepository;
import sn.cperf.dao.IndicateurRepository;
import sn.cperf.dao.ObjectifRepository;
import sn.cperf.dao.TaskRepository;
import sn.cperf.dao.TypeIndicatorRepository;
import sn.cperf.dao.TypeObjectifRepository;
import sn.cperf.model.Fonction;
import sn.cperf.model.Indicateur;
import sn.cperf.model.Objectif;
import sn.cperf.model.TypeIndicator;
import sn.cperf.model.TypeObjectif;

@Controller
@RequestMapping("/Objectif")
@Secured({ "ROLE_admin" })
public class ObjectifController {
	@Autowired
	ObjectifRepository objectifRepository;
	@Autowired
	FonctionRepository fonctionRepository;
	@Autowired
	IndicateurRepository indicateurRepository;

	@Autowired
	TypeObjectifRepository typeObjectifRepository;

	@Autowired
	TaskRepository taskRepository;
	@Autowired
	GroupRepository groupRepository;
	
	@Autowired TypeIndicatorRepository typeIndicatorRepository;

	@GetMapping("/")
	public String getObjectifView(@RequestParam(name = "fid", defaultValue = "1") Long fonctionId,
			@RequestParam(name = "objid", defaultValue = "0") Long objectifId, Model model) {
		try {
			if (fonctionId != null && fonctionId > 0) {
				Optional<Fonction> of = fonctionRepository.findById(fonctionId);
				if (of.isPresent()) {
					Objectif obj = new Objectif();
					try {
						if(objectifId != null && objectifId >0) {
							Optional<Objectif> opObj = objectifRepository.findById(objectifId);
							if(opObj.isPresent())
								obj = opObj.get();
						}
					} catch (Exception e) {
					}
					if (obj == null) {
						obj = new Objectif();
					}
					Fonction fonction = of.get();
					model.addAttribute("fonction", fonction);
					model.addAttribute("typeObjectifs", typeObjectifRepository.findByValid(true));
					model.addAttribute("objectifs", objectifRepository.findByFonctionOrderByIdDesc(fonction));
					model.addAttribute("groups", groupRepository.findAll());
					model.addAttribute("tasks", taskRepository.getByActor(fonction.getId()));
					model.addAttribute("collectifObjectif", obj.getGroup() != null ? true : false);
					model.addAttribute("withTask", obj.getTask() != null ? true : false);
					obj.setFonction(fonction);
					model.addAttribute("objectif", obj);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "objectif";
	}

	@PostMapping("/")
	public String saveObjectif(@RequestParam(name = "collectif", defaultValue = "false") boolean collectif,
			@RequestParam(name = "withTask", defaultValue = "false") boolean withTask,
			@RequestParam(name = "fid", defaultValue = "0") Long fonctionId,@Valid Objectif objectif, BindingResult bind, Model model) {
		try {
			if (!bind.hasErrors()) {
					boolean isUpdateOp = objectif.getId() != null ? true : false;
					if(!collectif)
						objectif.setGroup(null);
					if(!withTask)
						objectif.setTask(null);
					if(objectifRepository.save(objectif) != null) {
						String msg = isUpdateOp ? "Objectif modifié !" : "Objectif enregistré";
						model.addAttribute("successMsg", msg);
						System.out.println("fonction id"+objectif.getFonction().getId());
						model.addAttribute("fonction", objectif.getFonction());
						model.addAttribute("typeObjectifs", typeObjectifRepository.findByValid(true));
						model.addAttribute("objectifs", objectifRepository.findByFonctionOrderByIdDesc(objectif.getFonction()));
						model.addAttribute("groups", groupRepository.findAll());
						model.addAttribute("tasks", taskRepository.getByActor(objectif.getFonction().getId()));
						model.addAttribute("collectifObjectif", objectif.getGroup() != null ? true : false);
						model.addAttribute("withTask", objectif.getTask() != null ? true : false);
					}else {
						model.addAttribute("errorMsg", "Opération échouée !");
					}
				}else {
					for(ObjectError error : bind.getAllErrors())
						System.err.println(error.getDefaultMessage());
					model.addAttribute("errorMsg", "Vous avez des erreurs de validation du formulaire!");
				}
		} catch (Exception e) {
			model.addAttribute("errorMsg", "Exception levée, vueillez recommencr ou contacter l'administrateur !");
			e.printStackTrace();
		}
		return "objectif";
	}
	@GetMapping("/getTypeObjectifById")
	@ResponseBody
	public TypeObjectif getTypeObjectifById(@RequestParam("id") Long id) {
		try {
			return typeObjectifRepository.getOne(id);
		} catch (Exception e) {
		}
		return null;
	}

	@GetMapping("/{id}/del")
	public String deleteObjectifJson(@PathVariable("id") Long objectifId) {
		System.out.println("je rentre dans del");
		try {
			Optional<Objectif> opObj = objectifRepository.findById(objectifId);
			if (opObj.isPresent()) {
				Objectif objectif = opObj.get();
				Long fonction_id = objectif.getFonction().getId();
				objectifRepository.delete(objectif);
				return "redirect:/Objectif/?fid="+fonction_id;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "redirect:/Objectif/";
	}
	
	@GetMapping("/Type")
	public String getTypeObjectifView(@RequestParam(name="tid", defaultValue="0") Long id, Model model) {
		TypeObjectif typeObjectif = new TypeObjectif();
		try {
			if(id != null && id>0) {
				Optional<TypeObjectif> opTob = typeObjectifRepository.findById(id);
				if(opTob.isPresent())
					typeObjectif = opTob.get();
			}
		} catch (Exception e) {
		}
		try { model.addAttribute("typesObjectifs", typeObjectifRepository.findAll()); } catch (Exception e) {}
		if(typeObjectif == null)
			typeObjectif = new TypeObjectif();
		model.addAttribute("form", typeObjectif);
		return "type_objectif";
	}
	
	@GetMapping("/Type/changeValidStatus")
	@ResponseBody
	public Map<String, Object> changeValidStatus(@RequestParam("tid") Long typeObjectifId, @RequestParam("valid") boolean valid){
		Map<String, Object> datas = new HashMap<>();
		datas.put("status", false);
		datas.put("msg","");
		try {
			TypeObjectif typeObj = typeObjectifRepository.getOne(typeObjectifId);
			if(typeObj != null) {
				typeObj.setValid(valid);
				if(typeObjectifRepository.save(typeObj) != null) {
					datas.put("status", true);
					datas.put("msg", valid ? "Actif" : "Inactif");
				}
			}
		} catch (Exception e) {
		}
		return datas;
	}
	
	@PostMapping("/Type")
	public String editTypeObjectif(@Valid @ModelAttribute("form") TypeObjectif typeObjectif, BindingResult bind,Model model) {	
		try {
			if(bind.hasErrors()) {
				bind.getAllErrors().forEach(e->{System.err.println(e.getDefaultMessage());});
			}else {
				boolean isUpdateOperation = (typeObjectif.getId() != null && typeObjectif.getId()>0) ? true : false;
				if(!isUpdateOperation) {
					String slug = "";
					if(typeObjectif.getDescription().length()>=5)
						slug =typeObjectif.getDescription().substring(0,typeObjectif.getDescription().length()-(typeObjectif.getDescription().length()-5));
					else
						slug = typeObjectif.getDescription();
					slug = slug.replace(" ", "_").toLowerCase();
					typeObjectif.setSlug(slug);
					TypeObjectif TypefindedbySlug = typeObjectifRepository.findBySlug(slug);
					if(TypefindedbySlug != null) {
						TypefindedbySlug = typeObjectifRepository.findFirstByOrderByIdDesc();
						if(TypefindedbySlug != null) {
							typeObjectif.setSlug(typeObjectif.getSlug()+"_"+TypefindedbySlug.getId());
						}
					}
				}
				if(typeObjectif.getUnite() != null && typeObjectif.getUnite().equals(""))
					typeObjectif.setUnite(null);
				if(typeObjectifRepository.save(typeObjectif) !=  null) {
					if(isUpdateOperation)
						model.addAttribute("successMsg", "Type d'objectif modifié !");
					else
						model.addAttribute("successMsg", "Type d'objectif enregistré");
				}else {
					model.addAttribute("errorMsg", "Données non enregistrées, veuillez recommencer !");
				}
			}
		} catch (Exception e) {
			model.addAttribute("errorMsg", "Opération échouée !");
			e.printStackTrace();
		}
		try { model.addAttribute("typesObjectifs", typeObjectifRepository.findAll()); } catch (Exception e) {}
		return "type_objectif";
	}

	@GetMapping("/changeParentIndicatorJson")
	@ResponseBody
	public boolean changeParentIndicatorJson(@RequestParam("indId") Long indicatorId,
			@RequestParam("indPId") Long parentId, @RequestParam("nextChirldId") Long nextChirldId) {
		try {
			if (indicatorId != null && indicatorId > 0) {
				Indicateur indicator = indicateurRepository.getOne(indicatorId);
				Indicateur bigParent = null;
				if (indicator != null) {
					bigParent = indicator.getParent();
					Indicateur parent = null;
					if (parentId != null && parentId > 0) {
						try {
							parent = indicateurRepository.getOne(parentId);
						} catch (Exception e) {
						}
					}
					// next chirdl setter
					Indicateur nexChirld = null;
					if (nextChirldId != null && nextChirldId > 0) {
						try {
							nexChirld = indicateurRepository.getOne(nextChirldId);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					// initialize parent_id before
					if (parent != null) {
						parent.setParent(null);
						indicateurRepository.save(parent);
					}
					indicator.setParent(null);
					indicateurRepository.save(indicator);
					if (nexChirld != null) {
						nexChirld.setParent(null);
						indicateurRepository.save(nexChirld);
					}
					// update parent id after initilizing
					indicator.setParent(parent);
					indicateurRepository.save(indicator);
					parent.setParent(bigParent);
					indicateurRepository.save(parent);
					if (nexChirld != null) {
						nexChirld.setParent(indicator);
						indicateurRepository.save(nexChirld);
					}
				}
			}
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	@GetMapping("/Indicator")
	public String getIndicators(@RequestParam("objid") Long objectifId,@RequestParam(name="indid", defaultValue="0") Long indicatorId, Model model) {
		Objectif objectif = null;
		try {
			objectif = objectifRepository.getOne(objectifId);
			if(objectif != null) {
				model.addAttribute("objectif", objectif);
				model.addAttribute("indicators", indicateurRepository.findByObjectifOrderByIdAsc(objectif));
				model.addAttribute("typeIndicators", typeIndicatorRepository.findByValid(true));
				Indicateur selectedIndicator = new Indicateur();
				selectedIndicator.setObjectif(objectif);
				if(indicatorId != null && indicatorId>0) {
					try {
						Optional<Indicateur> otIndicator = indicateurRepository.findById(indicatorId);
						if(otIndicator.isPresent())
							selectedIndicator = otIndicator.get();
					} catch (Exception e) {
					}
				}
				model.addAttribute("selectedIndicator", selectedIndicator);
				try {
					if(selectedIndicator.getId() != null)
						model.addAttribute("parentIndicators", indicateurRepository.findByParentNotAndIdNotOrderByIdDesc(selectedIndicator, selectedIndicator.getId()));
					else
						model.addAttribute("parentIndicators", indicateurRepository.findAll(Sort.by("id").descending()));
				} catch (Exception e) {
					model.addAttribute("parentIndicators", new ArrayList<>());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "indicators";
	}
	
	@PostMapping("/Indicator")
	public String editIndicators(@RequestParam("objid") Long objectifId,@RequestParam(name="indid", defaultValue="0") Long indicatorId,
								@Valid @ModelAttribute("selectedIndicator") Indicateur indicator, BindingResult bind, Model model) {
		try {
			if(bind.hasErrors()) {
				bind.getAllErrors().forEach(err ->System.err.println(err.getDefaultMessage()));
				model.addAttribute("errorMsg", "la validation du formulaire a échoué,verifiez les données renseignées !");
			}else {
				boolean isUpdateOp = indicator.getId() != null ? true : false;
				if(indicator.getType().isWithExpectedNumberResult()) {
					indicator.setExpectedTextResult(null);
				}
				else {
					indicator.setExpectedNumberResult(null);
					indicator.setExpectedResultUnite(null);
				}
				if(!indicator.isQuestionResolvableByActor()) {
					indicator.setQuestion(null);
				}else {
					if(indicator.getQuestion() != null && !indicator.getQuestion().toLowerCase().equals("")) {
						String lastQuestionCharacter = indicator.getQuestion().trim().substring(indicator.getQuestion().trim().length()-1, indicator.getQuestion().trim().length());
						if(lastQuestionCharacter.toLowerCase().equals("?")) {
							indicator.setQuestion(indicator.getQuestion().substring(0, indicator.getQuestion().lastIndexOf("?")-1));
						}
					}
				}
				if(indicateurRepository.save(indicator) != null) {
					if(isUpdateOp) {
						model.addAttribute("successMsg", "Indicateur modifié !");
					}
					else {
						model.addAttribute("successMsg", "Indicateur enrigistré !");
						indicator.setId(null);
					}
				}else {
					model.addAttribute("errorMsg", "Opération échouée, veuillez recommencer !");
				}
			}
		} catch (Exception e) {
			model.addAttribute("errorMsg", "Exception levée, veuillez actualser et recommencer !");
		}
		// get necesary model objects
		try {
			model.addAttribute("objectif", indicator.getObjectif());
			model.addAttribute("indicators", indicateurRepository.findByObjectifOrderByIdAsc(indicator.getObjectif()));
			model.addAttribute("typeIndicators", typeIndicatorRepository.findByValid(true));
			if(indicator.getId() != null)
				model.addAttribute("parentIndicators", indicateurRepository.findByParentNotAndIdNotOrderByIdDesc(indicator, indicator.getId()));
			else
				model.addAttribute("parentIndicators", indicateurRepository.findAll(Sort.by("id").descending()));
		} catch (Exception e) {
			model.addAttribute("parentIndicators", new ArrayList<>());
		}
		//model.addAttribute("selectedIndicator", indicator);
		if(indicator.getId() == null) {
			Indicateur selectedIndicateur = new Indicateur();
			selectedIndicateur.setObjectif(indicator.getObjectif());
			model.addAttribute("selectedIndicator", selectedIndicateur);
		}
		return "indicators";
	}
	
	@GetMapping("/getTIndicatorByIdJson")
	@ResponseBody
	public TypeIndicator getTIndicatorByIdJson(@RequestParam("id") Long id) {
		try {
			return typeIndicatorRepository.getOne(id);
		} catch (Exception e) {
		}
		return null;
	}

	@GetMapping("/Indicator/{id}/del")
	public String deleteIndicator(@PathVariable("id") Long indicatorId) {
		try {
			Optional<Indicateur> opIndic = indicateurRepository.findById(indicatorId);
			if (opIndic.isPresent()) {
				Indicateur indicateur = opIndic.get();
				Objectif obj = indicateur.getObjectif();
				try {indicateurRepository.delete(indicateur);} catch (Exception e) {
				}
				return "redirect:/Objectif/Indicator?objid="+obj.getId();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "redirect:/Objectif/";
	}

	
	@GetMapping("/Indicator/Type")
	public String getTypeIndicatorView(@RequestParam(name="tindid", defaultValue="0") Long id, Model model) {
		TypeIndicator typeIndicator = new TypeIndicator();
		try {
			if(id != null && id>0) {
				Optional<TypeIndicator> opTTind = typeIndicatorRepository.findById(id);
				if(opTTind.isPresent())
					typeIndicator = opTTind.get();
			}
		} catch (Exception e) {
		}
		try { model.addAttribute("typesIndicators", typeIndicatorRepository.findAll()); } catch (Exception e) {}
		if(typeIndicator == null)
			typeIndicator = new TypeIndicator();
		model.addAttribute("form", typeIndicator);
		return "type_indicator";
	}
	
	@GetMapping("/Indicator/Type/changeValidStatus")
	@ResponseBody
	public Map<String, Object> changeTypeIndicatorValidStatus(@RequestParam("tindid") Long typeIndicatorId, @RequestParam("valid") boolean valid){
		Map<String, Object> datas = new HashMap<>();
		datas.put("status", false);
		datas.put("msg","");
		try {
			TypeIndicator typeIndicator = typeIndicatorRepository.getOne(typeIndicatorId);
			if(typeIndicator != null) {
				typeIndicator.setValid(valid);
				if(typeIndicatorRepository.save(typeIndicator) != null) {
					datas.put("status", true);
					datas.put("msg", valid ? "Actif" : "Inactif");
				}
			}
		} catch (Exception e) {
		}
		return datas;
	}
	
	@PostMapping("/Indicator/Type")
	public String editTypeIndicator(@Valid @ModelAttribute("form") TypeIndicator typeIndicator, BindingResult bind,Model model) {	
		try {
			if(bind.hasErrors()) {
				bind.getAllErrors().forEach(e->{System.err.println(e.getDefaultMessage());});
			}else {
			    int nbError = 0;
				boolean isUpdateOperation = (typeIndicator.getId() != null && typeIndicator.getId()>0) ? true : false;
				if(!isUpdateOperation) {
					TypeIndicator typefindedbyType = null;
					try {typefindedbyType = typeIndicatorRepository.findByTypeIgnoreCase(typeIndicator.getType());} catch (Exception e) {}
					if(typefindedbyType != null) {
						nbError++;
						model.addAttribute("errorMsg", "Ce type d'indicateur existe déjà, vous ne pouvez pas le dupliquer !");
					}
				}
				if(nbError <=0) {
					if(typeIndicatorRepository.save(typeIndicator) !=  null) {
						if(isUpdateOperation)
							model.addAttribute("successMsg", "Type indicateur modifié !");
						else
							model.addAttribute("successMsg", "Type indicateur enregistré");
					}else {
						model.addAttribute("errorMsg", "Données non enregistrées, veuillez recommencer !");
					}
				}
			}
		} catch (Exception e) {
			model.addAttribute("errorMsg", "Opération échouée, verifiez les données remplis !");
			e.printStackTrace();
		}
		try { model.addAttribute("typesIndicators", typeIndicatorRepository.findAll()); } catch (Exception e) {}
		return "type_indicator";
	}
}
