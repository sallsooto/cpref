package sn.cperf.service;

import sn.cperf.model.Task;

public interface TaskService {
  public Task normalizechirldTasksConditions(Task task);
  public boolean checkIfThisIsTheFirstTaskForProcess(Task task);
  public void notifyChilrdIfThisParentTaskIsLunched(Task parent);
}
