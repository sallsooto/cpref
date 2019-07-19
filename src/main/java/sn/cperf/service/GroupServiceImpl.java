package sn.cperf.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sn.cperf.dao.GroupRepository;
import sn.cperf.dao.ProcessRepository;
import sn.cperf.dao.TaskRepository;
import sn.cperf.model.Group;
import sn.cperf.model.ProcessSection;
import sn.cperf.model.Processus;
import sn.cperf.model.Task;
import sn.cperf.util.GroupProcesses;

@Service
public class GroupServiceImpl implements GroupService{
    @Autowired GroupRepository groupRepository;
    @Autowired TaskRepository taskRepository;
    @Autowired ProcessRepository processRepository;
	@Override
	public List<GroupProcesses> getGroupProcesses(Date date) {
		List<GroupProcesses> groupProcesses = new ArrayList<>();
		try {
			List<Group> groups = groupRepository.findAll();
			if(groups != null) {
				List<Processus> processes = processRepository.findAll();
				if(processes != null) {
					for(Group group : groups) {
						List<Processus> gProcesses = new ArrayList<>();
						for( Processus p : processes) {
							List<Task> processesGroupTasks = taskRepository.getByProcessIdAndGroupId(p.getId(), group.getId());
							if(processesGroupTasks != null && !processesGroupTasks.isEmpty()) {
								ProcessSection section = new ProcessSection();
								section.setId(null);
								section.setName("global Section");
								section.setTasks(processesGroupTasks);
								List<ProcessSection> sections = new ArrayList<>();
								sections.add(section);
								p.setSections(sections);
								if(date == null) {
									gProcesses.add(p);
								}else {
									if(isProcesStoredAfter(p, date));
										gProcesses.add(p);
								}
							}
						}
						if(!gProcesses.isEmpty()) {
							GroupProcesses gp = new GroupProcesses();
							gp.setGroup(group);
							gp.setProcesses(gProcesses);
							groupProcesses.add(gp);
						}
					}
				}
			}
		} catch (Exception e) {
			System.err.println("In groupServive on getGroupProcesses method");
			e.printStackTrace();
		}
		return groupProcesses;
	}
	
   private boolean isProcesStoredAfter(Processus process,Date date) {
	   if(process != null && process.getStoreAt() != null && date != null) {
		   if(date.compareTo(process.getStoreAt())>=0)
			   return true;
	   }
	   return false;
   }
}
