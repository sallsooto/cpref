package sn.cperf.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;
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
import sn.cperf.dao.IndicateurRepository;
import sn.cperf.dao.ObjectifRepository;
import sn.cperf.dao.ProcessRepository;
import sn.cperf.dao.ProcessSectionRepository;
import sn.cperf.dao.TaskRepository;
import sn.cperf.dao.UserRepository;
import sn.cperf.form.SectionForm;
import sn.cperf.model.DBFile;
import sn.cperf.model.Group;
import sn.cperf.model.Indicateur;
import sn.cperf.model.Objectif;
import sn.cperf.model.ProcessSection;
import sn.cperf.model.Processus;
import sn.cperf.model.Task;
import sn.cperf.model.User;
import sn.cperf.service.CperfService;
import sn.cperf.service.DBFileService;
import sn.cperf.service.NotificationService;
import sn.cperf.service.ProcessService;
import sn.cperf.service.StorageService;
import sn.cperf.util.NotificationType;
import sn.cperf.util.TaskStatus;

@Controller
@RequestMapping("/Process")
@Secured("ROLE_admin")
public class ProcessController {
	@Autowired
	ProcessRepository processRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	CperfService cperfService;
	@Autowired
	ProcessSectionRepository sectionRepository;
	@Autowired
	GroupRepository groupRepository;
	@Autowired
	ProcessSectionRepository processSectionRepository;
	@Autowired
	TaskRepository taskRepository;
	@Autowired
	StorageService storageService;
	@Autowired
	NotificationService notifService;
	@Autowired
	DBFileService dbFileService;
	@Autowired
	IndicateurRepository indicatorRepository;
	@Autowired
	ObjectifRepository ObjectifRepository;
	
	@Autowired ProcessService processService;

