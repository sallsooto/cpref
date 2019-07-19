package sn.cperf.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sn.cperf.model.Processus;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class ProcessesDossierGrouper {
	private int index;
	private String label;
	private List<Processus> processes;
	public double getDynamicPerformance() {
		double performance = 0;
		if(processes != null && !processes.isEmpty()) {
			double realPerformance = 0;
			for(Processus p : processes)
				realPerformance = realPerformance + p.getDynamicPerformance();
			if(realPerformance >0) {
				performance = (realPerformance * 100)/ (processes.size()*100);
				// formattage du nombre 
				BigDecimal bdec, bdec2;
				bdec = new BigDecimal(Double.toString(performance));
				bdec2 = bdec.setScale(2,RoundingMode.FLOOR);
				performance = bdec2.doubleValue();
			}
		}
		return performance;
	}
}
