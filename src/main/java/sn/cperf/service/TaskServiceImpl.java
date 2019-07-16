package sn.cperf.service;

import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sn.cperf.dao.HolidayRepositoty;
import sn.cperf.dao.TaskRepository;
import sn.cperf.dao.WorkCalendarRepository;
import sn.cperf.model.Task;
import sn.cperf.util.NotificationType;
import sn.cperf.util.TaskStatus;

@Service
public class TaskServiceImpl implements TaskService {
	@Autowired
	TaskRepository taskRepository;
	@Autowired
	NotificationService notifyService;
	@Autowired WorkCalendarRepository workCalendarRepository;
	@Autowired HolidayRepositoty holidayRepository;

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

	@Override
	public void notifyChilrdIfThisParentTaskIsLunched(Task parent) {
		if (parent != null) {
			try {
				List<Task> chilrds = taskRepository.findByParent(parent);
				if (chilrds != null && !chilrds.isEmpty()) {
					String taskSatus = parent.getStatus().toLowerCase().trim()
							.equals(TaskStatus.STARTED.toString().toLowerCase().trim()) ? "relanceé"
									: parent.getStatus().toLowerCase().trim().equals(
											TaskStatus.CANCELED.toString().toLowerCase().trim()) ? "annulée"
													: parent.getStatus().toLowerCase().trim().equals(
															TaskStatus.VALID.toString().toLowerCase().trim()) ? "réinitialisée" : null;
					if (taskSatus != null) {
						for (Task chirld : chilrds) {
							try {
								if (chirld.getStatus().toLowerCase().trim()
										.equals(TaskStatus.COMPLETED.toString().toLowerCase().trim())) {
									notifyService.notify("Tâche précédante " + taskSatus,
											"La tâche " + parent.getName() + " qui précède votre tâche "
													+ chirld.getName() + " a été " + taskSatus,
											NotificationType.INFO.toString(), "/Task/", chirld.getAllUsers(), null);
								}
							} catch (Exception e) {
								e.getSuppressed();
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Task associateCalanderAndHoildays(Task task) {
		if(task != null) {
			try {
				task.setDaysWorkTimes(workCalendarRepository.findAll());
				Calendar calendar = Calendar.getInstance();
				if(task.getStartAt() != null)
					calendar.setTime(task.getStartAt());
				calendar.add(Calendar.DATE, -1);
				task.setHolidays(holidayRepository.findByDteAfter(calendar.getTime()));
			} catch (Exception e) {
			}
		}
		return task;
	}
}
