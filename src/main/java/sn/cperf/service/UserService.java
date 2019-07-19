package sn.cperf.service;

import java.util.Date;
import java.util.List;

import sn.cperf.model.Task;
import sn.cperf.model.User;
import sn.cperf.util.ProcessTasks;
import sn.cperf.util.UserTaskPerfomance;

public interface UserService {
	public boolean deleteUser(Long id);
	public boolean deleteUser(User user);
	public User getLoged();
	public List<ProcessTasks> getUserProcessTasks(Long userId);
	public List<ProcessTasks> getUserProcessTasks(User user);
	public List<Task> getUserTaskByProcess(User user,Long processId);
	public List<UserTaskPerfomance> getUserTasksPerfomance(Date date);
}
