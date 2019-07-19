package sn.cperf.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sn.cperf.dao.IndicateurRepository;
import sn.cperf.dao.ObjectifRepository;
import sn.cperf.dao.ProcessRepository;
import sn.cperf.dao.ProcessSectionRepository;
import sn.cperf.dao.TaskRepository;
import sn.cperf.model.DBFile;
import sn.cperf.model.Holiday;
import sn.cperf.model.Indicateur;
import sn.cperf.model.Objectif;
import sn.cperf.model.ProcessSection;
import sn.cperf.model.Processus;
import sn.cperf.model.Task;
import sn.cperf.model.User;
import sn.cperf.model.WorkCalendar;
import sn.cperf.util.ProcessesDossierGrouper;
import sn.cperf.util.TaskStatus;

@Service
public class ProcessServiceImpl implements ProcessService {
	@Autowired
	ProcessRepository processRepository;
	@Autowired
	TaskRepository taskRepository;
	@Autowired
	ProcessSectionRepository sectionRepository;
	@Autowired
	IndicateurRepository indicatorRepository;
	@Autowired
	ObjectifRepository objectifRepository;
	@Autowired
	DBFileService dbFileService;

	@Override
	public void finishProcessWhenIsTime(Processus process) {
		try {
			boolean taskNoCompletedFouned = false;
			if (process != null && process.getStartAt() != null) {
				List<Task> processTasks = process.getTasks();
				if (processTasks != null && !processTasks.isEmpty()) {
					for (Task task : processTasks) {
						if (!task.getStatus().toLowerCase().equals(TaskStatus.COMPLETED.toString().toLowerCase())
								&& !task.getStatus().toLowerCase()
										.equals(TaskStatus.CANCELED.toString().toLowerCase())) {
							taskNoCompletedFouned = true;
							break;
						}
					}
				}
				if (!taskNoCompletedFouned && process.getFinishDate() == null) {
					process.setFinishDate(new Date());
					processRepository.save(process);
				}
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void startChirldTaskStatusWhenIsNecessary(Task task) {
		if (task != null && (task.getStatus().toLowerCase().equals(TaskStatus.COMPLETED.toString().toLowerCase())
				|| task.getStatus().toLowerCase().equals(TaskStatus.CANCELED.toString().toLowerCase()))) {
			try {
				List<Task> chirldsTasks = taskRepository.findByParent(task);
				for (Task chirld : chirldsTasks) {
					if (chirld.getStatus().toLowerCase().equals(TaskStatus.VALID.toString().toLowerCase())) {
						if (chirld.getStartAt() == null)
							chirld.setStartAt(new Date());
						chirld.setStatus(TaskStatus.STARTED.toString().toLowerCase());
						try {
							taskRepository.save(chirld);
						} catch (Exception e) {
						}
					}
				}
			} catch (Exception e) {
			}
		}
	}

	@Override
	public List<Processus> getProcessForTasks(List<Task> tasks) {
		List<Processus> processes = new ArrayList<>();
		if (tasks != null && !tasks.isEmpty()) {
			for (Task task : tasks) {
				if (task.getSection() != null && task.getSection().getProcess() != null) {
					int findedProcess = 0;
					for (Processus p : processes) {
						if (p.getId() == task.getProcessId()) {
							findedProcess++;
							break;
						}
					}
					if (findedProcess <= 0)
						processes.add(task.getSection().getProcess());
				}
			}
		}
		return processes;
	}

	@Override
	public void reproduceModel(Processus p) {
		if (p != null && p.getModelId() != null) {
			try {
				rolBackProcess(p);
				Processus processModel = processRepository.getOne(p.getModelId());
				if (processModel != null) {
					p.setLabel(processModel.getLabel());
					if (p.getDescription() == null)
						p.setDescription(processModel.getDescription());
					List<ProcessSection> modelSections = processModel.getSections();
					for (ProcessSection modelSection : modelSections) {
						ProcessSection section = constructSectionByModel(modelSection, p);
						try {
							if (sectionRepository.save(section) != null)
							saveSectionTasks(section, modelSection.getTasks());
						} catch (Exception e) {
							System.err.println("reproduce process model on section adding");
							e.printStackTrace();
						}
					}
					try {
						processRepository.save(p);
					} catch (Exception e) {
						System.err.println("reproduce process model on process updatting");
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				System.err.println("reproduce process model");
				e.printStackTrace();
			}
		}
	}

	private void saveSectionTasks(ProcessSection section, List<Task> tasks) {
		if (section != null && !tasks.isEmpty()) {
			try {
				List<Task> parentTasks = tasks.stream().filter(t -> t.getParent() == null).collect(Collectors.toList());
				for (Task task : parentTasks) {
					saveTask(section, task, null);
				}
			} catch (Exception e) {
				System.err.println("error processService on saveSectiontaTasks method");
				e.printStackTrace();
			}
		}
	}

	private void saveTask(ProcessSection section, Task task, Task parent) {
		try {
			if (section != null && task != null) {
				List<Task> chirlds = task.getChirlds();
				Task producedTask = constructTaskByModel(task, section);
				producedTask.setParent(parent);
				try {
					producedTask = taskRepository.save(producedTask);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (!chirlds.isEmpty()) {
					for (Task chirld : chirlds) {
						saveTask(section, chirld, producedTask);
					}
				}
			}
		} catch (Exception e) {
			System.err.println("error processService on saveTask method");
			e.printStackTrace();
		}
	}

	private ProcessSection constructSectionByModel(ProcessSection model, Processus p) {
		ProcessSection section = null;
//		try {
//			section = sectionRepository.findTop1ByProcessAndNameIgnoreCase(p, model.getName());
//		} catch (Exception e) {
//		}
		if (section == null)
			section = new ProcessSection();
		section.setGroup(model.getGroup());
		section.setName(model.getName());
		section.setProcess(p);
		return section;
	}

	private Task constructTaskByModel(Task model, ProcessSection section) {
		Task task = null;
		try {
			task = taskRepository.findTop1BySectionAndNameIgnoreCase(section, model.getName());
		} catch (Exception e) {
		}
		if (task == null)
			task = new Task();
		task.setName(model.getName());

		List<WorkCalendar> daysWorkTime = new ArrayList<>();
		for (WorkCalendar wc : model.getDaysWorkTimes())
			daysWorkTime.add(wc);
		task.setDaysWorkTimes(daysWorkTime);
		// task.setDaysWorkTimes(model.getDaysWorkTimes());

		task.setDbFileDescription(model.getDbFileDescription());
		task.setDescription(model.getDescription());
		task.setFileDescriptionPath(model.getFileDescriptionPath());
		task.setGroup(model.getGroup());

		List<Holiday> holidays = new ArrayList<>();
		for (Holiday h : model.getHolidays())
			holidays.add(h);
		task.setHolidays(holidays);
		// task.setHolidays(model.getHolidays());

		task.setLastStatus(model.getLastStatus());
		task.setLunchingByProcess(model.isLunchingByProcess());
		task.setStatus(TaskStatus.VALID.toString());
		task.setLastStatus(TaskStatus.VALID.toString());
		task.setNbDays(model.getNbDays());
		task.setNbHours(model.getNbHours());
		task.setNbMinuites(model.getNbMinuites());
		task.setNbMonths(model.getNbMonths());
		task.setNbYears(model.getNbYears());

		List<Objectif> objectifs = new ArrayList<>();
		for (Objectif obj : model.getObjectifs())
			objectifs.add(obj);
		task.setObjectifs(objectifs);
		// task.setObjectifs(model.getObjectifs());

		task.setPriorityLevel(model.getPriorityLevel());
		task.setTextCondition(model.getTextCondition());
		task.setType(model.getType());

		List<User> users = new ArrayList<>();
		for (User user : model.getUsers())
			users.add(user);
		task.setUsers(users);
		// task.setUsers(model.getUsers());

		task.setGroup(model.getGroup());
		task.setYesCondition(model.isYesCondition());
		task.setValidator(model.getValidator());
		task.setStartConditional(model.isStartConditional());
		task.setStatusValid(model.isStatusValid());
		task.setSection(section);
		return task;
	}

	private void rolBackProcess(Processus p) {
		if (p != null && p.getId() != null) {
			try {
				List<Task> tasks = p.getTasks();
				if (tasks != null) {
					for (Task t : tasks) {
						if (t.getObjectifs() != null && !t.getObjectifs().isEmpty()) {
							for (Objectif obj : t.getObjectifs()) {
								if (obj.getIndicators() != null && !obj.getIndicators().isEmpty()) {
									for (Indicateur ind : obj.getIndicators()) {
										indicatorRepository.delete(ind);
									}
								}
								objectifRepository.delete(obj);
							}
						}
						DBFile dbFiledescription = t.getDbFileDescription();
						taskRepository.delete(t);
						if (t.getDbFileDescription() != null)
							dbFileService.delete(dbFiledescription);
					}
					deleteAllProcessSectionsWithoutTask();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void deleteAllProcessSectionsWithoutTask() {
		try {
			List<ProcessSection> emptySections = sectionRepository.findByTasksIsNullOrProcessIsNull();
			System.err.println(" emptys sections size "+emptySections);
			if (!emptySections.isEmpty()) {
				emptySections.forEach(ps -> {
					sectionRepository.delete(ps);
				});
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public List<ProcessesDossierGrouper> getProcessDossierGrouppers(Date date) {
		List<ProcessesDossierGrouper> processDossierGrouppers = new ArrayList<>();
		try {
			List<String> comonLabels = processRepository.getProcessGroupedByLabel();
			int index = 0;
			for(String label : comonLabels) {
				ProcessesDossierGrouper  pdg = new ProcessesDossierGrouper();
				pdg.setIndex(index);
				pdg.setLabel(label); 
				if(date == null)
					pdg.setProcesses(processRepository.findByLabel(label));
				else
					pdg.setProcesses(processRepository.findByLabelAndStoreAtAfter(label, date));
				processDossierGrouppers.add(pdg);
				index++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return processDossierGrouppers;
	}
}
