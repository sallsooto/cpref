package sn.cperf.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import sn.cperf.model.DBFile;

public interface DBFileRepository extends JpaRepository<DBFile, Long>{
	DBFile findTop1ByDefaultUserAvatarIsTrueOrderByIdDesc();
}
