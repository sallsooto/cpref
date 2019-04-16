package sn.cperf.controller;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import sn.cperf.dao.GroupRepository;
import sn.cperf.dao.ProcessRepository;
import sn.cperf.dao.ProcessSectionRepository;
import sn.cperf.dao.TaskRepository;
import sn.cperf.dao.UserRepository;
import sn.cperf.form.SectionForm;
import sn.cperf.form.TaskForm;
import sn.cperf.model.Group;
import sn.cperf.model.Notification;
import sn.cperf.model.Procedure;
import sn.cperf.model.ProcessSection;
import sn.cperf.model.Processus;
import sn.cperf.model.Task;
import sn.cperf.model.User;
import sn.cperf.service.CperfService;
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
	
	@GetMapping("/List")
	public String list(@RequestParam(name="s", defaultValue="") String searchKey, 
			@RequestParam(name="size", defaultValue="5") int size,
			@RequestParam(name="page", defaultValue="0") int page, Model model){
			List<Processus> process = new ArrayList<>();
			double pages = 0;
			try {
				List<Processus> tempProcess = new ArrayList<>();
				if(searchKey != null && !searchKey.equals("")) {
					System.err.println(searchKey);
					Page<Processus> pProcess = processRepository.findByLabelLikeIgnoreCaseOrderByIdDesc("%"+searchKey+"%", new PageRequest(page, size));
					System.err.println(pProcess.getContent().size());
					for(Processus p : pProcess.getContent()) {
						// loding process and verify id loged is atorized
						process = loadProcessus(process,p,size);
					}
				}else {
					Page<Processus> pProcess = processRepository.findByOrderByIdDesc(new PageRequest(page, size));
					for(Processus p : pProcess.getContent()) {
						// loding process and verify id loged is atorized
						process = loadProcessus(process,p,size);
					}
				}
				if(!process.isEmpty()) {
					pages = Math.floor(process.size()/size)+1;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			model.addAttribute("processes",process);
			model.addAttribute("pages", pages);
			model.addAttribute("currentPage", page);
		return "liste_process";
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
					// storing notifications
//					if(form.getId() != null && form.getId()>0) {
//						String title = "Nouvelle section du process " +section.getProcess().getLabel();
//						String link = "/Process/List/";
//						String note = "Une nouvelle section de tâches a été créée";
//						User sender = cperfService.getLoged();
//						if(form.getIntervenants() !=null) {
//							for(User user : section.getIntervenants()) {
//								Notification notif = new Notification();
//								notif.setNote(note);
//								notif.setTitle(title);
//								notif.setType(NotificationType.INFO);
//							}
//						}
//					}
					// end storing notification
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
	public String getTaskView(@RequestParam("sid") Long sectionId, @RequestParam(name="tid", defaultValue="0") Long taskId, Model model) {
		Task task = new Task();
		ProcessSection section = new ProcessSection();
		List<ProcessSection> sections = new ArrayList<>();
		List<Task> tasks  = new ArrayList<>();
		try {
			if(sectionId != null && sectionId >0) {
				Optional<ProcessSection> oSection = processSectionRepository.findById(sectionId);
				if(oSection.isPresent()) {
					section = oSection.get();
					if(section.getProcess() != null && section.getProcess().getId() != null)
						tasks = taskRepository.getByProcess(section.getProcess().getId());
					sections = processSectionRepository.findByProcess(section.getProcess());
				}
			}
			task.setSection(section);
			if(taskId != null && taskId >0) {
				Optional<Task> opTask = taskRepository.findById(taskId);
				if(opTask.isPresent()) {
					task = opTask.get();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.addAttribute("sections", sections);
		model.addAttribute("tasks", tasks);
		model.addAttribute("users", userRepository.findAll());
		model.addAttribute("groups", groupRepository.findAll());
		model.addAttribute("task", task);
		return "task";
	}
	@PostMapping("/Task/Edit")
	public String editTask(@RequestParam("sid") Long sectionId,@RequestParam("fileDescription") MultipartFile fileDescription,
							@RequestParam("grouprodio") int withGroup,@Valid @ModelAttribute("task") Task task, BindingResult bind, Model model) {
		boolean isUpdateOperation = false;
		List<Task> tasks = new ArrayList<>();
		List<ProcessSection> sections = new ArrayList<>();
		try {
			if(bind.hasErrors()) {
				bind.getAllErrors().forEach(e->System.out.println(e.getDefaultMessage()));
			}else {
				if(task.getSection() != null) {
					if(task.getSection().getProcess() != null && task.getSection().getProcess().getId() != null)
						tasks = taskRepository.getByProcess(task.getSection().getProcess().getId());
					sections = processSectionRepository.findByProcess(task.getSection().getProcess());
					if(task.getId() != null && task.getId() > 0) {
						isUpdateOperation = true;
						tasks = taskRepository.getByProcessAndTaskIdIsNot(task.getId(), task.getSection().getProcess().getId());
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
						String successMsg = "";
						if(isUpdateOperation)
							successMsg = "Tâche modifiée";
						else
							successMsg = "Tâche enregistrée";
						if(fileDescription != null && task.getFileDescriptionPath()==null)
							successMsg = successMsg + " sans le fichier de description(extension autorisée : pdf) !";
						else
							successMsg = successMsg+" !";
						model.addAttribute("successMsg", successMsg);
					}else {
						model.addAttribute("errorMsg", "Echèc de l'enregistrement de données!");
					}
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

	private List<Processus> loadProcessus(List<Processus> listProcess, Processus process,int size){
		try {
			if(process != null && listProcess != null && listProcess.size() < size) {
				User loged = cperfService.getLoged();
				if(loged.hasRole("admin") || loged.hasRole("Role_admin") || process.getSections() == null || process.getSections().isEmpty()) {
					listProcess.add(process);
				}else {
					if(process.getSections() != null) {
						for(ProcessSection section : process.getSections()) {
							if(section.getGroup() != null) {
								for(User user : section.getGroup().getUsers()) {
									if(user.getId() == loged.getId() && !processInList(listProcess,process))
										listProcess.add(process);
								}
							}
							for(User user : section.users()) {
								if(user.getId() == loged.getId() && !processInList(listProcess,process))
									listProcess.add(process);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listProcess;
	}
	private boolean processInList(List<Processus> listProcess, Processus process) {
		if(process != null) {
			for(Processus p : listProcess) {
				if(p.getId() == process.getId())
					return true;
			}
		}
		return false;
	}
	
    @RequestMapping(value = "/Task/File/Description/Show/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_PDF_VALUE)
    @ResponseBody
    public ResponseEntity<InputStreamResource> showProceduresPDf(@PathVariable(name = "id") Long taskId) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=procedure.pdf");
    	try {
			Task task = taskRepository.getOne(taskId);
			InputStream is = new FileInputStream(storageService.getFilePathInUploadDir(task.getFileDescriptionPath()).toFile());

			return ResponseEntity
			        .ok()
			        .headers(headers)
			        .contentType(MediaType.APPLICATION_PDF)
			        .body(new InputStreamResource(is));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
    	System.out.println("Fichier introuvable");
    	return null;
    }
}
