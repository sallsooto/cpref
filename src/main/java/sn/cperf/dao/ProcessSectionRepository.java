package sn.cperf.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import sn.cperf.model.ProcessSection;
import sn.cperf.model.Processus;

public interface ProcessSectionRepository extends JpaRepository<ProcessSection, Long> {
	List<ProcessSection> findByProcess(Processus process);
	List<ProcessSection> findByTasksIsNullOrProcessIsNull();
	ProcessSection findTop1ByProcessAndNameIgnoreCase(Processus process, String name);
}
