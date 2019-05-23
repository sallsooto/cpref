package sn.cperf.util;

public enum TaskStatus {

    VALID("valid"),
    CANCELED("canceled"),
    COMPLETED("completed");
	private String status="";
	
	TaskStatus(String status){
		this.status = status;
	}
	
	@Override
	public String toString() {
		return this.status;
	}
}
