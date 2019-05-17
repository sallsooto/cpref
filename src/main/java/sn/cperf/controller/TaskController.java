package sn.cperf.controller;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import sn.cperf.dao.ProcessRepository;
import sn.cperf.dao.TaskRepository;
import sn.cperf.dao.UserRepository;
import sn.cperf.model.Processus;
import sn.cperf.model.Task;
import sn.cperf.model.User;
import sn.cperf.service.CperfService;
import sn.cperf.service.NotificationService;
import sn.cperf.service.StorageService;
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

	@GetMapping("/")
	public String getListTaskView() {
		return "tasks";
	}

	@GetMapping("/userTasksJson")
	@ResponseBody
	public Map<String, Object> getUserTasksJson(@RequestParam(name = "name", defaultValue = "") String name,
			@RequestParam(name = "status", defaultValue = "all") String status,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "7") int size,
			@RequestParam(name = "processLunshed", defaultValue = "true") boolean lunchedProcessStatus) {
		System.out.println("status " + status);
		status = (status == null) ? "valid" : status;
		name = (name == null) ? "" : name;
		Map<String, Object> datas = new HashMap<>();
		List<Task> tasks = new ArrayList<>();
		int totalPages = 0;
		try {
			User loged = cperfService.getLoged();
			if (loged != null) {
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
			InputStream is = new FileInputStream(
					storageService.getFilePathInUploadDir(task.getFileDescriptionPath()).toFile());

			return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
					.body(new InputStreamResource(is));
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
					task.setStatus(status);
					if (taskRepository.save(task) != null) {
						task.getAllUsers().forEach(u -> {
							System.err.println(u.getId() + " " + u.getFirstname() + " " + u.getLastname());
						});
						// storing notifications
						try {
							String title = (status.toLowerCase().equals("valid")) ? "Tâche relancée "
									: (status.toLowerCase().equals("completed")) ? " Tâche completée"
											: "Tâche annulée!";
							String noteSlug = (status.toLowerCase().equals("valid"))
									? "a relancé le traitement de la tâche "
									: (status.toLowerCase().equals("completed"))
											? " a completé le traitement de la tâche "
											: " a anullé le traitement de la tâche ";
							String note = loged.getFirstname() + " " + loged.getLastname() + " " + noteSlug + " "
									+ task.getName() + " du processus " + task.getSection().getProcess().getLabel();
							notifService.notify(title, note, NotificationType.INFO.toString(),
									"/Task/?tid=" + task.getId(), task.getAllUsers(), null);
						} catch (Exception e) {
							e.printStackTrace();
						}
						// store process finisAt date if is laste task completed
						try {
							Processus processForTask = task.getSection().getProcess();
							if (status.toLowerCase().equals(TaskStatus.COMPLETED.toString().toLowerCase()) 
								|| status.toLowerCase().equals(TaskStatus.CANCELED.toString().toLowerCase())) {
								if (processForTask.getIsFinished()) {
									processForTask.setFinishDate(new Date());
									processRepository.save(processForTask);
								}
							}else {
								if(processForTask.getFinishDate() != null) {
									processForTask.setFinishDate(null);
									processRepository.save(processForTask);
								}
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
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
}