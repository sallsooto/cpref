package sn.cperf.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class CausalityJustificationForm {
	private Long id;
	private String cause;
	private String content;
	private Long processId;
	private Long taskId;
}
