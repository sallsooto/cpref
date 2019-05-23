package sn.cperf.form;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sn.cperf.model.Task;
import sn.cperf.model.User;
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class SectionForm {
	private Long id;
	private String name;
	private Long processId;
	private Long groupId;
	private boolean withGroup = true;
	private List<User> intervenants;
	private List<Task> tasks;
}
