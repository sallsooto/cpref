package sn.cperf.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class PerformanceManagerForm {
	private Long id;
	private Long objectifId;
	private Long fonctionId;
	private String name;
	private String description;
	private Long parentId;
	private boolean objectif = false;
}
