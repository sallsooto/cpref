package sn.cperf.service;

import java.util.List;

import sn.cperf.model.Group;
import sn.cperf.model.User;

public interface NotificationService {
	public void notify(String title,String note,String type,User target,String link) throws Exception;
	public void notify(String title,String note,String type,String link,List<User> users, Group groupe);
	public void changeAllUserTargetNotificationsSeenStatus(boolean seen);
	public void changeAllUserTargetMessagesSeenStatus(boolean seen);
}
