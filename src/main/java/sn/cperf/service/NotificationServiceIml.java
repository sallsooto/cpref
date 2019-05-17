package sn.cperf.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sn.cperf.dao.NotificationRepository;
import sn.cperf.model.Group;
import sn.cperf.model.Notification;
import sn.cperf.model.User;

@Service
public class NotificationServiceIml implements NotificationService{
	
	@Autowired NotificationRepository notificationRepository;
	@Autowired CperfService cperfService;
	@Override
	public void notify(String title,String note,String type,String link,List<User> users, Group groupe) {
		try {
			users = users == null ? new ArrayList<User>() : users;
			if(groupe != null && groupe.getUsers() != null) {
				for(User user : groupe.getUsers()) {
					if(!users.contains(user))
						users.add(user);
				}
			}
			// saving notification
			for(User target : users) {
				try {
					notify(title, note, type, target,link);
				} catch (Exception e) {
					System.err.println("One notification unsaved!");
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void notify(String title, String note, String type, User target,String link) throws Exception {
		try {
			Notification notify = new Notification();
			notify.setTitle(title);
			notify.setNote(note);
			notify.setType(type);
			notify.setTarget(target);
			notify.setSender(cperfService.getLoged());
			notify.setStoreAt(new Date());
			notify.setLink(link);
			notificationRepository.save(notify);
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void changeAllUserTargetNotificationsSeenStatus(boolean seen) {
		try {
			List<Notification> notifs = notificationRepository.findByTargetAndTypeNotAndSeenOrderById(cperfService.getLoged(), "message", !seen);
			for(Notification note : notifs) {
				note.setSeen(seen);
				try {
					notificationRepository.save(note);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void changeAllUserTargetMessagesSeenStatus(boolean seen) {
		try {
			List<Notification> notifs = notificationRepository.findByTargetAndTypeAndSeenOrderById(cperfService.getLoged(), "message", !seen);
			for(Notification note : notifs) {
				note.setSeen(seen);
				try {
					notificationRepository.save(note);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
