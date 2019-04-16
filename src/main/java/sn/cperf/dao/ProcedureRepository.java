package sn.cperf.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import sn.cperf.model.Procedure;

public interface ProcedureRepository extends JpaRepository<Procedure, Long>{
	Page<Procedure> findByOrderByIdDesc(Pageable page);
	Page<Procedure> findByNameLikeIgnoreCaseOrderByIdDesc(String name,Pageable page);
}
