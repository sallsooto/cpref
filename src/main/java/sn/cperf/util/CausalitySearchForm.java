package sn.cperf.util;

import java.util.Calendar;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor @AllArgsConstructor
@Setter
public class CausalitySearchForm {
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date startDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date endDate;
	private String status;
	private int page;
	private int size;
	
	public Date getStartDate() {
		if(startDate == null || startDate.compareTo(getEndDate()) == 0) {
			startDate = getEndDate();
			try {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(startDate);
				calendar.add(Calendar.DATE, -10);
				startDate = calendar.getTime();
			} catch (Exception e) {
				
			}
		}
		else if(startDate.compareTo(getEndDate()) > 0) {
			Date tmpDate = startDate;
			startDate = getEndDate();
			endDate = tmpDate;
			tmpDate = null;
		}
		return startDate;
	}
	public Date getEndDate() {
		return endDate == null ? new Date() : endDate;
	}
	public String getStatus() {
		return (status == null || status.equals("")) ? "finished_expired" : status;
	}
	public int getPage() {
		return page <=0 ? 0 : page;
	}
	public int getSize() {
		return size <= 0 ? 7 : size;
	}
}
