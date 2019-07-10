package sn.cperf.controller;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import sn.cperf.dao.GroupRepository;
import sn.cperf.dao.ProcessRepository;
import sn.cperf.dao.ProcessSectionRepository;
import sn.cperf.dao.TaskRepository;
import sn.cperf.dao.UserRepository;
import sn.cperf.model.ProcessSection;
import sn.cperf.model.Processus;
import sn.cperf.model.Task;
import sn.cperf.model.User;
import sn.cperf.service.CperfService;
import sn.cperf.service.DBFileService;
import sn.cperf.service.NotificationService;
import sn.cperf.service.ProcessService;
import sn.cperf.service.StorageService;
import sn.cperf.service.TaskService;
import sn.cperf.util.NotificationType;
import sn.cperf.util.TaskStatus;

@Controller
@RequestMapping("/Task")
public class TaskController {
	@Autowired
	CperfService cperfService;
	@Autowired
	TaskRepository taskRepository;
	@Autowired
	StorageService storageService;
	@Autowired
	UserRepository userRepository;
	@Autowired
	NotificationService notifService;
	@Autowired
	ProcessRepository processRepository;
	@Autowired DBFileService dbFileService;
	@Autowired GroupRepository groupRepository;
	@Autowired ProcessSectionRepository processSectionRepository;
	@Autowired ProcessService processService;
	@Autowired TaskService taskService;
	@GetMapping("/")
	public String getListTaskView(Model model) {
		User loged =  cperfService.getLoged();
		model.addAttribute("loged",loged);
		boolean logedIsAdmin = false;
		try { logedIsAdmin = loged.hasRole("admin") ? true : false;} catch (Exception e) {}
		model.addAttribute("logedIsAdmin",logedIsAdmin);
		return "tasks";
	}

