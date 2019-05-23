package sn.cperf.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class JustificationForm {
	private Long id;
	private Long processId;
	private Long taskId;
	private String impact;
	private String cause;
	private String content;
}
