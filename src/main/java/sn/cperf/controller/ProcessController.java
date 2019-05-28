package sn.cperf.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import sn.cperf.dao.GroupRepository;
import sn.cperf.dao.ProcessRepository;
import sn.cperf.dao.ProcessSectionRepository;
import sn.cperf.dao.TaskRepository;
import sn.cperf.dao.UserRepository;
import sn.cperf.form.SectionForm;
import sn.cperf.model.Group;
import sn.cperf.model.Procedure;
import sn.cperf.model.ProcessSection;
import sn.cperf.model.Processus;
import sn.cperf.model.Task;
import sn.cperf.model.User;
import sn.cperf.service.CperfService;
import sn.cperf.service.NotificationService;
import sn.cperf.service.StorageService;
import sn.cperf.util.NotificationType;

@Controller
@RequestMapping("/Process")
@Secured("ROLE_admin")
public class ProcessController {
	@Autowired
	ProcessRepository processRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired CperfService cperfService;
	@Autowired ProcessSectionRepository sectionRepository;
	@Autowired GroupRepository groupRepository;
	@Autowired ProcessSectionRepository processSectionRepository;
	@Autowired TaskRepository taskRepository;
	@Autowired StorageService storageService;
	@Autowired NotificationService notifService;

	@GetMapping("/Edit")
	public String process(@RequestParam(name="pid",defaultValue="0") Long processId, Model model) {
		Processus form = new Processus();
		if(processId != null && processId>0) {
			Optional<Processus> optProcess = processRepository.findById(processId);
			if(optProcess.isPresent()) {
				Processus processus = optProcess.get();
				if(processus != null) {
					form.setId(processus.getId());
					form.setDescription(processus.getDescription());
					form.setLabel(processus.getLabel());
					form.setPreviewStartDate(processus.getPreviewStartDate());
					form.setPreviewFinishDate(processus.getPreviewFinishDate());
					form.setStartDate(processus.getStartDate());
					form.setFinishDate(processus.getFinishDate());
				}
			}
		}
		model.addAttribute("process", form);
		return "process";
	}
	
	@PostMapping("/Edit")
	public String edit(@Valid @ModelAttribute("process") Processus form, BindingResult bind, Model model) {
		try {
			if(bind.hasErrors()) {
				bind.getAllErrors().forEach(e->{ System.out.println(e.getDefaultMessage());});
				model.addAttribute("errorMsg", "Des erreurs de validation trouvés!");
			}else {
				Processus process = new Processus();
				if(form.getId() != null) {
					Optional<Processus> optProcess = processRepository.findById(form.getId());
					if(optProcess.isPresent())
						process = optProcess.get();
				}
				process.setDescription(form.getDescription());
				process.setEditor(cperfService.getLoged());
				process.setFinishDate(form.getFinishDate());
				process.setLabel(form.getLabel());
				process.setStartDate(form.getStartDate());
				process.setPreviewStartDate(form.getPreviewStartDate());
				process.setPreviewFinishDate(form.getPreviewFinishDate());
				process.setId(form.getId());
				if(processRepository.save(process) != null) {
					if(form.getId() != null && form.getId()>0)
						model.addAttribute("successMsg", "Process modifié!");
					else
						model.addAttribute("successMsg", "Process enregistré!");
					form.setId(process.getId());
				}else {
					model.addAttribute("errorMsg", "Echèc de l'enregistrement de données!");
				}
			}
			//
		} catch (Exception e) {
			model.addAttribute("errorMsg", "Echèc de l'opération, verifiez les entrés du formulaire!");
			e.printStackTrace();
		}
		return "process";
	}
    
    @GetMapping("/{id}/del")
    public String delProcess(@PathVariable("id") Long id) {
    	try {
			Processus p = processRepository.getOne(id);
			processRepository.delete(p);
		} catch (Exception e) {
		}
    	return "redirect:/Process/List";
    }
	
