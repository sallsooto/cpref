package sn.cperf.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sn.cperf.dao.ProcessRepository;
import sn.cperf.dao.TaskRepository;
import sn.cperf.model.Processus;
import sn.cperf.model.Task;
import sn.cperf.util.TaskStatus;

@Service
public class ProcessServiceImpl implements ProcessService{
    @Autowired ProcessRepository processRepository;
    @Autowired TaskRepository taskRepository;
	@Override
	public void finishProcessWhenIsTime(Processus process) {
		try {
			boolean taskNoCompletedFouned = false;
			if(process != null && process.getStartAt() != null) {
				List<Task> processTasks = process.getTasks();
				if(processTasks != null && !processTasks.isEmpty()) {
					for(Task task : processTasks) {
						if(!task.getStatus().toLowerCase().equals(TaskStatus.COMPLETED.toString().toLowerCase())
							&& !task.getStatus().toLowerCase().equals(TaskStatus.CANCELED.toString().toLowerCase())){
							taskNoCompletedFouned = true;
							break;
						}
					}
				}
				if(!taskNoCompletedFouned && process.getFinishDate() == null) {
					process.setFinishDate(new Date());
					processRepository.save(process);
				}
			}
		} catch (Exception e) {
		}
	}
	@Override
	public void startChirldTaskStatusWhenIsNecessary(Task task) {
	  if(task != null && (task.getStatus().toLowerCase().equals(TaskStatus.COMPLETED.toString().toLowerCase())
			  || task.getStatus().toLowerCase().equals(TaskStatus.CANCELED.toString().toLowerCase()))) {
		  try {
			List<Task> chirldsTasks = taskRepository.findByParent(task);
			  for(Task chirld : chirldsTasks) {
				  if(chirld.getStatus().toLowerCase().equals(TaskStatus.VALID.toString().toLowerCase())) {
					  if(chirld.getStartAt() == null)
						  chirld.setStartAt(new Date());
					  chirld.setStatus(TaskStatus.STARTED.toString().toLowerCase()); 
					  try {taskRepository.save(chirld);} catch (Exception e) {
					}
				  }
			  }
		} catch (Exception e) {
		}
	  }
	}
	@Override
	public List<Processus> getProcessForTasks(List<Task> tasks) {
		List<Processus> processes = new ArrayList<>();
		if(tasks != null && !tasks.isEmpty()) {
			for(Task task : tasks) {
				if(task.getSection() != null && task.getSection().getProcess() != null) {
					int findedProcess = 0;
					for(Processus p : processes) {
						if(p.getId() == task.getProcessId()){
							findedProcess++;
							break;
						}
					}
					if(findedProcess<=0)
						processes.add(task.getSection().getProcess());
				}
			}
		}
		return processes;
	}
}
