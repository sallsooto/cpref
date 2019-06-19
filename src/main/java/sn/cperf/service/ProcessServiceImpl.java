package sn.cperf.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sn.cperf.dao.ProcessRepository;
import sn.cperf.model.Processus;
import sn.cperf.model.Task;
import sn.cperf.util.TaskStatus;

@Service
public class ProcessServiceImpl implements ProcessService{
    @Autowired ProcessRepository processRepository;
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

}
