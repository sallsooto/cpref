package sn.cperf.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sn.cperf.model.Fonction;
import sn.cperf.model.Group;
import sn.cperf.model.ProcessSection;
import sn.cperf.model.Task;
import sn.cperf.model.User;

public interface TaskRepository extends JpaRepository<Task, Long> {
	List<Task> findByIdIsNot(Long id);

	List<Task> findBySection(ProcessSection section);

	List<Task> findBySectionAndIdIsNot(ProcessSection section, Long id);

	@Query("select t from Task t where t.section.process.id=:x order by t.id DESC")
	List<Task> getByProcess(@Param("x") Long processId);

	@Query("select t from Task t where t.id !=:id and t.section.process.id=:x order by t.id DESC")
	List<Task> getByProcessAndTaskIdIsNot(@Param("id") Long taskId, @Param("x") Long processId);

	List<Task> findByGroupInAndNameLikeIgnoreCase(List<Group> groups, String name);

	@Query(value = "SELECT * from tasks t inner join users_tasks ut on t.id = ut.task_id where "
			+ "(t.group_id in :groupIds OR ut.user_id = :userId) and t.name Like :name and t.status = :status", nativeQuery = true)
	Page<Task> getUserTasks(@Param("groupIds") List<Long> groupIds, @Param("userId") Long userId,
			@Param("name") String name, @Param("status") String status, Pageable page);

	@Query(value = "SELECT * from tasks t inner join users_tasks ut on t.id = ut.task_id where "
			+ "ut.user_id = :userId and t.name Like :name and t.status = :status", nativeQuery = true)
	Page<Task> getUserTasks(@Param("userId") Long userId, @Param("name") String name, @Param("status") String status,
			Pageable page);

	List<Task> findByStatusIgnoreCaseOrderByIdDesc(String status);

	@Query("select t from Task t where UPPER(t.status) =UPPER(:status) and (UPPER(t.name) LIKE UPPER(:name) OR UPPER(t.description) LIKE UPPER(:description))")
	List<Task> searchTasksStatusANdNameOrDescrion(@Param("status") String status, @Param("name") String name,
			@Param("description") String decription);

	@Query("select t from Task t where UPPER(t.status) !=UPPER(:status) and (UPPER(t.name) LIKE UPPER(:name) OR UPPER(t.description) LIKE UPPER(:description))")
	List<Task> searchTasksStatusNotANdNameOrDescrion(@Param("status") String status, @Param("name") String name,
			@Param("description") String decription);

	@Query(value = "SELECT t.* FROM tasks as t" + " INNER JOIN process_sections as ps ON t.section_id=ps.id "
			+ "INNER JOIN process as p ON ps.process_id=p.id WHERE p.id=:pid AND UPPER(t.status) != UPPER('completed')", nativeQuery = true)
	List<Task> getUncompletedTaskTasksByProcess(@Param("pid") Long processId);

	@Query(value = "SELECT DISTINCT t.* FROM process ps INNER JOIN process_sections as pss ON ps.id=pss.process_id "
			+ "INNER JOIN tasks as t ON pss.id=t.section_id INNER JOIN users_tasks as ut ON t.id=ut.task_id "
			+ "INNER JOIN users as u ON ut.user_id=u.id INNER JOIN fonctions as f ON u.fonction_id=f.id WHERE f.id=:fonctionId", nativeQuery = true)
	List<Task> getByActor(@Param("fonctionId") Long fonctionId);
	List<Task> findByParent(Task parent);
	
}
