package sn.cperf.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sn.cperf.model.Holiday;

public interface HolidayRepositoty extends JpaRepository<Holiday, Long>{
	Holiday findByDte(Date date);
	List<Holiday> findByDteAfter(Date date);
}
