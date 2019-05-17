package sn.cperf.util;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class IndicatorDataCollectorUtil {
	private Long id;
	private String dataText;
	private Double dataNumber;
	@DateTimeFormat(pattern="dd/MM/yyyy")
	private Date startDate;
	@DateTimeFormat(pattern="dd/MM/yyyy")
	private Date maxDate;
}
