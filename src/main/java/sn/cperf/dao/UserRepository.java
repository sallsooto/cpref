package sn.cperf.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sn.cperf.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByUsername(String username);
	User findByEmail(String email);
	List<User> findByUserSupIsNullAndOrganigrammeTrue();
	List<User> findByUserSupIsNotNullAndOrganigrammeTrue();
	List<User> findByOrderByIdDesc();
	Optional<User> findFirst1ByUserSupIsNullAndIdNotOrderById(Long id);
	@Query("select u from User u where (u.firstname LIKE :x or u.lastname LIKE :x) and u.valid=true and u.organigramme=false")
	Page<User> searchOthersForOrganigramme(@Param("x") String searchkey, Pageable page);
	@Query("select u from User u where u.valid=true and u.organigramme=false")
	Page<User> searchOthersForOrganigramme(Pageable page);
	List<User> findByValid(boolean valid);
	Page<User> findByValid(boolean valid, Pageable page);
	Page<User> findByFirstnameLikeOrLastnameLikeIgnoreCase(String firstname,String lastname, Pageable page);
	List<User> findByFirstnameLikeIgnoreCaseOrLastnameLikeIgnoreCase(String firstname,String lastname);
	List<User> findByUserSup(User userSup);
}
