package sn.cperf.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import sn.cperf.model.Notification;
import sn.cperf.model.User;

public interface NotificationRepository extends JpaRepository<Notification, Long>{
	public List<Notification> findByTargetOrderByIdDesc(User target);
	public List<Notification> findBySenderOrderByIdDesc(User sender);
	public List<Notification> findByTargetOrSenderOrderByIdDesc(User target,User sender);
	public List<Notification> findByTargetOrderBySeen(User target);
	public List<Notification> findByTargetAndTypeNotOrderBySeen(User target,String type);
	public List<Notification> findByTargetAndTypeOrderBySeen(User target,String type);
	public List<Notification> findByTargetAndTypeNotAndSeenOrderById(User target,String type,boolean seen);
	public List<Notification> findByTargetAndTypeAndSeenOrderById(User target,String type,boolean seen);
	public List<Notification> findTop5ByTargetAndTypeNotAndSeenOrderByIdDesc(User target,String type,boolean seen);
	public List<Notification> findTop5ByTargetAndTypeAndSeenOrderByIdDesc(User target,String type,boolean seen);
	public Page<Notification> findByTargetAndTypeNotAndSeenOrderById(User target,String type,boolean seen, Pageable page);
	public Page<Notification> findByTargetAndTypeAndSeenOrderById(User target,String type,boolean seen, Pageable page);
	public List<Notification> findByStoreAtLessThanEqualAndSeenTrue(Date date);
	public List<Notification> findBySenderIsOrTargetIs(User sender, User target);
}
