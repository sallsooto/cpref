package sn.cperf.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import sn.cperf.model.Parametre;

public interface ParamRepository extends JpaRepository<Parametre, Long>{
	Parametre findBySlug(String slug);
}
