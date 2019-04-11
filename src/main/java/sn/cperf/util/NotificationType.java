package sn.cperf.util;

public enum NotificationType {
	ALERT("alert"),
	INFO("info"),
	DANGER("danger"),
	SUCCESS("success");
	
	private String type="";
	NotificationType(String type){
		this.type = type;
	}
	
	@Override
	public String toString() {
		return this.type;
	}
}
