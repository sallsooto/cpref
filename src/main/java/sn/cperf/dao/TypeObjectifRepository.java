package sn.cperf.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sn.cperf.model.TypeObjectif;

public interface TypeObjectifRepository extends JpaRepository<TypeObjectif, Long>{
	TypeObjectif findBySlug(String slug);
	List<TypeObjectif> findByValid(boolean valid);
	TypeObjectif findFirstByOrderByIdDesc();
}
