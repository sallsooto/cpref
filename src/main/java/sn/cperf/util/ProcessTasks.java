package sn.cperf.util;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class ProcessTasks {
	private ProcessUtil process;
	private List<TaskUtil> tasks;
}
