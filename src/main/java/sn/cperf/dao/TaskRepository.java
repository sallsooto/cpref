package sn.cperf.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sn.cperf.model.ProcessSection;
import sn.cperf.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
	List<Task> findByIdIsNot(Long id);
	List<Task> findBySection(ProcessSection section);
	List<Task> findBySectionAndIdIsNot(ProcessSection section, Long id);
	@Query("select t from Task t where t.section.process.id=:x")
	List<Task> getByProcess(@Param("x") Long processId);
	@Query("select t from Task t where t.id !=:id and t.section.process.id=:x")
	List<Task> getByProcessAndTaskIdIsNot(@Param("id") Long taskId, @Param("x") Long processId);
}
