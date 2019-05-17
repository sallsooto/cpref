package sn.cperf.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sn.cperf.model.Impact;
public interface ImpactRepository extends JpaRepository<Impact, Long>{
	Impact findBySlug(String type);
}
