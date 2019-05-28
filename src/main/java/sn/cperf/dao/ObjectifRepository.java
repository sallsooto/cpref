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
	Page<Objectif> findByFonction(Fonction fonction, Pageable page);
	Page<Objectif> findByFonctionAndType(Fonction fonction,TypeObjectif type, Pageable page);
	Page<Objectif> findByTypeAndFonctionOrGroupIn(TypeObjectif type,Fonction fonction, List<Group> groupes, Pageable page);
	@Query("select ob from Objectif ob where ob.type=:type and (ob.fonction =:fonction OR ob.group IN :groups)")
	Page<Objectif> serachByTypeAndFonctionOrGroupIn(@Param("type") TypeObjectif type,@Param("fonction") Fonction fonction,@Param("groups") List<Group> groupes, Pageable page);
	List<Objectif> findByFonctionOrGroupIn(Fonction fonction, List<Group> fonctionsUsersGroups);
}
