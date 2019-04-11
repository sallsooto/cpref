package sn.cperf.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import sn.cperf.model.Group;

public interface GroupRepository extends JpaRepository<Group, Long>{

}
