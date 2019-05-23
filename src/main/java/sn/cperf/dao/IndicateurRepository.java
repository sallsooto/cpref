package sn.cperf.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sn.cperf.model.Indicateur;
import sn.cperf.model.Objectif;

public interface IndicateurRepository extends JpaRepository<Indicateur, Long>{
	Indicateur findFirst1ByOrderByIdDesc();
	List<Indicateur> findByObjectifOrderByIdAsc(Objectif objectif);
	@Query(value="update indicateurs set parent_id=:parentId where id=:id", nativeQuery=true)
	Indicateur nativeUpdateIndicatorParentId(@Param("parentId") Long parentId, @Param("id") Long id);
	List<Indicateur> findByParentNotAndIdNotOrderByIdDesc(Indicateur parent, Long id);
}
