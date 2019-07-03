package sn.cperf.service;

import java.util.ArrayList;
import java.util.List;

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

@Service
@Transactional
public class UserServiceImpl implements UserService{
	@Autowired UserRepository userRepository;
	@Autowired GroupRepository groupRepository;
	@Autowired ProcessRepository processRepository;
	@Autowired TaskRepository taskRepository;
	@Autowired FonctionRepository fonctionRepository;
	@Autowired NotificationRepository notificationRepository;
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

}
