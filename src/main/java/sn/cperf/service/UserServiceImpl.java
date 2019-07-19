package sn.cperf.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sn.cperf.dao.FonctionRepository;
import sn.cperf.dao.GroupRepository;
import sn.cperf.dao.NotificationRepository;
import sn.cperf.dao.ProcessRepository;
import sn.cperf.dao.TaskRepository;
import sn.cperf.dao.UserRepository;
import sn.cperf.model.Group;
import sn.cperf.model.Notification;
import sn.cperf.model.Processus;
import sn.cperf.model.Task;
import sn.cperf.model.User;
import sn.cperf.util.ProcessTasks;
import sn.cperf.util.ProcessUtil;
import sn.cperf.util.TaskUtil;
import sn.cperf.util.UserTaskPerfomance;

@Service
@Transactional
public class UserServiceImpl implements UserService{
	@Autowired UserRepository userRepository;
	@Autowired GroupRepository groupRepository;
	@Autowired ProcessRepository processRepository;
	@Autowired TaskRepository taskRepository;
	@Autowired FonctionRepository fonctionRepository;
	@Autowired NotificationRepository notificationRepository;
	@Autowired CperfService cperfService;
	@Override
	public boolean deleteUser(Long id) {
		try {
			User user = userRepository.getOne(id);
			deleteUser(user);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean deleteUser(User user) {
		if(user != null) {
			try {
				// detaching user on group
				if(user.getGroupes() != null) {
					List<Group> userGroup = user.getGroupes();
					for(Group group : userGroup) {
						group.setUsers(removeUserOnList(group.getUsers(), user));
						groupRepository.save(group);
					}
				}
				// dettaching user on process editor
				List<Processus> userEditorProcesses = processRepository.findByEditorIs(user);
				if(userEditorProcesses != null && !userEditorProcesses.isEmpty()) {
					for(Processus process : userEditorProcesses) {
						process.setEditor(null);
						processRepository.save(process);
					}
				}
				//dettaching user on tasks
				List<Task> userTasks = taskRepository.getUserTasks(user.getId());
				if(userTasks != null && !userTasks.isEmpty()) {
					for(Task task : userTasks) {
						task.setUsers(removeUserOnList(task.getAllUsers(), user));
						if(task.getValidator() != null && task.getValidator().getId() == user.getId())
							task.setValidator(null);
						taskRepository.save(task);
						System.err.println("détachement d'une tâche");
					}
				}
				//dettaching user notifications
				List<Notification> notifications = notificationRepository.findBySenderIsOrTargetIs(user, user);
				if(notifications != null && ! notifications.isEmpty()) {
					for(Notification note : notifications) {
						if(note.getSender() != null && note.getSender().getId() == user.getId())
							note.setSender(null);
						if(note.getTarget() != null && note.getTarget().getId() == user.getId())
							note.setTarget(null);
						notificationRepository.save(note);
					}
				}
				// dettash on user chirlds
        		List<User> chirlds = userRepository.findByUserSup(user);
        		System.err.println(chirlds.size());
				if(chirlds != null && !chirlds.isEmpty()) {
					for(User chirld : chirlds) {
						chirld.setUserSup(null);
						userRepository.save(chirld);
						System.err.println("mise à jour");
					}
				}
				// dettaching roles
				user.setRoles(null);
				userRepository.save(user);
				// deletting user
				userRepository.delete(user);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	private List<User> removeUserOnList(List<User> users, User user){
		List<User> newList = new ArrayList<>();
		if(users != null && !users.isEmpty() && user != null) {
			for(User u : users) {
				if(u.getId() != user.getId())
					newList.add(u);
			}
		}
		return newList;
	}

	@Override
	public User getLoged() {
		return cperfService.getLoged();
	}

	@Override
	public List<ProcessTasks> getUserProcessTasks(Long userId) {
		try {
			User user = userRepository.getOne(userId);
			return getUserProcessTasks(user);
		} catch (Exception e) {
			System.err.println("in UserserviceImpl on getUserProcessTasks methode");
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
	
	@Override
	public List<ProcessTasks> getUserProcessTasks(User user) {
		List<ProcessTasks> usersProcessTasks = new ArrayList<>();
		if(user !=null) {
			List<Task> usersTasks = null;
			try {
				if(user.getGroupes() != null && !user.getGroupes().isEmpty())
				usersTasks = taskRepository.findByGroupIn(user.getGroupes());
			} catch (Exception e) {}
			if(usersTasks == null)
				usersTasks = new ArrayList<>();
			if(! usersTasks.isEmpty() && user.getTasks() != null && !user.getTasks().isEmpty()) {
				for(Task task : user.getTasks()) {
					int taskFounded = 0;
					for(Task utask : usersTasks) {
						if(task.getId() == utask.getId())
							taskFounded++;
					}
					if(taskFounded <=0)
						usersTasks.add(task);
				}
				user.setTasks(usersTasks);
			}
			List<Processus> userProcess = new ArrayList<>();
			// All process gettings
			for(Task t : user.getTasks()) {
				if(t.getSection() != null && t.getSection().getProcess() != null) {
					Processus process = t.getSection().getProcess();
					int processFinded = 0;
					for(Processus p : userProcess) {
						if(p.getId() == process.getId()) {
							processFinded++;
							break;
						}
					}
					if(processFinded <=0)
						userProcess.add(process);
				}
			}
			// tasks grouping for process
			for(Processus p : userProcess) {
				ProcessTasks pt = new ProcessTasks();
				ProcessUtil pu = new ProcessUtil();
				pu.setId(p.getId());
				pu.setLabel(p.getLabel());
				pt.setProcess(pu);
				List<TaskUtil> tasks = new ArrayList<>();
				for(Task t : user.getTasks()) {
					if(t.getSection() != null && t.getSection().getProcess() != null) {
						if(t.getProcessId() == p.getId()) {
							TaskUtil tu = new TaskUtil();
							tu.setId(t.getId());
							tu.setName(t.getName());
							tasks.add(tu);
						}
					}
				}
				pt.setTasks(tasks);
				// adding processTasks
				usersProcessTasks.add(pt);
			}
		}
		return usersProcessTasks;
	}

	@Override
	public List<Task> getUserTaskByProcess(User user,Long processId) {
		List<Task> tasks = new ArrayList<>();
		if(user != null && processId != null) {
			try {
				tasks = taskRepository.getByProcess(processId);
				if(!tasks.isEmpty()) {
					List<Long> taskIds = tasks.stream().map(Task::getId).collect(Collectors.toList());
					if(user.getGroupes() != null && !user.getGroupes().isEmpty()) {
						List<Long> groupIds = user.getGroupes().stream().map(Group::getId).collect(Collectors.toList());
						tasks = taskRepository.getTasksByUserIdOrUserGroupIdsWhereTaskIdIn(user.getId(), taskIds, groupIds);
					}else {
						tasks = taskRepository.getTasksByUserIdWhereTaskIdIn(user.getId(), taskIds);
					}
					tasks.forEach(t->System.err.println("task name " + t.getName() + " processid "+t.getProcessId() + " compare " +(t.getProcessId() == processId)));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return tasks;
	}

	@Override
	public List<UserTaskPerfomance> getUserTasksPerfomance(Date date) {
		List<UserTaskPerfomance> userTasksPerformances = new ArrayList<>();
		List<User> users = userRepository.findAll();
		if(users != null) {
			for(User user : users) {
				List<Task> userTasks = taskRepository.getUserTasks(user.getId());
				List<Task> filteredTasks = new ArrayList<>();
				if (userTasks != null && !userTasks.isEmpty()) {
					for(Task task : userTasks) {
						if(date == null) {
							filteredTasks.add(task);
						}else {
							if(task.getSection() != null && task.getSection().getProcess() != null && 
									task.getSection().getProcess().getStartAt() != null 
									&& date.compareTo(task.getSection().getProcess().getStartAt())>=0) {
								filteredTasks.add(task);
							}
						}
					}
					if(filteredTasks != null && !filteredTasks.isEmpty()) {
						UserTaskPerfomance utp = new UserTaskPerfomance();
						utp.setUser(user);
						utp.setTasks(filteredTasks);
						userTasksPerformances.add(utp);
					}
				}
			}
		}
		return userTasksPerformances;
	}

}
