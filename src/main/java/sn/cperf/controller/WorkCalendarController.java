package sn.cperf.controller;

import java.util.ArrayList;
import java.util.List;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import sn.cperf.dao.HolidayRepositoty;
import sn.cperf.dao.TaskRepository;
import sn.cperf.dao.WorkCalendarRepository;
import sn.cperf.model.Holiday;
import sn.cperf.model.Task;
import sn.cperf.model.WorkCalendar;

@Controller
@Secured(value= {"ROLE_admin"})
@RequestMapping("/WorkCalendar")
public class WorkCalendarController {
	@Autowired WorkCalendarRepository wCalendarRepository;
	@Autowired HolidayRepositoty holidayRepository;
	@Autowired TaskRepository taskRepository;
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
	
	@GetMapping("/holidays/")
	public String getHolidays(@RequestParam(name="hid", defaultValue="0") Long holidayId, Model model) {
		Holiday holiday = null;
		if(holidayId != null && holidayId >0)
			try {holiday = holidayRepository.getOne(holidayId);} catch (Exception e) {}
		if(holiday == null)
			holiday = new Holiday();
		model.addAttribute("hid", holidayId);
		model.addAttribute("holiday", holiday);
		model.addAttribute("holidays", holidayRepository.findAll(Sort.by(Order.desc("id"))));
		return "holidays";
	}
	
	@PostMapping("/holidays/")
	public String editHolidays(@Valid @ModelAttribute("holiday") Holiday holiday,BindingResult bind,
			@RequestParam(name="hid", defaultValue="0") Long holidayId, Model model) {
		if(!bind.hasErrors()) {
			try {
				if(holiday.getDte() != null) {
					boolean isUpdateOp = false;
					Holiday holidayDB = holidayRepository.findByDte(holiday.getDte());
					if(holiday.getId() != null)
						isUpdateOp = true;
					if(holidayDB != null ) {
						holiday.setDte(holidayDB.getDte());
						holiday.setId(holidayDB.getId());
					}
					if(holidayRepository.save(holiday) != null) {
						if(isUpdateOp)
							model.addAttribute("successMsg", "Jour ferié modifié");
						else
							model.addAttribute("successMsg", "Jour ferié Enregistré");
					}else {
						model.addAttribute("errorMsg", "Donnée non enrégistrées, veuillez recommencez");
					}
				}else {
					model.addAttribute("errorMsg", "La date du jour est obligatoire");
				}
			} catch (Exception e) {
				e.printStackTrace();
				model.addAttribute("errorMsg", "Opération échouée !");
			}
		}else {
			bind.getAllErrors().forEach(e->System.err.println(e.getDefaultMessage()));
			model.addAttribute("errorMsg", "Veuillez vérifier les données renseignées aux champs du formulaire!");
		}
		model.addAttribute("hid", holidayId);
		model.addAttribute("holidays", holidayRepository.findAll(Sort.by(Order.desc("id"))));
		return "holidays";
	}
	
	@GetMapping("/holidays/{hid}/del/")
	public String delHoliday(@PathVariable("hid") Long id) {
		try {
			Holiday holiday = holidayRepository.getOne(id);
			// detaching holidays task
			if(holiday.getTasks() != null) {
				for(Task t : holiday.getTasks()) {
					if(t.getHolidays() != null) {
						List<Holiday> taskHolidays = new ArrayList<>();
						for(Holiday h : t.getHolidays()) {
							if(h.getId() != holiday.getId())
								taskHolidays.add(h);
						}
						t.setHolidays(taskHolidays);
						taskRepository.save(t); 
					}
				}
			}
			// deleting holiday
			holidayRepository.delete(holiday);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/WorkCalendar/holidays/";
	}
}
