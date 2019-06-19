package sn.cperf.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

import sn.cperf.dao.JustificationRepository;
import sn.cperf.dao.ProcessRepository;
import sn.cperf.dao.TaskRepository;
import sn.cperf.model.Justification;
import sn.cperf.model.Processus;
import sn.cperf.model.Task;
import sn.cperf.util.CausalityJustificationForm;
import sn.cperf.util.CausalitySearchForm;

@Controller
@RequestMapping("/Causality")
public class CostalityController {

	@Autowired
	ProcessRepository processRepository;
	@Autowired JustificationRepository justificationRepository;
	@Autowired TaskRepository taskRepository;

	@GetMapping("/")
	public String index(Model model, HttpSession session) {
		Page<Processus> pageProcess = null;
		CausalitySearchForm searchForm = null;
		if (session.getAttribute("searchForm") != null) {
			try {searchForm = (CausalitySearchForm) session.getAttribute("searchForm");} catch (Exception e) {}
			session.removeAttribute("searchForm");
		}
		if(searchForm == null)
			searchForm = new CausalitySearchForm();
		try {
			Calendar calendar = GregorianCalendar.getInstance();
			calendar.setTime(searchForm.getStartDate());
			calendar.add(Calendar.DATE, -1);
			Date startDate = calendar.getTime();
			calendar.setTime(searchForm.getEndDate());
			calendar.add(GregorianCalendar.DATE, +1);
			Date endDate = calendar.getTime();
			if (searchForm.getStatus().toLowerCase().equals("valid"))
				pageProcess = processRepository.getNofinishedAndNoExpiredProcess(startDate,endDate,new Date(),
						PageRequest.of(searchForm.getPage(), searchForm.getSize()));
			else if (searchForm.getStatus().toLowerCase().equals("unlunched"))
				pageProcess = processRepository
						.findByStartAtIsNull(PageRequest.of(searchForm.getPage(), searchForm.getSize()));
			else if (searchForm.getStatus().toLowerCase().equals("canceled"))
				pageProcess = processRepository
						.findByValidFalse(PageRequest.of(searchForm.getPage(), searchForm.getSize()));
			else if (searchForm.getStatus().toLowerCase().equals("unfinished_expired"))
				pageProcess = processRepository.getNofinishedAndExpiredProcess(startDate,endDate,new Date(),
						PageRequest.of(searchForm.getPage(), searchForm.getSize()));
			else
				pageProcess = processRepository
						.getFinishedAndExpiredProcess(startDate,endDate,PageRequest.of(searchForm.getPage(), searchForm.getSize()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.addAttribute("pageProcess", pageProcess);
		model.addAttribute("causalitySearchForm", searchForm);
		return "causality";
	}

	@PostMapping("/")
	public String serachProcess(@Valid CausalitySearchForm searchForm, HttpSession session) {
		session.setAttribute("searchForm", searchForm);
		return "redirect:/Causality/";
	}
	@GetMapping("/ChangePage")
	public String changePage(@RequestParam(name="status",defaultValue="") String status,
			@RequestParam(name="startDate",defaultValue="") String startDatePatterm,
			@RequestParam(name="endDate",defaultValue="") String endDatePattern,
			@RequestParam(name="page",defaultValue="0") int page,
			@RequestParam(name="size",defaultValue="1") int size, HttpSession session) {
		  if(session.getAttribute("searchForm") != null)
			  session.removeAttribute("searchForm");
		  CausalitySearchForm searchForm = new CausalitySearchForm();
		  SimpleDateFormat tf = new SimpleDateFormat("yyyy-MM-dd");
		  try {
			Date startDate = tf.parse(startDatePatterm);
			searchForm.setStartDate(startDate);
		} catch (ParseException e) {
		}
		try {
			Date endDate = tf.parse(endDatePattern);
			searchForm.setEndDate(endDate);
		} catch (ParseException e) {
		}
		if(status != null && !status.equals(""))
			searchForm.setStatus(status);
		searchForm.setPage(page);
		searchForm.setSize(size);
		session.setAttribute("searchForm", searchForm);
		return "redirect:/Causality/";
	}
	@GetMapping("/getJustificationsByProcessOrTask")
	@ResponseBody
	List<Justification> getJustificationsByProcessOrTask(
			@RequestParam(name="pid",defaultValue="0") Long processId,
			@RequestParam(name="tid",defaultValue="0") Long taskId){
			try {
				if(taskId != null && taskId >0) {
					Task task = taskRepository.getOne(taskId);
					return justificationRepository.findByTask(task);
				}else {
					Processus process = processRepository.getOne(processId);
					return justificationRepository.findByProcessAndTaskIsNull(process);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	     return null;
	}
	
	@GetMapping(value= {"/addJustification","/editJustification"})
	@ResponseBody 
	public Map<String,Object> editJustification(@RequestParam("data") String justficationJson) {
		Map<String,Object> data = new HashMap<>();
		try {
			ObjectMapper om = new ObjectMapper();
			CausalityJustificationForm justificationForm = om.readValue(justficationJson, CausalityJustificationForm.class);
			Processus justificationProcess = null;
			Task justificationTask = null;
			try {
				if(justificationForm.getProcessId() != null && justificationForm.getProcessId()>0) {
					Optional<Processus> opJustificationProcess = processRepository.findById(justificationForm.getProcessId());
					if(opJustificationProcess.isPresent()) {
						justificationProcess = new Processus();
						justificationProcess = opJustificationProcess.get();
					}
				}
			} catch (Exception e) {
			}
			try {
				if(justificationForm.getTaskId() != null && justificationForm.getTaskId()>0) {
					Optional<Task> opTask = taskRepository.findById(justificationForm.getTaskId());
					if(opTask.isPresent()) {
						justificationTask = new Task();
						justificationTask = opTask.get();
					}
				}
			} catch (Exception e) {
			}
			Justification justification = null;
			if(justificationForm.getId() != null && justificationForm.getId()>0) {
				try { justification = justificationRepository.getOne(justificationForm.getId()); } catch (Exception e) {}
			}
			if(justification == null)
				justification = new Justification();
			justification.setCause(justificationForm.getCause());
			justification.setContent(justificationForm.getContent());
			if(justification.getTask() == null)
				justification.setTask(justificationTask);
			if(justification.getProcess() == null)
				justification.setProcess(justificationProcess);
			if(justificationRepository.save(justification) != null) {
				data.put("status", true);
				data.put("msg", justificationForm.getId() != null ? "Justification modifiée avec succès !" : "Justification enregistrée avec succès !");
			}else {
				data.put("status", false);
				data.put("msg", justificationForm.getId() != null ? "Echèc de la modification de la justification !" : "Echéc de l'enregistrement de la justification !");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}
	@GetMapping("/delJustification")
	@ResponseBody
	public void delJustification(@RequestParam("id") Long id) {
		try {
			justificationRepository.deleteById(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
