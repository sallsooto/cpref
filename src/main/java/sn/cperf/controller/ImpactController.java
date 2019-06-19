package sn.cperf.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

import sn.cperf.dao.ImpactRepository;
import sn.cperf.dao.JustificationRepository;
import sn.cperf.dao.ProcessRepository;
import sn.cperf.dao.TaskRepository;
import sn.cperf.form.JustificationForm;
import sn.cperf.model.Impact;
import sn.cperf.model.Justification;
import sn.cperf.model.Processus;
import sn.cperf.model.Task;

@Controller
@RequestMapping("/Impact")
public class ImpactController {
	
	@Autowired ProcessRepository processRepository;
	@Autowired JustificationRepository justificationRepository;
	@Autowired ImpactRepository impactRepository;
	@Autowired TaskRepository taskRepository;
	
	@GetMapping("/History")
	@Secured(value= {"ROLE_admin"})
	public String history() {
		return "history_impact";
	}
	
	@GetMapping("/getHistoryJson")
	@ResponseBody
	public Page<Processus> getHistoryJson(@RequestParam(name="startDatePattern",defaultValue="") String startDatePattern,
			                              @RequestParam(name="endDatePattern",defaultValue="") String endDatePattern,
			                              @RequestParam(name="status",defaultValue="finished_expired") String status,
			                              @RequestParam(name="page",defaultValue="0") int page, @RequestParam(name="size",defaultValue="5") int size) {
		Date startDate = null, endDate=null;
		status = (status == null || status.equals("")) ? "finished_expired" :  status;
		try {
			SimpleDateFormat tf = new SimpleDateFormat("dd/MM/yyy");
			try { startDate = tf.parse(startDatePattern); } catch (ParseException e) {}
			try { endDate = tf.parse(endDatePattern); } catch (ParseException e) {}
			if(endDate == null)
				endDate = new Date();
			if(startDate == null) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(endDate);
				calendar.set(Calendar.DATE, Calendar.DATE-5);
				startDate = calendar.getTime();
			}
			
			if(startDate.compareTo(endDate)>0) {
				Date tmpDate = endDate;
				endDate = startDate;
				startDate = tmpDate;
			}
			if(status.toLowerCase().equals("valid"))
				return processRepository.getNofinishedAndNoExpiredProcess(startDate, endDate,new Date(), new PageRequest(page, size));
			else if(status.toLowerCase().equals("unlunched"))
				return processRepository.findByStartAtIsNull(new PageRequest(page, size));
			else if(status.toLowerCase().equals("canceled"))
				return processRepository.findByValidFalse(new PageRequest(page, size));
			else if(status.toLowerCase().equals("unfinished_expired"))
				return processRepository.getNofinishedAndExpiredProcess(startDate, endDate,new Date(), new PageRequest(page, size));
			else
				return processRepository.getFinishedAndExpiredProcess(startDate, endDate,new PageRequest(page, size));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@GetMapping("/findJustificationByIdJson")
	@ResponseBody
	public Justification getJustificationById(@RequestParam("jid") Long jid) {
		try {
			return justificationRepository.getOne(jid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@GetMapping("/findProcessByIdJson")
	@ResponseBody
	public Processus getProcessById(@RequestParam("pid") Long pid) {
		try {
			return processRepository.getOne(pid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	@GetMapping("/editJustificationJson")
	@ResponseBody
	public Justification editJustificationJson(@RequestParam("data") String data) {
		try {
			ObjectMapper om = new ObjectMapper();
			JustificationForm form = om.readValue(data, JustificationForm.class);
			Justification justification = null;
			Long taskId = null;
			try {taskId = taskRepository.getOne(form.getTaskId()).getId();} catch (Exception e) {}
			try {justification = justificationRepository.getOne(form.getId());} catch (Exception e) {}
			if(justification == null)
				justification = new Justification();
			justification.setCause(form.getCause());
			justification.setContent(form.getContent());
			try {justification.setImpact(impactRepository.findBySlug(form.getImpact()));} catch (Exception e1) {}
			try {justification.setProcess(taskId == null ? processRepository.getOne(form.getProcessId()) : null);} catch (Exception e) {}
			try {justification.setTask(taskRepository.getOne(form.getTaskId()));} catch (Exception e) {}
			return justificationRepository.save(justification);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	@GetMapping("/getAllJson/")
	@ResponseBody
	public List<Impact> getAlljson(){
		return impactRepository.findAll();
	}

	@GetMapping("/getProcessUncompletedTasks/")
	@ResponseBody
	public List<Task> getProcessUncompletedTasksJson(@RequestParam("pid") Long pid){
		try {
			return taskRepository.getUncompletedTaskTasksByProcess(pid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