	@GetMapping("/userTasksJson")
	@ResponseBody
	public Map<String, Object> getUserTasksJson(@RequestParam(name = "name", defaultValue = "") String name,
			@RequestParam(name = "status", defaultValue = "all") String status,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "7") int size,
			@RequestParam(name = "processLunshed", defaultValue = "true") boolean lunchedProcessStatus) {
		status = (status == null) ? "valid" : status;
		name = (name == null) ? "" : name;
		Map<String, Object> datas = new HashMap<>();
		List<Task> tasks = new ArrayList<>();
		int totalPages = 0;
		try {
			User loged = cperfService.getLoged();
			datas.put("loged", loged);
			if (loged != null) {
				boolean logedIsAdmin = false;
				try { logedIsAdmin = loged.hasRole("admin") ? true : false;} catch (Exception e) {}
				datas.put("logedIsAdmin",logedIsAdmin);
				status = (status.toLowerCase().equals(TaskStatus.CANCELED.toString()) && !loged.hasRole("admin"))
						? "valid"
						: status;
				List<Task> Alltasks = new ArrayList<>();
				if (status.toLowerCase().equals("all"))
					Alltasks = taskRepository.searchTasksStatusNotANdNameOrDescrion(status, "%" + name + "%",
							"%" + name + "%");
				else
					Alltasks = taskRepository.searchTasksStatusANdNameOrDescrion(status, "%" + name + "%",
							"%" + name + "%");
				if (Alltasks != null && !Alltasks.isEmpty()) {
					List<Task> tasksForLoged = new ArrayList<>();
					for (Task t : Alltasks) {
						if (isTaskForUser(t, loged, lunchedProcessStatus))
							tasksForLoged.add(t);
					}
					int index = 0, addTask = 1, cptItter = 1;
					for (Task task : tasksForLoged) {
						if (addTask <= size + 1) {
							if (index >= page) {
								tasks.add(task);
								addTask++;
							}
						}
						index++;
						if (cptItter == size) {
							totalPages++;
							cptItter = 1;
						} else {
							cptItter++;
						}
					}
					if (!tasksForLoged.isEmpty() && size > 0) {
						if ((tasksForLoged.size() > (size * totalPages)) || (!tasks.isEmpty() && totalPages == 0)) {
							totalPages++;
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		datas.put("tasks", tasks);
		datas.put("totalPages", totalPages);
		return datas;
	}

	@RequestMapping(value = "/File/Description/Show/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_PDF_VALUE)
	@ResponseBody
	public ResponseEntity<InputStreamResource> showProceduresPDf(@PathVariable(name = "id") Long taskId) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=procedure.pdf");
		try {
			Task task = taskRepository.getOne(taskId);
			if(task.getDbFileDescription() != null) {
				// show if file exist on db
				System.err.println("c'est avec la base");
				return  dbFileService.showPDfOnBrower(task.getDbFileDescription());
			}else {
				// show if file exist on disk
				InputStream is = new FileInputStream(
						storageService.getFilePathInUploadDir(task.getFileDescriptionPath()).toFile());
	
				return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
					.body(new InputStreamResource(is));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		System.out.println("Fichier introuvable");
		return null;
	}

	@GetMapping("/{taskId}/Status")
	@ResponseBody
	public boolean changeTaskStatusWithAssync(@PathVariable("taskId") Long taskId,
			@RequestParam("status") String status) {
		try {
			Optional<Task> opTask = taskRepository.findById(taskId);
			User loged = cperfService.getLoged();
			if (opTask.isPresent() && loged != null) {
				Task task = opTask.get();
				if (isTaskForUser(task, loged, task.isProcessLunched()) || loged.hasRole("admin")) {
					// List<Task> tasks = loged.getTasks();
					task.setLastStatus(task.getStatus());
					task.setStatus(status);
					task.setStatusValid(false);
					// seting finish date
					if (task.getStatus().equals(TaskStatus.COMPLETED.toString())) {
						if(task.getFinishAt() == null)
							task.setFinishAt(new Date());
					}
					else {
						task.setFinishAt(null);
					}
					// set staring task date
					if (!task.getStatus().toLowerCase().equals(TaskStatus.STARTED.toString().toLowerCase())) {
						if(task.getStartAt() == null)
							task.setStartAt(new Date());
					}
					else {
						task.setStartAt(null);
					}
					// en setting start task date
					if (taskRepository.save(task) != null) {
						
						// lunching task chirlds if is necessary
						processService.startChirldTaskStatusWhenIsNecessary(task);
						
						// store process finisAt date if is laste task completed
						processService.finishProcessWhenIsTime(task.getSection().getProcess());
						// storing notifications
						try {
							String title = (status.toLowerCase().equals("started")) ? "Tâche relancée "
									: (status.toLowerCase().equals("completed")) ? " Tâche completée"
											: "Tâche annulée!";
							String noteSlug = (status.toLowerCase().equals("started"))
									? "a relancé le traitement de la tâche "
									: (status.toLowerCase().equals("completed"))
											? " a completé le traitement de la tâche "
											: " a anullé le traitement de la tâche ";
							String note = loged.getFirstname() + " " + loged.getLastname() + " " + noteSlug + " "
									+ task.getName() + " du processus " + task.getSection().getProcess().getLabel();
							if (task.getValidator() != null)
								notifService.notify(title, note, NotificationType.ALERT.toString(), task.getValidator(),
										"/Task/");
							notifService.notify(title, note, NotificationType.INFO.toString(),
									"/Task/?tid=" + task.getId(), task.getAllUsers(), null);

						} catch (Exception e) {
							e.printStackTrace();
						}
//						try {
//							Processus processForTask = task.getSection().getProcess();
//							if (status.toLowerCase().equals(TaskStatus.COMPLETED.toString().toLowerCase())
//									|| status.toLowerCase().equals(TaskStatus.CANCELED.toString().toLowerCase())) {
//								if (processForTask.getIsFinished()) {
//									processForTask.setFinishDate(new Date());
//									processRepository.save(processForTask);
//								}
//							} else {
//								if (processForTask.getFinishDate() != null) {
//									processForTask.setFinishDate(null);
//									processRepository.save(processForTask);
//								}
//							}
//						} catch (Exception e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean isTaskForUser(Task task, User user, boolean lunchedProcessStatus) {
		try {
			if (task != null && user != null && task.isProcessLunched() == lunchedProcessStatus) {
				List<User> users = task.getAllUsers();
				if (users.contains(user))
					return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@GetMapping("/getTaskByIdJson")
	@ResponseBody
	public Task getTaskByIdJson(@RequestParam("tid") Long id) {
		try {
			return taskRepository.getOne(id);
		} catch (Exception e) {
		}
		return null;
	}

	@GetMapping("/changeStatusValid")
	@ResponseBody
	public Map<String, Object> changeStatusValid(@RequestParam("tid") Long taskId,
			@RequestParam("valid") boolean valid) {
		Map<String, Object> data = new HashMap<>();
		try {
			Task task = taskRepository.getOne(taskId);
			data.put("status", false);
			if (task != null && task.getStatus() != null) {
				if (task.getValidator() == null || task.getValidator().getId() == cperfService.getLoged().getId()) {
					task.setStatusValid(valid);
					if (!valid && task.getLastStatus() != null) {
						String currentStatus = task.getStatus();
						task.setStatus(task.getLastStatus());
						task.setLastStatus(currentStatus);
					}
					// seting finish date
					if (task.getStatus().equals(TaskStatus.COMPLETED.toString())) {
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
						// finish process op
						try {
							Processus p = processRepository.getOne(task.getProcessId());
							processService.finishProcessWhenIsTime(p);
						} catch (Exception e1) {
						}
						// en process finishing op
						data.put("status", true);
						String textStatus = "";
						if (task.getStatus().equals(TaskStatus.STARTED.toString())) {
							textStatus = "En état de traitement";
						}
						else if (task.getStatus().equals(TaskStatus.COMPLETED.toString())) {
							textStatus = "Traitée";
							try {
								// lunch started
								if(task.getChirlds() != null && !task.getChirlds().isEmpty()) {
									for(Task chirld : task.getChirlds()) {
										if(chirld.getStatus().toLowerCase().equals(TaskStatus.VALID.toString().toLowerCase())) {
											chirld.setStatus(TaskStatus.STARTED.toString().toLowerCase());
											chirld.setStartAt(new Date());
											taskRepository.save(chirld);
										}
									}
								}
							} catch (Exception e) {
							}
						}
						else {
							textStatus = "Traitement annulé";
						}
						data.put("textStatus", textStatus);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	
	@GetMapping("/LoadTaskInModel")
	public String LoadTaskInModel(@RequestParam("tid") Long taskId, Model model, HttpSession session) {
		try {
			// suppression des section vide
			deleteAllProcessSectionsWithoutTask();
			
		  Task t = taskRepository.getOne(taskId);
		  if(t != null) {
			    Processus process = processRepository.getOne(t.getProcessId());
			  	List<ProcessSection> sections = new ArrayList<>();
			  	if(t.getParent() != null && t.getParent().getSection() != null && 
			  		t.getSection() != null && t.getParent().getSection().getId() != t.getSection().getId()) {
			  		ProcessSection parentSection = t.getParent().getSection();
			  		parentSection.setName("Section précédente");
			  		sections.add(parentSection);
			  	}
			  	if(t.getParent() != null && t.getParent().getSection() != null && t.getSection().getId() == t.getParent().getSection().getId()) {
			  		ProcessSection newSection = new ProcessSection();
			  		newSection.setName("Nouvelle section");
			  		sections.add(newSection);
			  	}
			  	sections.add(t.getSection());
			  	if(sections.isEmpty())
			  		sections = process.getSections();
				model.addAttribute("sections", sections);
				model.addAttribute("tasks", taskRepository.getByProcessAndTaskIdIsNot(process.getId(), t.getId()));
				model.addAttribute("users", userRepository.findAll());
			    model.addAttribute("task", t);
				model.addAttribute("groups", groupRepository.findAll());
				model.addAttribute("process", process);
				if(session.getAttribute("errorMsg") != null && !session.getAttribute("errorMsg").equals("")) {
					model.addAttribute("errorMsg", session.getAttribute("errorMsg"));
					session.removeAttribute("errorMsg");
				}
				if(session.getAttribute("successMsg") != null && !session.getAttribute("successMsg").equals("")) {
					model.addAttribute("successMsg", session.getAttribute("successMsg"));
					session.removeAttribute("successMsg");
				}
			  return "logigramme_with_raphael :: #taskForm";
		  }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@GetMapping("/loadTasksChronoData")
	@ResponseBody
	public Map<String,Object> LoadTaskInModel(@RequestParam("tid") Long taskId) {
		Map<String, Object> datas = new HashMap<>();
		String startAt = "";
		String maxDate = "";
		String finishAt = "";
		boolean finishState = false;
		try {
			Task t = taskRepository.getOne(taskId);
			if(t != null  && t.getStartAt() != null) {
				SimpleDateFormat tf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				startAt = tf.format(t.getStartAt());
				maxDate = tf.format(t.getMaxDate());
				if(t.getFinishAt() != null) {
					finishState = true;
					finishAt = tf.format(t.getFinishAt());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		datas.put("startAt", startAt);
		datas.put("maxDate", maxDate);
		datas.put("finishAt",finishAt);
		datas.put("finishState", finishState);
		return datas;
	}
	
	@GetMapping("/relunchTask")
	@ResponseBody
	@Transactional
	public void relunchTask(@RequestParam("tid") Long taskId) {
		try {
			Task task = taskRepository.getOne(taskId);
			Processus process = processRepository.getOne(task.getProcessId());
			task.setLastStatus(null);
			task.setStatus(TaskStatus.STARTED.toString().toLowerCase());
			task.setFinishAt(null);
			process.setFinishDate(null);
			taskRepository.save(task);
			// notify childs tasks if is task lunching
			taskService.notifyChilrdIfThisParentTaskIsLunched(task);
			processRepository.save(process);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
}
