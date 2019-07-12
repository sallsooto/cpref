package sn.cperf.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import sn.cperf.model.WorkCalendar;

public interface WorkCalendarRepository extends JpaRepository<WorkCalendar, Long> {
	WorkCalendar findByDayIndex(Integer dayIndex);
}
