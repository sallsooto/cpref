package sn.cperf.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import sn.cperf.model.Fonction;

public interface FonctionRepository extends JpaRepository<Fonction, Long>{
	Fonction findByNameIgnoreCase(String name);
}
