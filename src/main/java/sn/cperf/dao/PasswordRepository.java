package sn.cperf.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import sn.cperf.model.Password;

public interface PasswordRepository extends JpaRepository<Password, Long>{
	Password findByEmail(String email);
	Password findByToken(String token);
}
