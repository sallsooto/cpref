package sn.cperf.util;

public enum NotificationType {
	ALERT("alert"),
	INFO("info"),
	DANGER("danger"),
	SUCCESS("success"),
	MESSAGE("message");
	
	private String type="";
	NotificationType(String type){
		this.type = type;
	}
	
	@Override
	public String toString() {
		return this.type;
	}
}
