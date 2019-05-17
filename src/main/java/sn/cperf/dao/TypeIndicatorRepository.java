package sn.cperf.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sn.cperf.model.TypeIndicator;

public interface TypeIndicatorRepository extends JpaRepository<TypeIndicator, Long> {
	TypeIndicator findByTypeIgnoreCase(String type);
	List<TypeIndicator> findByValid(boolean valid);
	TypeIndicator findFirstByOrderByIdDesc();
}
