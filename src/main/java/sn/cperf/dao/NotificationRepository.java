package sn.cperf.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sn.cperf.model.Notification;
import sn.cperf.model.User;

public interface NotificationRepository extends JpaRepository<Notification, Long>{
	public List<Notification> findByTargetOrderByIdDesc(User target);
	public List<Notification> findBySenderOrderByIdDesc(User sender);
	public List<Notification> findByTargetOrSenderOrderByIdDesc(User target,User sender);
}
