package sn.cperf.service;

import java.util.Date;
import java.util.List;

import sn.cperf.model.Processus;
import sn.cperf.model.Task;
import sn.cperf.util.ProcessesDossierGrouper;

public interface ProcessService {
	 void finishProcessWhenIsTime(Processus process);
	 void startChirldTaskStatusWhenIsNecessary(Task task);
	 List<Processus> getProcessForTasks(List<Task> tasks);
	 void reproduceModel(Processus p);
	 List<ProcessesDossierGrouper> getProcessDossierGrouppers(Date date);
}
