package sn.cperf.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
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

import sn.cperf.dao.WorkCalendarRepository;
import sn.cperf.model.WorkCalendar;

@Controller
@Secured(value= {"ROLE_admin"})
@RequestMapping("/WorkCalendar")
public class WorkCalendarController {
	@Autowired WorkCalendarRepository wCalendarRepository;
	@GetMapping("/")
	public String index(@RequestParam(name="wcid", defaultValue="0") Long id, Model model) {
		WorkCalendar workCalender = null;
		if(id != null && id>0)
			try {workCalender = wCalendarRepository.getOne(id);} catch (Exception e) {}
		if(workCalender == null)
			workCalender = new WorkCalendar();
		model.addAttribute("calendars", wCalendarRepository.findAll(Sort.by(Order.asc("id"))));
		model.addAttribute("wcid", id);
		model.addAttribute("workCalendar", workCalender);
		return "calendar";
	}
	@PostMapping("/")
	public String edit(@RequestParam(name="wcid", defaultValue="0") Long id,
			@Valid 	@ModelAttribute("workCalendar") WorkCalendar workCalendar,BindingResult bind,Model model) {
		boolean isUpdateOp = false;
		if(bind.hasErrors()) {
			model.addAttribute("errorMsg", "Des érreur de validation sont trouvé vérifiez les donnée renseignées");
			bind.getAllErrors().forEach(e ->System.err.println(e.getDefaultMessage()));
		}else {
			try {
				if(workCalendar.getDayIndex() != null && workCalendar.getDayIndex() >0) {
					// we have inconnu because java calendar days_of_week start of 1
					String[] days = {"Inconnu","Dimanche", "Lundi", "Mardi","Mercredi","Jeudi","Vendredi","Samedi"};
					workCalendar.setDayName(days[workCalendar.getDayIndex()]);
					WorkCalendar dbWc = new WorkCalendar();
					try { dbWc  = wCalendarRepository.findByDayIndex(workCalendar.getDayIndex());} catch (Exception e) {}
					if(dbWc != null && dbWc.getId() != null) {
						workCalendar.setId(dbWc.getId());
						isUpdateOp = true;
					}
					if(workCalendar.getStartHour() == null) {
						workCalendar.setStartHour(null);
						workCalendar.setPauseHours(null);
					}
					if(workCalendar.getWorkHours() == null)
						workCalendar.setWorkHours(0);
					if(workCalendar.getWorkMinutes() == null)
						workCalendar.setWorkMinutes(0);
					if(workCalendar.getPauseHours() == null)
						workCalendar.setPauseHours(0);
					if(workCalendar.getPauseMinutes() == null)
						workCalendar.setPauseMinutes(0);
					if(workCalendar.getWorkHours() == 0 && workCalendar.getWorkMinutes() ==0) {
						workCalendar.setPauseMinutes(0);
						workCalendar.setPauseHours(0);
					}
					if(workCalendar.getWorkHours() != null && workCalendar.getWorkHours() > 24) {
						model.addAttribute("errorMsg", "Le nombre d'heure de travail n'est pas valide !");
					}else {
						if(workCalendar.getPauseHours() != null && workCalendar.getWorkHours() != null 
								&& workCalendar.getPauseHours() > workCalendar.getWorkHours()) {
							model.addAttribute("errorMsg", "Le nombre d'heure de pause n'est pas valide !");
						}else {
							if(workCalendar.getWorkMinutes()>60) {
								model.addAttribute("errorMsg", "Le nombre de minutes de travail n'est pas valide !");
							}else {
								if(workCalendar.getPauseMinutes()>60) {
									model.addAttribute("errorMsg", "Le nombre de minutes de pause n'est pas valide !");
								}else {
									if(workCalendar.getWorkHours() == 0 && workCalendar.getWorkMinutes() == 0 
											&& workCalendar.getPauseHours() ==0 && workCalendar.getPauseMinutes()==0)
										workCalendar.setFreeDay(true);
									else
										workCalendar.setFreeDay(false);
									if(workCalendar.getStartHour() != null && (workCalendar.getStartHour() < 0 || workCalendar.getStartHour()>23)) {
										model.addAttribute("errorMsg", "L'heure de reprise du travail n'est pas valide !");
									}else {
										if(wCalendarRepository.save(workCalendar) != null) {
											if(isUpdateOp) {
												model.addAttribute("successMsg", "Calandrier du "+ workCalendar.getDayName()+" modifier !");
											}else {
												model.addAttribute("successMsg", "Calandrier du"+ workCalendar.getDayName()+" modifier !");
												workCalendar = new WorkCalendar();
											}
										}
									}
								}
							}
						}
					}
				}else {
					model.addAttribute("errorMsg", "Vous devez shoisir une journée de la semaine valide !");
				}
			} catch (Exception e) {
				e.printStackTrace();
				model.addAttribute("errorMsg", "Une erreur est survenue !");
			}
		}
		model.addAttribute("calendars", wCalendarRepository.findAll(Sort.by(Order.asc("id"))));	
		model.addAttribute("wcid", id);
		return "calendar";
	}
	
	@GetMapping("/changeFreeDayStatus/")
	@ResponseBody
	public Map<String, Object> changeFreeDayStatus(@RequestParam("wcid") Long id, @RequestParam(name="status", defaultValue="false") boolean status) {
		 Map<String, Object> data = new HashMap<String, Object>();
		 data.put("msg", "Non");
		 data.put("status", false);
		 try {
			WorkCalendar wc = wCalendarRepository.getOne(id);
			 wc.setFreeDay(status);
			 wCalendarRepository.save(wc);
			 data.put("msg", wc.isFreeDay() ? "Oui": "Non");
			 data.put("status", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		 return data;
	}
}
