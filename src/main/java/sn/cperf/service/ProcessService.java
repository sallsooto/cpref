package sn.cperf.service;

import sn.cperf.model.Processus;
import sn.cperf.model.Task;

public interface ProcessService {
	 void finishProcessWhenIsTime(Processus process);
	 void startChirldTaskStatusWhenIsNecessary(Task task);
}
