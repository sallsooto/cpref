package sn.cperf.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sn.cperf.dao.TaskRepository;
import sn.cperf.model.Task;

@Service
public class TaskServiceImpl implements TaskService {
	@Autowired
	TaskRepository taskRepository;

	@Override
	public Task normalizechirldTasksConditions(Task task) {
		if (task != null && task.getParent() != null) {
			if (!checkIfThisIsTheFirstTaskForProcess(task)) {
				try {
					Task taskParent = task.getParent();
					Task firstOtherChilrd = taskParent.getChirlds().stream().filter(tch -> tch.getId() != task.getId())
							.findFirst().orElse(null);
					if (firstOtherChilrd != null) {
						if (!task.isStartConditional())
							task.setStartConditional(true);
						if (!firstOtherChilrd.isStartConditional())
							firstOtherChilrd.setStartConditional(true);
						// changing old yesCondition value
						firstOtherChilrd.setYesCondition(!task.isYesCondition());
						// text condition unification
						String textCondition = (task.getTextCondition() != null) ? task.getTextCondition()
								: (firstOtherChilrd.getTextCondition() != null) ? firstOtherChilrd.getTextCondition()
										: "";

						firstOtherChilrd.setTextCondition(textCondition);
						task.setTextCondition(textCondition);
						taskRepository.save(firstOtherChilrd);
					}
				} catch (Exception e) {
				}
			} else {
				task.setParent(null);
			}
		}
		return task;
	}

	// check if it the first task for the process
	@Override
	public boolean checkIfThisIsTheFirstTaskForProcess(Task task) {
		try {
			List<Task> processTasks = taskRepository.getByProcess(task.getProcessId());
			if (processTasks != null && !processTasks.isEmpty()) {
				Task firstProcessTask = processTasks.stream().sorted(Comparator.comparingLong(Task::getId)).findAny()
						.orElse(null);
				if (firstProcessTask != null && firstProcessTask.getId() == task.getId())
					return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

}
