package sn.cperf.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sn.cperf.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long>{
	Role findByRole(String role);
	List<Role> findByOrderByIdDesc();
}
