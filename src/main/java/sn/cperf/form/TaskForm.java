package sn.cperf.form;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sn.cperf.model.Group;
import sn.cperf.model.ProcessSection;
import sn.cperf.model.Task;
import sn.cperf.model.User;
@NoArgsConstructor
@AllArgsConstructor 
@Getter @Setter
public class TaskForm {
	private Long id;
	private String name;
	private String type;
	private String status;
	private String description;
	private MultipartFile descriptionFileData;
	private String fileDataType;
	private Task parent;
	private List<Task> chirlds;
	private ProcessSection section;
	private Group group;
	private List<User> users;
}
