package sn.cperf.service;

import java.util.Date;

import sn.cperf.model.Task;

public interface TaskService {
  public Task normalizechirldTasksConditions(Task task);
  public boolean checkIfThisIsTheFirstTaskForProcess(Task task);
  public void notifyChilrdIfThisParentTaskIsLunched(Task parent);
  public Task associateCalanderAndHoildays(Task task);
  public void startStartupTasks(Task task, Date startDate);
}
