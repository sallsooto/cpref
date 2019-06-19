package sn.cperf.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sn.cperf.model.Justification;
import sn.cperf.model.Processus;
import sn.cperf.model.Task;

public interface JustificationRepository extends JpaRepository<Justification, Long> {
	List<Justification> findByProcessAndTaskIsNull(Processus process);
	List<Justification> findByProcess(Processus process);
	List<Justification> findByTask(Task task);
}
