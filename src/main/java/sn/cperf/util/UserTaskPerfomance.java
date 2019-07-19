package sn.cperf.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sn.cperf.model.Task;
import sn.cperf.model.User;
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class UserTaskPerfomance {
	private User user;
	private List<Task> tasks;
	public double getDynamicPerformance() {
		double performance = 0;
		if(tasks != null && !tasks.isEmpty()) {
			double realPerformance = 0;
			for(Task task : tasks) {
				realPerformance = realPerformance + task.getDynamicPerformance();
			}
			if(realPerformance >0) {
				performance = (realPerformance * 100)/ (tasks.size()*100);
				// formattage du nombre 
				BigDecimal bdec, bdec2 = null;
				bdec = new BigDecimal(Double.toString(performance));
				bdec2 = bdec.setScale(2,RoundingMode.FLOOR);
				performance = bdec2.doubleValue();
			}
		}
		System.err.println(" task performance" +performance);
		return performance;
	}
}
