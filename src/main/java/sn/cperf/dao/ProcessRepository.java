package sn.cperf.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sn.cperf.model.Processus;
import sn.cperf.model.User;

public interface ProcessRepository extends JpaRepository<Processus, Long> {
//	List<Processus> findByYesProcessNullAndNoProcessNullOrderByIdDesc();
//	List<Processus> findByYesProcessNullAndNoProcessNullAndIdIsNotOrderByIdDesc(Long id);
//	List<Process> findByOrderByParentDesc();
	List<Process> findByIdIsNotOrderByIdDesc(Long id);
	Page<Processus> findByOrderByIdDesc(Pageable page);
	Page<Processus> findByLabelLikeIgnoreCaseOrderByIdDesc(String label, Pageable page);
	Page<Processus> findByStartAtBetween(Date startDate, Date endDate, Pageable page);
	Page<Processus> findByStartAtIsNull(Pageable page);
	@Query("select p from Processus p where p.startAt != null and p.finishDate != null and valid=true "
			+ "and p.startAt BETWEEN :startDate AND :endDate and p.finishDate > p.totalTime")
	Page<Processus> getFinishedAndExpiredProcess(@Param("startDate") Date startDate,@Param("endDate") Date endDate, Pageable page);
	@Query("select p from Processus p where p.startAt != null and p.finishDate = null "
			+ "and p.startAt BETWEEN :startDate AND :endDate and valid=true and p.totalTime <:limitDate")
	Page<Processus> getNofinishedAndExpiredProcess(@Param("startDate") Date startDate,@Param("endDate") Date endDate, @Param("limitDate") Date limitDate,Pageable page);
	@Query("select p from Processus p where p.startAt != null and p.finishDate = null and valid=true "
			+ "and p.startAt BETWEEN :startDate AND :endDate and p.totalTime>=:limitDate")
	Page<Processus> getNofinishedAndNoExpiredProcess(@Param("startDate") Date startDate,@Param("endDate") Date endDate, @Param("limitDate") Date limitDate,Pageable page);
	Page<Processus> findByValidFalse(Pageable page);
	List<Processus> findByEditorIs(User editor);
	List<Processus> findByModelIdIsNullOrderByIdDesc();
	List<Processus> findByModelId(Long modelId);
	@Query("select distinct p.label from Processus p group by p.label")
	List<String> getProcessGroupedByLabel();
	List<Processus> findByLabel(String label);
	List<Processus> findByLabelAndStoreAtAfter(String label, Date beforStoreAt);
	List<Processus> findByStoreAtAfter(Date beforStoreAt);
	
}
