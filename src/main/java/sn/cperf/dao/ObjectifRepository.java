package sn.cperf.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sn.cperf.model.Fonction;
import sn.cperf.model.Group;
import sn.cperf.model.Objectif;
import sn.cperf.model.TypeObjectif;

public interface ObjectifRepository extends JpaRepository<Objectif, Long>{
	List<Objectif> findByFonctionOrderByIdDesc(Fonction fonction);
	Page<Objectif> findByFonctionOrGroupIn(Fonction fonction, List<Group> groupes, Pageable page);
	@Query("select o from Objectif o where (o.fonction =:fonction or o.group in :groupes) and o.type =:type")
	Page<Objectif> getByFonctionOrGroupInAndType(@Param("fonction") Fonction fonction, @Param("groupes") List<Group> groupes, @Param("type") TypeObjectif type, Pageable page);
}
