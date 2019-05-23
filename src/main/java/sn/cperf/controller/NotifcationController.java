package sn.cperf.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import sn.cperf.dao.NotificationRepository;
import sn.cperf.model.Notification;
import sn.cperf.model.User;
import sn.cperf.service.CperfService;
import sn.cperf.service.NotificationService;

@Controller
@RequestMapping("/Notification")
public class NotifcationController {

	@Autowired
	NotificationRepository notificationRepository;
	@Autowired
	CperfService cperfService;
	@Autowired NotificationService notificationService;
	
	@GetMapping("/getUserSeenNotificationsJson")
	@ResponseBody
	public List<Notification> getUserSeenNotificationsJson() {
		List<Notification> notifs = new ArrayList<>();
		try {
			notifs = notificationRepository.findTop5ByTargetAndTypeNotAndSeenOrderByIdDesc(cperfService.getLoged(),"message",true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return notifs;
	}
	
	@GetMapping("/getUserNoSeenNotificationsJson")
	@ResponseBody
	public List<Notification> getUserNoSeenNotificationsJson() {
		List<Notification> notifs = new ArrayList<>();
		try {
			notifs = notificationRepository.findTop5ByTargetAndTypeNotAndSeenOrderByIdDesc(cperfService.getLoged(),"message",false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return notifs;
	}
	@GetMapping("/getUserSeenMessagesJson")
	@ResponseBody
	public List<Notification> getUserSeenMessagesJson() {
		List<Notification> notifs = new ArrayList<>();
		try {
			notifs = notificationRepository.findTop5ByTargetAndTypeAndSeenOrderByIdDesc(cperfService.getLoged(),"message",true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return notifs;
	}
	
	@GetMapping("/getUserNoSeenMessagesJson")
	@ResponseBody
	public List<Notification> getUserNoSeenMessagesJson() {
		List<Notification> notifs = new ArrayList<>();
		try {
			notifs = notificationRepository.findTop5ByTargetAndTypeAndSeenOrderByIdDesc(cperfService.getLoged(),"message",false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return notifs;
	}
	
	@GetMapping("/setSeenNotesJson")
	@ResponseBody
	public void setSeenNotesJson() {
		try {
			deleteExpiredNotifications();
			notificationService.changeAllUserTargetNotificationsSeenStatus(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@GetMapping("/setSeenMessagesJson")
	@ResponseBody
	public void setSeenMessagesJson() {
		try {
			notificationService.changeAllUserTargetMessagesSeenStatus(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@GetMapping("/")
	public String getNotificationsView(@RequestParam(name="type", defaultValue="info") String type) {
		return "notifications";
	}
	
	// notifications with pagination
	@GetMapping("/getUserSeenNotificationsOrMessagePageJson")
	@ResponseBody
	public Page<Notification> getUserNotificationsOrMessagesPageJson(@RequestParam(name="page", defaultValue="0") int page, 
			@RequestParam(name="size", defaultValue="7") int size, @RequestParam(name="type", defaultValue="info") String type,
			@RequestParam(name="seen", defaultValue="false") boolean seen) {
		try {
			return  notificationRepository.findByTargetAndTypeAndSeenOrderById(cperfService.getLoged(),type,seen, new PageRequest(page, size));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private void deleteExpiredNotifications() {
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.set(GregorianCalendar.DAY_OF_MONTH, calendar.get(GregorianCalendar.DAY_OF_MONTH)-1);
			List<Notification> expiredNotifs = notificationRepository.findByStoreAtLessThanEqualAndSeenTrue(calendar.getTime());
			System.out.println(" Nb notif " +expiredNotifs.size());
			for(Notification expiredNotif :  expiredNotifs) {
				try {
					notificationRepository.delete(expiredNotif);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