	@GetMapping("/List")
	public String list(@RequestParam(name="s", defaultValue="") String searchKey, 
			@RequestParam(name="size", defaultValue="5") int size,
			@RequestParam(name="page", defaultValue="0") int page, Model model){
			List<Processus> process = new ArrayList<>();
			
			double pages = 0;
			try {
				if(searchKey != null && !searchKey.equals("")) {
					Page<Processus> pageProcess = processRepository.findByLabelLikeIgnoreCaseOrderByIdDesc("%"+searchKey+"%", new PageRequest(page, size));
					if(!pageProcess.isEmpty()) {
						process = pageProcess.getContent();
						pages = pageProcess.getTotalPages();
					}
				}else {
					Page<Processus> pageProcess = processRepository.findByOrderByIdDesc(new PageRequest(page, size));
					System.err.println("conten " + pageProcess.getContent().size());
					if(!pageProcess.isEmpty()) {
						process = pageProcess.getContent();
						pages = pageProcess.getTotalPages();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			model.addAttribute("processes",process);
			model.addAttribute("pages", pages);
			model.addAttribute("currentPage", page);
		return "liste_process";
	}
	
	@GetMapping("/playOrStop")
	@ResponseBody
	public Map<String, Object> playOrStop(@RequestParam("pid") Long id, @RequestParam("play") boolean play) {
		Map<String, Object> data = new HashMap<>();
		data.put("success", false);
		data.put("startAt", "Arrêté");
		data.put("maxDate", "Arrêté");
		try {
			Date startDate = play ? new Date() : null;
			Processus process = processRepository.getOne(id);
			process.setStartAt(startDate);
			process.setTotalTime(process.getMaxDate());
			if(processRepository.save(process) != null) {
				data.put("success", true);
				if(play) {
					SimpleDateFormat tf = new SimpleDateFormat("dd/MM/yyyy à HH:mm");
					data.put("startAt", tf.format(startDate));
					data.put("maxDate", tf.format(process.getMaxDate()));
				}
				// store Notifications
				 try {
					String title = (play) ? "Un process démarré " : " Un process stoppé ";
					 String noteSlug = (play) ? "demarré" : " arrêté";
					 String note = "Le process << " + process.getLabel() + " >> est " + noteSlug+" !";
					 notifService.notify(title, note, NotificationType.INFO.toString(), "/Task/", process.getAllUsers(), null);
				} catch (Exception e) {
				}
				// end store Notifications
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	@GetMapping("/Section/Edit")
	public String getEditSectionView(@RequestParam("pid") Long pid, @RequestParam(name="sid", defaultValue="0") Long sectionId,Model model) {
		try {
			Optional<Processus> opProcess = processRepository.findById(pid);
			ProcessSection section = new ProcessSection();
			if(opProcess.isPresent()) {
				Processus p =  opProcess.get();
				section.setProcess(p);
				if(sectionId != null && sectionId >0) {
					Optional<ProcessSection> opSection = sectionRepository.findById(sectionId);
					if(opSection.isPresent()) {
						section = opSection.get();
					}
				}
				SectionForm form = new SectionForm();
				form.setWithGroup(true);
				form.setId(section.getId());
				form.setGroupId(section.getGroup() != null ? section.getGroup().getId() : null);
				form.setProcessId(p.getId());
				form.setName(section.getName());
				form.setTasks(section.getTasks());
				model.addAttribute("process", p);
				model.addAttribute("sectionForm", form);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "section";
	}
	
	@PostMapping("/Section/Edit")
	public String editSection(@RequestParam("pid") Long pid,@Valid @ModelAttribute("sectionForm") SectionForm form, BindingResult bind, Model model) {
		Processus p = new Processus();
		try {
			p.setId(form.getProcessId());
			if(p.getId() != null && p.getId()>0)
				p = processRepository.getOne(p.getId());
			if(bind.hasErrors()) {
				bind.getAllErrors().forEach(e->{
					System.err.println(e.getDefaultMessage());
				});
			}else {
				ProcessSection section = new ProcessSection();
				if(form.isWithGroup() && form.getGroupId() != null && form.getGroupId() >0) {
					Optional<Group> og = groupRepository.findById(form.getId());
					if(og.isPresent())
						section.setGroup(og.get());
				}
				section.setId(form.getId());
				section.setName(form.getName());
				section.setProcess(p);
				if(processSectionRepository.save(section) != null) {
					p = section.getProcess();
					if(form.getId() != null && form.getId()>0)
						model.addAttribute("successMsg", "Section modifiée!");
					else
						model.addAttribute("successMsg", "Section enregistrée!");
					form.setId(section.getId());
				}else {
					model.addAttribute("errorMsg", "Echèc de l'enregistrement de données!");
				}
			}
		} catch (Exception e) {
			model.addAttribute("errorMsg", "Opération échouée, veuillez recommncer !");
			e.printStackTrace();
		}
		model.addAttribute("process", p);
		return "section";
	}
	
	@GetMapping("/Task/Edit")
	public String getTaskView(@RequestParam(name="pid") Long processId,
			@RequestParam(name="tid", defaultValue="0") Long taskId, Model model) {
		Task task = new Task();
		List<Task> tasks  = new ArrayList<>();
		Processus process = null;
		try {
			if(processId != null && processId>0) {
				Optional<Processus> oProcess = processRepository.findById(processId);
				if(oProcess.isPresent()) {
					process = new Processus();
					process = oProcess.get();
					tasks = taskRepository.getByProcess(process.getId());
				}else {
					return "redirect:/Process/List/";
				}
			}
			
			try {
				if(!process.getSections().isEmpty()) {
					task.setSection(process.getSections().get(process.getSections().size()-1));
				}
			} catch (Exception e) {
				//e.printStackTrace();
			}
			
			if(taskId != null && taskId >0) {
				Optional<Task> opTask = taskRepository.findById(taskId);
				if(opTask.isPresent()) {
					task = opTask.get();
					tasks = taskRepository.getByProcessAndTaskIdIsNot(task.getId(), process.getId());
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.addAttribute("sections", process.getSections());
		model.addAttribute("tasks", tasks);
		model.addAttribute("users", userRepository.findAll());
		model.addAttribute("groups", groupRepository.findAll());
		model.addAttribute("task", task);
		model.addAttribute("process", process);
		return "task";
	}
	@PostMapping("/Task/Edit")
	public String editTask(@RequestParam("pid") Long processId,@RequestParam("fileDescription") MultipartFile fileDescription,
							@RequestParam("grouprodio") int withGroup,@Valid @ModelAttribute("task") Task task, BindingResult bind, Model model) {
		boolean isUpdateOperation = false;
		List<Task> tasks = new ArrayList<>();
		List<ProcessSection> sections = new ArrayList<>();
		Processus process = null;
		try {
			if(bind.hasErrors()) {
				bind.getAllErrors().forEach(e->System.out.println(e.getDefaultMessage()));
			}else {
				  try {
					process = processRepository.getOne(processId);
				} catch (Exception e1) {
					return "redirect:/Process/List";
				}
				if(task.getSection() == null) {
					ProcessSection section = new ProcessSection();
					int sectionSize = (process.getSections() != null && !process.getSections().isEmpty()) ? process.getSections().size()+1 : 1;
					String textSectionSize = sectionSize < 10 ? "0"+sectionSize : sectionSize+"";
					section.setName("Section "+textSectionSize);
					section.setProcess(process);
					if(sectionRepository.save(section) != null)
						task.setSection(section);
				}
				if(process != null)
					tasks = taskRepository.getByProcess(process.getId());
				sections = processSectionRepository.findByProcess(process);
				if(task.getId() != null && task.getId() > 0) {
					isUpdateOperation = true;
					tasks = taskRepository.getByProcessAndTaskIdIsNot(task.getId(), process.getId());
				}
				if(withGroup==3)
					task.setGroup(null);
				// traitement du fichier
				if(fileDescription != null) {
					try {
						task.setFileDescriptionPath(storageService.storeFile(fileDescription, new String[] {"pdf"}));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				//end file filedescription traitements
				if(taskRepository.save(task) != null) {
					model.addAttribute("task", task);
					String successMsg = "";
					String notificationMsg = "";
					String notificationTitle = "";
					if(isUpdateOperation) {
						successMsg = "Tâche modifiée";
						notificationTitle = "Tâche mise à jour";
						notificationMsg = "La tâche \" "+task.getName()+" \" du process \" " + task.getSection().getProcess().getLabel() +" \" est mise à jour";
					}
					else {
						successMsg = "Tâche enregistrée";
						notificationTitle = "Nouvelle tâche créee";
						notificationMsg = "Vous avez une nouvelle tâche nomée "+task.getName()+" du process \" " + task.getSection().getProcess().getLabel() + " \"";
					}
					if(fileDescription != null && task.getFileDescriptionPath()==null)
						successMsg = successMsg + " sans le fichier de description(extension autorisée : pdf) !";
					else
						successMsg = successMsg+" !";
					model.addAttribute("successMsg", successMsg);
					
					//update process totol time
						try {
							Processus p = task.getSection().getProcess();
							p.setTotalTime(p.getMaxDate());
							processRepository.save(p);
							System.out.println("total time "+p.getTotalTime().toString());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					// storing notifications
					  notifService.notify(notificationTitle, notificationMsg, NotificationType.INFO.toString(),"/Task/?tid="+task.getId(), task.getAllUsers(), null);
					// end storing notification
				}else {
					model.addAttribute("errorMsg", "Echèc de l'enregistrement de données!");
				}
			}
		} catch (Exception e) {
			model.addAttribute("errorMsg", "Opération échouée, veuillez recommncer !");
			e.printStackTrace();
		}
		model.addAttribute("sections", sections);
		model.addAttribute("tasks", tasks);
		model.addAttribute("users", userRepository.findAll());
		model.addAttribute("groups", groupRepository.findAll());
		model.addAttribute("process", process);
		return "task";
	}
	
	@GetMapping("/Logigramme")
	public String logigramme(@RequestParam(name="pid",defaultValue="0") Long processId, Model model) {
		Processus p = null;
		try {
			if(processId != null && processId>0) {
				Optional<Processus>  op = processRepository.findById(processId);
				if(op.isPresent()) {
					p = new Processus();
					p = op.get();
				}
			}
			System.out.println(p.getId());
			model.addAttribute("pId", p.getId());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.addAttribute("process", p);
		return "logigramme";
	}
}
