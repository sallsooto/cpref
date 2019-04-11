package sn.cperf.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sn.cperf.model.ProcessSection;
import sn.cperf.model.Processus;

public interface ProcessSectionRepository extends JpaRepository<ProcessSection, Long> {
	List<ProcessSection> findByProcess(Processus process);
}