	@GetMapping("/Edit")
	public String process(@RequestParam(name = "pid", defaultValue = "0") Long processId, Model model) {
		Processus form = new Processus();
		if (processId != null && processId > 0) {
			Optional<Processus> optProcess = processRepository.findById(processId);
			if (optProcess.isPresent()) {
				Processus processus = optProcess.get();
				if (processus != null) {
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
			if (bind.hasErrors()) {
				bind.getAllErrors().forEach(e -> {
					System.out.println(e.getDefaultMessage());
				});
				model.addAttribute("errorMsg", "Des erreurs de validation trouvés!");
			} else {
				Processus process = new Processus();
				if (form.getId() != null) {
					Optional<Processus> optProcess = processRepository.findById(form.getId());
					if (optProcess.isPresent())
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
				if (processRepository.save(process) != null) {
					if (form.getId() != null && form.getId() > 0)
						model.addAttribute("successMsg", "Process modifié!");
					else
						model.addAttribute("successMsg", "Process enregistré!");
					form.setId(process.getId());
				} else {
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
			List<Task> tasks = p.getTasks();
			if (tasks != null) {
				for (Task t : tasks) {
					if (t.getObjectifs() != null && !t.getObjectifs().isEmpty()) {
						for (Objectif obj : t.getObjectifs()) {
							if (obj.getIndicators() != null && !obj.getIndicators().isEmpty()) {
								for (Indicateur ind : obj.getIndicators()) {
									indicatorRepository.delete(ind);
								}
							}
							ObjectifRepository.delete(obj);
						}
					}
					DBFile dbFiledescription = t.getDbFileDescription();
					taskRepository.delete(t);
					if (t.getDbFileDescription() != null)
						dbFileService.delete(dbFiledescription);
				}
			}
			processRepository.delete(p);
		} catch (Exception e) {
		}
		return "redirect:/Process/List";
	}

	@GetMapping("/List")
	public String list(@RequestParam(name = "s", defaultValue = "") String searchKey,
			@RequestParam(name = "size", defaultValue = "5") int size,
			@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
		List<Processus> process = new ArrayList<>();

		double pages = 0;
		try {
			if (searchKey != null && !searchKey.equals("")) {
				Page<Processus> pageProcess = processRepository
						.findByLabelLikeIgnoreCaseOrderByIdDesc("%" + searchKey + "%", new PageRequest(page, size));
				if (!pageProcess.isEmpty()) {
					process = pageProcess.getContent();
					pages = pageProcess.getTotalPages();
				}
			} else {
				Page<Processus> pageProcess = processRepository.findByOrderByIdDesc(new PageRequest(page, size));
				System.err.println("conten " + pageProcess.getContent().size());
				if (!pageProcess.isEmpty()) {
					process = pageProcess.getContent();
					pages = pageProcess.getTotalPages();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addAttribute("processes", process);
		model.addAttribute("pages", pages);
		model.addAttribute("currentPage", page);
		model.addAttribute("loged", cperfService.getLoged());
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
			if (processRepository.save(process) != null) {
				try {
					if (play) {
						for (Task task : process.getTasks()) {
							if (task.isLunchingByProcess()) {
								task.setStartAt(startDate);
								taskRepository.save(task);
							}
						}
					} else {
						for (Task task : process.getTasks()) {
							if (task.isLunchingByProcess() && task.getStatus().toLowerCase()
									.equals(TaskStatus.STARTED.toString().toLowerCase())) {
								task.setStartAt(null);
								task.setStatus(TaskStatus.VALID.toString().toLowerCase());
								taskRepository.save(task);
							}
						}
					}
				} catch (Exception e1) {
				}
				data.put("success", true);
				if (play) {
					SimpleDateFormat tf = new SimpleDateFormat("dd/MM/yyyy à HH:mm");
					data.put("startAt", tf.format(startDate));
					data.put("maxDate", tf.format(process.getMaxDate()));
					if (process.getTasks() != null && !process.getTasks().isEmpty()) {
						for (Task task : process.getTasks()) {
							if (task.getStatus().equals(TaskStatus.VALID.toString()) && task.isStatusValid()
									&& task.isLunchingByProcess()) {
								task.setStatus(TaskStatus.STARTED.toString());
								try {
									taskRepository.save(task);
								} catch (Exception e) {
								}
							}
						}
					}
				}
				// store Notifications
				try {
					String title = (play) ? "Un process démarré " : " Un process stoppé ";
					String noteSlug = (play) ? "demarré" : " arrêté";
					String note = "Le process << " + process.getLabel() + " >> est " + noteSlug + " !";
					notifService.notify(title, note, NotificationType.INFO.toString(), "/Task/", process.getAllUsers(),
							null);
				} catch (Exception e) {
				}
				// update process total_time 
				process.setTotalTime(process.getMaxDate());
				processRepository.save(process);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	@GetMapping("/Section/Edit")
	public String getEditSectionView(@RequestParam("pid") Long pid,
			@RequestParam(name = "sid", defaultValue = "0") Long sectionId, Model model) {
		try {
			Optional<Processus> opProcess = processRepository.findById(pid);
			ProcessSection section = new ProcessSection();
			if (opProcess.isPresent()) {
				Processus p = opProcess.get();
				section.setProcess(p);
				if (sectionId != null && sectionId > 0) {
					Optional<ProcessSection> opSection = sectionRepository.findById(sectionId);
					if (opSection.isPresent()) {
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
	public String editSection(@RequestParam("pid") Long pid, @Valid @ModelAttribute("sectionForm") SectionForm form,
			BindingResult bind, Model model) {
		Processus p = new Processus();
		try {
			p.setId(form.getProcessId());
			if (p.getId() != null && p.getId() > 0)
				p = processRepository.getOne(p.getId());
			if (bind.hasErrors()) {
				bind.getAllErrors().forEach(e -> {
					System.err.println(e.getDefaultMessage());
				});
			} else {
				ProcessSection section = new ProcessSection();
				if (form.isWithGroup() && form.getGroupId() != null && form.getGroupId() > 0) {
					Optional<Group> og = groupRepository.findById(form.getId());
					if (og.isPresent())
						section.setGroup(og.get());
				}
				section.setId(form.getId());
				section.setName(form.getName());
				section.setProcess(p);
				if (processSectionRepository.save(section) != null) {
					p = section.getProcess();
					if (form.getId() != null && form.getId() > 0)
						model.addAttribute("successMsg", "Section modifiée!");
					else
						model.addAttribute("successMsg", "Section enregistrée!");
					form.setId(section.getId());
				} else {
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
	public String getTaskView(@RequestParam(name = "pid") Long processId,
			@RequestParam(name = "tid", defaultValue = "0") Long taskId, Model model) {
		Task task = new Task();
		List<Task> tasks = new ArrayList<>();
		List<ProcessSection> sections = new ArrayList<>();
		Processus process = null;
		try {
			if (processId != null && processId > 0) {
				Optional<Processus> oProcess = processRepository.findById(processId);
				if (oProcess.isPresent()) {
					process = new Processus();
					process = oProcess.get();
					tasks = taskRepository.getByProcess(process.getId());
				} else {
					return "redirect:/Process/List/";
				}
			}

			try {
				if (!process.getSections().isEmpty()) {
					task.setSection(process.getSections().get(process.getSections().size() - 1));
				}
			} catch (Exception e) {
				// e.printStackTrace();
			}

			if (taskId != null && taskId > 0) {
				Optional<Task> opTask = taskRepository.findById(taskId);
				if (opTask.isPresent()) {
					task = opTask.get();
					tasks = taskRepository.getByProcessAndTaskIdIsNot(task.getId(), process.getId());
					if(task.getParent() != null && task.getParent().getSection().getId() != task.getSection().getId())
						sections.add(task.getParent().getSection());
					sections.add(task.getSection());
				}else {
					try {sections.add(process.getSections().get(process.getSections().size()-1));} catch (Exception e) {e.printStackTrace();}
				}
			}else {
				try {sections.add(process.getSections().get(process.getSections().size()-1));} catch (Exception e) {e.printStackTrace();}
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
		model.addAttribute("process", process);
		return "task";
	}

	@PostMapping("/Task/Edit")
	public String editTask(@RequestParam("pid") Long processId,
			@RequestParam("fileDescription") MultipartFile fileDescription,
			@RequestParam(name = "tid", defaultValue = "0") Long taskId, @RequestParam(name="grouprodio",defaultValue="3") int withGroup,
			@Valid @ModelAttribute("task") Task task, BindingResult bind, Model model) {
		boolean isUpdateOperation = false;
		List<Task> tasks = new ArrayList<>();
		List<ProcessSection> sections = new ArrayList<>();
		Processus process = null;
		int sectionFinded = 0;
		try {
			if (bind.hasErrors()) {
				bind.getAllErrors().forEach(e -> System.out.println(e.getDefaultMessage()));
			} else {
				try {
					process = processRepository.getOne(processId);
				} catch (Exception e1) {
					return "redirect:/Process/List";
				}
				if (task.getSection() == null) {
					ProcessSection section = new ProcessSection();
					int sectionSize = (process.getSections() != null && !process.getSections().isEmpty())
							? process.getSections().size() + 1
							: 1;
					String textSectionSize = sectionSize < 10 ? "0" + sectionSize : sectionSize + "";
					section.setName("Section " + textSectionSize);
					section.setProcess(process);
					if (sectionRepository.save(section) != null)
						task.setSection(section);
				}
				if (process != null)
					tasks = taskRepository.getByProcess(process.getId());
				//sections = processSectionRepository.findByProcess(process);
				try {
					if (task.getValidator() != null) {
						List<User> users = new ArrayList<>();
						if (task.getUsers() != null || !task.getUsers().isEmpty())
							users = task.getUsers();
						System.err.println("users " + task.getAllUsers().size());
						if (!task.getAllUsers().contains(task.getValidator())) {
							System.err.println("Validator " + task.getValidator().getFirstname());
							users.add(task.getValidator());
						}
						task.setUsers(users);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				if (task.getId() != null && task.getId() > 0) {
					isUpdateOperation = true;
					tasks = taskRepository.getByProcessAndTaskIdIsNot(task.getId(), process.getId());
				}
				if (withGroup == 3)
					task.setGroup(null);
				// traitement du fichier
				if (fileDescription != null) {
					try {
						if (dbFileService.checkExtensions(fileDescription.getOriginalFilename(),
								new String[] { "pdf" })) {
							task.setDbFileDescription(dbFileService.storeOrUpdateFile(fileDescription,
									task.getDbFileDescription() != null ? task.getDbFileDescription().getId() : null,
									false));
							if (task.getDbFileDescription() == null)
								task.setFileDescriptionPath(
										storageService.storeFile(fileDescription, new String[] { "pdf" }));
						}
					} catch (Exception e) {
						// e.printStackTrace();
					}
				}
				// end file filedescription traitements
				// lanching task or note
				if (task.getId() == null) {
					if (task.isLunchingByProcess()) {
						try {
							Processus p = processRepository.getOne(task.getProcessId());
							if (p != null && p.getStartAt() != null) {
								task.setStatus(TaskStatus.STARTED.toString().toLowerCase());
								task.setStartAt(new Date());
								sections.add(p.getSections().get(p.getSections().size()-1));
							}
						} catch (Exception e) {
						}
					} else {
						if (task.getParent() != null && task.getParent().getStatus().toLowerCase()
								.equals(TaskStatus.COMPLETED.toString().toLowerCase())) {
							task.setStatus(TaskStatus.STARTED.toString().toLowerCase());
						    task.setStartAt(new Date());
						}
					}
					if(task.getParent() != null) {
						sectionFinded = 0;
						 for(ProcessSection section : sections) {
							 if(section.getId() == task.getParent().getSection().getId()) {
								 sectionFinded++;
								 break;
							 }
						 }
						 if(sectionFinded <=0)
							 sections.add(task.getParent().getSection());
					}
					if(task.getSection() != null) {
						sectionFinded = 0;
						 for(ProcessSection section : sections) {
							 if(section.getId() == task.getSection().getId()) {
								 sectionFinded++;
								 break;
							 }
						 }
						 if(sectionFinded <=0)
								sections.add(task.getSection());
					}
				}
				// seting finish date
				if (task.getStatus().toLowerCase().equals(TaskStatus.COMPLETED.toString().toLowerCase())) {
					if(task.getFinishAt() == null)
						task.setFinishAt(new Date());
				}
				else {
					task.setFinishAt(null);
				}
				// set staring task date
				if (!task.getStatus().toLowerCase().equals(TaskStatus.VALID.toString().toLowerCase())) {
					if(task.getStartAt() == null)
						task.setStartAt(new Date());
				}
				else {
					task.setStartAt(null);
				}
				// en setting start task date
				if (taskRepository.save(task) != null) {
					model.addAttribute("task", task);
					String successMsg = "";
					String notificationMsg = "";
					String notificationTitle = "";
					if (isUpdateOperation) {
						successMsg = "Tâche modifiée";
						notificationTitle = "Tâche mise à jour";
						notificationMsg = "La tâche \" " + task.getName() + " \" du process \" "
								+ task.getSection().getProcess().getLabel() + " \" est mise à jour";
					} else {
						successMsg = "Tâche enregistrée";
						notificationTitle = "Nouvelle tâche créee";
						notificationMsg = "Vous avez une nouvelle tâche nomée " + task.getName() + " du process \" "
								+ task.getSection().getProcess().getLabel() + " \"";
					}
					if (fileDescription != null && task.getFileDescriptionPath() == null)
						successMsg = successMsg + " sans le fichier de description(extension autorisée : pdf) !";
					else
						successMsg = successMsg + " !";
					model.addAttribute("successMsg", successMsg);

					// update process totol time and finish data if nessary
					try {
						Processus p = task.getSection().getProcess();
						p.setTotalTime(p.getMaxDate());
						if(!task.getStatus().toLowerCase().equals(TaskStatus.CANCELED.toString().toLowerCase())
								   && !task.getStatus().toLowerCase().equals(TaskStatus.COMPLETED.toString().toLowerCase()))
									process.setFinishDate(null);
						processRepository.save(p);
						System.out.println("total time " + p.getTotalTime().toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// storing notifications
					notifService.notify(notificationTitle, notificationMsg, NotificationType.INFO.toString(),
							"/Task/?tid=" + task.getId(), task.getAllUsers(), null);
					// lunching task chirlds if is necessary
					processService.startChirldTaskStatusWhenIsNecessary(task);
					// finishing process if is necessary
					 processService.finishProcessWhenIsTime(process);
					// end finishing process op
					 sectionFinded = 0;
					 for(ProcessSection section : sections) {
						 if(section.getId() == task.getSection().getId()) {
							 sectionFinded++;
							 break;
						 }
					 }
					 if(sectionFinded <=0)
						 sections.add(task.getSection());
				} else {
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

	// cette version n'est pas utilisée
	@GetMapping("/UnusedLogigramme")
	public String logigramme(@RequestParam(name = "pid", defaultValue = "0") Long processId,
			@RequestParam(name = "tid", defaultValue = "0") Long taskId, Model model) {
		Processus p = null;
		Task task = null;
		List<Task> tasks = new ArrayList<>();
		List<ProcessSection> sections = new ArrayList<>();
		List<User> users = new ArrayList<>();
		List<Group> groups = new ArrayList<>();
		try {
			if (processId != null && processId > 0) {
				Optional<Processus> op = processRepository.findById(processId);
				if (op.isPresent()) {
					p = new Processus();
					p = op.get();
					sections = p.getSections();
				}
				model.addAttribute("pId", p.getId());
	
				if (taskId != null && taskId > 0) {
					Optional<Task> opTask = taskRepository.findById(taskId);
					if (opTask.isPresent()) {
						task = new Task();
						task = opTask.get();
						tasks = taskRepository.getByProcessAndTaskIdIsNot(task.getId(), p.getId());
					}else {
						tasks = taskRepository.getByProcess(p.getId());
					}
				}
				if (task == null) {
					task = new Task();
					tasks = p.getTasks();
				}
			}
			users = userRepository.findAll();
			groups = groupRepository.findAll();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// suppressions des tasche vies 
		deleteAllProcessSectionsWithoutTask();
		
		model.addAttribute("sections", sections);
		model.addAttribute("tasks", tasks);
		model.addAttribute("users", users);
		model.addAttribute("groups", groups);
		model.addAttribute("task", task);
		model.addAttribute("process", p);
		return "logigramme";
	}
	@PostMapping("/Logigramme")
	public String editTaskInLogigramme(@RequestParam(name = "pid", defaultValue = "0") Long processId,
			@RequestParam("fileDescription") MultipartFile fileDescription,
			@Valid @ModelAttribute("task") Task task, BindingResult bind, Model model,HttpSession session) {
		    List<Task> tasks = new ArrayList<>();
			List<ProcessSection> sections = new ArrayList<>();
			List<User> users = new ArrayList<>();
			List<Group> groups = new ArrayList<>();
			Long taskId = null;
			boolean tasSectionChanged = false;
		Processus process = null;
		try {
			if (bind.hasErrors()) {
				bind.getAllErrors().forEach(e -> System.out.println(e.getDefaultMessage()));
			} else {
				try {
					if(session.getAttribute("successMsg") != null)
						session.removeAttribute("successMsg");
					if(session.getAttribute("errorMsg") != null)
					 session.removeAttribute("errorMsg");
					Task dbTask = taskRepository.getOne(task.getId());
					if(dbTask != null) {
						tasSectionChanged = task.getSection() != null && task.getSection().getId() == dbTask.getSection().getId() ? false : true;
						taskId = dbTask.getId();
						process = dbTask.getSection().getProcess();
						dbTask.setGroup(task.getGroup());
						dbTask.setName(task.getName() != null && !task.getName().equals("") ? task.getName() : dbTask.getName());
						dbTask.setNbYears(task.getNbYears());
						dbTask.setNbMonths(task.getNbMonths());
						dbTask.setNbDays(task.getNbDays());
						dbTask.setNbHours(task.getNbHours());
						dbTask.setNbMinuites(task.getNbMinuites());
						dbTask.setDescription(task.getDescription());
						dbTask.setType(task.getType());
						dbTask.setStatus(task.getStatus());
						if(dbTask.getStartAt() == null && dbTask.getStatus().toLowerCase().equals(TaskStatus.STARTED.toString().toLowerCase()))
							dbTask.setStartAt(new Date());
						// traitement du fichier
						if (fileDescription != null) {
							try {
								if (dbFileService.checkExtensions(fileDescription.getOriginalFilename(),
										new String[] { "pdf" })) {
									task.setDbFileDescription(dbFileService.storeOrUpdateFile(fileDescription,
											task.getDbFileDescription() != null ? task.getDbFileDescription().getId() : null,
											false));
									if (task.getDbFileDescription() == null)
										task.setFileDescriptionPath(
												storageService.storeFile(fileDescription, new String[] { "pdf" }));
								}
							} catch (Exception e) {
								// e.printStackTrace();
							}
						}
						// section description traitements 
						dbTask.setDbFileDescription(task.getDbFileDescription() != null ? task.getDbFileDescription() : dbTask.getDbFileDescription());
						dbTask.setFileDescriptionPath(task.getFileDescriptionPath() != null ? task.getFileDescriptionPath() : dbTask.getFileDescriptionPath());
						if (task.getSection() == null) {
							try {
								ProcessSection section = new ProcessSection();
								int sectionSize = (process.getSections() != null && !process.getSections().isEmpty())
										? process.getSections().size() + 1
										: 1;
								String textSectionSize = sectionSize < 10 ? "0" + sectionSize : sectionSize + "";
								section.setName("Section " + textSectionSize);
								section.setProcess(process);
								if (sectionRepository.save(section) != null)
									task.setSection(section);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						// en section description traitements
						dbTask.setSection(task.getSection() != null ? task.getSection() : dbTask.getSection());
						// seting finish date
						if (dbTask.getStatus().equals(TaskStatus.COMPLETED.toString())) {
							if(dbTask.getFinishAt() == null)
								dbTask.setFinishAt(new Date());
						}
						else {
							dbTask.setFinishAt(null);
						}
						// set staring task date
						if (!dbTask.getStatus().toLowerCase().equals(TaskStatus.VALID.toString().toLowerCase())) {
							if(dbTask.getStartAt() == null)
								dbTask.setStartAt(new Date());
						}
						else {
							dbTask.setStartAt(null);
						}
						// end setting start task date
						if (taskRepository.save(dbTask) != null) {
							try {
								process = dbTask.getSection().getProcess();
								sections = process.getSections();
								tasks = taskRepository.getByProcessAndTaskIdIsNot(dbTask.getId(), process.getId());
								users =  userRepository.findAll();
								groups = groupRepository.findAll();
								model.addAttribute("task", dbTask);
							} catch (Exception e1) {
							}
							
							String successMsg ="Tâche modifiée";
							String notificationTitle = "Tâche mise à jour";
							String notificationMsg = "La tâche \" " + dbTask.getName() + " \" du process \" "
									+ dbTask.getSection().getProcess().getLabel() + " \" est mise à jour";
								
							if (fileDescription != null && dbTask.getFileDescriptionPath() == null && dbTask.getDbFileDescription() == null)
								successMsg = successMsg + " sans le fichier de description(extension autorisée : pdf) !";
							else
								successMsg = successMsg + " !";
							session.setAttribute("successMsg", successMsg);

							// update process totol time and finish_date if necessary
							try {
								process.setTotalTime(process.getMaxDate());
								if(!dbTask.getStatus().toLowerCase().equals(TaskStatus.CANCELED.toString().toLowerCase())
								   && !dbTask.getStatus().toLowerCase().equals(TaskStatus.COMPLETED.toString().toLowerCase()))
									process.setFinishDate(null);
								processRepository.save(process);
								System.out.println("total time " + process.getTotalTime().toString());
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							// lunching task chirlds if is necessary
							processService.startChirldTaskStatusWhenIsNecessary(dbTask);
							// storing notifications
							notifService.notify(notificationTitle, notificationMsg, NotificationType.INFO.toString(),
									"/Task/?tid=" + dbTask.getId(), dbTask.getAllUsers(), null);
							// finishing process if is necessary
							 processService.finishProcessWhenIsTime(process);
							// end finishing process op
						} else {
							session.setAttribute("errorMsg", "Echèc de la modification de la tâche");
						}
					}else {
						session.setAttribute("errorMsg", "Tâche introuvable");
					}
				} catch (Exception e1) {
					return "redirect:/Process/List";
				}
			}
		} catch (Exception e) {
			session.setAttribute("errorMsg", "Opération échouée, veuillez recommncer !");
			e.printStackTrace();
		}
		
		// suppressions des tasche vies 
		deleteAllProcessSectionsWithoutTask();
		System.err.println("je suis redirigé");
		return "redirect:/Process/Logigramme/?pid="+process.getId();
	}
	
	private void deleteAllProcessSectionsWithoutTask() {
		try {
			List<ProcessSection> emptySections = processSectionRepository.findByTasksIsNullOrProcessIsNull();
			if(!emptySections.isEmpty()) {
				emptySections.forEach(ps->{
					processSectionRepository.delete(ps);
				});
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/** Logigramme with flowchart.js and raphael.js **/


	@GetMapping("/Logigramme")
	public String svgLogigramme(@RequestParam(name = "pid", defaultValue = "0") Long processId,
			@RequestParam(name = "tid", defaultValue = "0") Long taskId, Model model) {
		Processus p = null;
		Task task = null;
		List<Task> tasks = new ArrayList<>();
		List<ProcessSection> sections = new ArrayList<>();
		List<User> users = new ArrayList<>();
		List<Group> groups = new ArrayList<>();
		try {
			if (processId != null && processId > 0) {
				Optional<Processus> op = processRepository.findById(processId);
				if (op.isPresent()) {
					p = new Processus();
					p = op.get();
					sections = p.getSections();
				}
				model.addAttribute("pId", p.getId());
	
				if (taskId != null && taskId > 0) {
					Optional<Task> opTask = taskRepository.findById(taskId);
					if (opTask.isPresent()) {
						task = new Task();
						task = opTask.get();
						tasks = taskRepository.getByProcessAndTaskIdIsNot(task.getId(), p.getId());
					}else {
						tasks = taskRepository.getByProcess(p.getId());
					}
				}
				if (task == null) {
					task = new Task();
					tasks = p.getTasks();
				}
			}
			users = userRepository.findAll();
			groups = groupRepository.findAll();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// suppressions des tasche vies 
		deleteAllProcessSectionsWithoutTask();
		
		model.addAttribute("sections", sections);
		model.addAttribute("tasks", tasks);
		model.addAttribute("users", users);
		model.addAttribute("groups", groups);
		model.addAttribute("task", task);
		model.addAttribute("process", p);
		model.addAttribute("raphaelCode", makeRaphaelJsCode(tasks));
		return "logigramme_with_raphael";
	}
	
	private String makeRaphaelJsCode(List<Task> tasks) {
		String code = "", path="";
		if(!tasks.isEmpty()) {
			int nbTask = tasks.size();
			try {tasks = tasks.stream().sorted(Comparator.comparingLong(Task::getId)).collect(Collectors.toList());} catch (Exception e1) {}
			for(int i=0; i<nbTask; i++) {
				Task task = tasks.get(i);
				Task futureTask = null;
				if(i < nbTask-1)
					futureTask = tasks.get(i+1);
				String key = "t"+task.getId();
				String type = formateRaphaelTaskType(task.getType());
				String orientation = "";
				if(i < nbTask -1) {
					orientation = "(right)->";
					try {
						if(futureTask != null && task.getSection().getId() !=  futureTask.getSection().getId()) {
							orientation = "(bottom)->";
						}
					} catch (Exception e) {
					}
					//System.err.println(task.getName() + ": orientation => "+orientation);
				}
				code = code + " "+key+"=>"+type+": "+task.getName()+"|"+task.getStatus()+":$showTaskDetails  \n";
				path = path+key+orientation;
			}
		}
		return code +" "+path;
	}
	
	private String formateRaphaelTaskType(String type) {
		if(type != null) {
			if(type.toLowerCase().equals("start"))
				return "start";
			else if(type.toLowerCase().equals("finish"))
				return "end";
			else if(type.toLowerCase().equals("sub_process"))
				return "subroutine";
			else if(type.toLowerCase().equals("doc"))
				return "inputoutput";
			else
				return "operation";
		}
		return "operation";
	}
}
