package sn.cperf.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import sn.cperf.dao.GroupRepository;
import sn.cperf.dao.TaskRepository;
import sn.cperf.model.Group;
import sn.cperf.model.Task;
import sn.cperf.util.GroupProcesses;

public class GroupServiceImpl implements GroupService{
    @Autowired GroupRepository groupRepository;
    @Autowired TaskRepository taskRepository;
	@Override
	public List<GroupProcesses> getGroupProcesses() {
		List<GroupProcesses> groupProcesses = new ArrayList<>();
		List<Group> groups = groupRepository.findAll();
		if(groups != null) {
			for(Group group : groups) {
				List<Task> groupTasks = taskRepository.findByGroup(group);
				for(Task task : groupTasks) {
					
				}
			}
		}
		return null;
	}

}
