package sn.cperf.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sn.cperf.model.Indicateur;
import sn.cperf.model.IndicatorDataCollector;
import sn.cperf.model.Processus;
import sn.cperf.model.Task;
import sn.cperf.util.TaskStatus;

@Service
public class IndicatorServiceImpl implements IndicatorService {
	@Autowired
	CperfService cperfService;

	@Override
	public boolean checkIndicatorDataResultEditable(Indicateur indicator) {

		try {
			boolean withresultEdition = (cperfService.getLoged() != null && !cperfService.getLoged().hasRole("admin") && !indicator.isQuestionResolvableByActor()) ? false : true;
			if (indicator != null) {
				if (withresultEdition) {
					// verification selon la validité du status de la tâche et le process si c'est rattaché à une tâche 
					if (indicator.getObjectif() != null && indicator.getObjectif().getTask() != null) {
						Task task = indicator.getObjectif().getTask();
						if (!task.getStatus().toLowerCase().equals(TaskStatus.CANCELED.toString().toLowerCase())
								&& !task.getStatus().toLowerCase()
										.equals(TaskStatus.COMPLETED.toString().toLowerCase())) {
							if (task.getSection() != null && task.getSection().getProcess() != null) {
								Processus process = task.getSection().getProcess();
								if (process.isValid() && !process.isExpired() && !process.getIsFinished() && process.getStartAt() != null)
									withresultEdition = true;
								else
									withresultEdition = false;
							}
						}
					} 
					
					// verification du te
					Calendar calander = Calendar.getInstance();
					if (indicator.getDataCollectors() != null && !indicator.getDataCollectors().isEmpty()) {
						IndicatorDataCollector lastDataCollector = indicator.getDataCollectors()
								.get(indicator.getDataCollectors().size() - 1);
						if (lastDataCollector.getMaxDate() != null && indicator.getObjectif().getDeley() != null
								&& indicator.getObjectif().getDeley() > 0) {
							calander.setTime(lastDataCollector.getMaxDate());
						}else {
							if(indicator.getObjectif().getTask() != null && indicator.getObjectif().getTask().getSection() != null && indicator.getObjectif().getTask().getSection().getProcess() != null) {
								if(indicator.getObjectif().getTask().getSection().getProcess().getStartAt() != null)
									calander.setTime(indicator.getObjectif().getTask().getSection().getProcess().getStartAt());
							}
						}
						// limitation le temps d'edition à 1/2 du delai d'evaluation de l'objectif
						LocalDate maxDate = LocalDate.of(calander.get(Calendar.YEAR), calander.get(Calendar.MONTH),calander.get(Calendar.DAY_OF_MONTH));
							String uniteDeley = indicator.getObjectif().getType().getUnite();
							if (maxDate != null && uniteDeley != null) {
								if (uniteDeley.toLowerCase().contains("jour")) {
									if ((ChronoUnit.DAYS.between(maxDate, LocalDate.now())) > indicator.getObjectif()
											.getDeley() / 2)
										withresultEdition = true;
									else
										withresultEdition = false;
								} else if (uniteDeley.toLowerCase().contains("moi")) {
									if ((ChronoUnit.MONTHS.between(maxDate, LocalDate.now())) > indicator.getObjectif()
											.getDeley() / 2)
										withresultEdition = true;
									else
										withresultEdition = false;
								} else if (uniteDeley.toLowerCase().contains("anne")) {
									if ((ChronoUnit.YEARS.between(maxDate, LocalDate.now())) > indicator.getObjectif()
											.getDeley() / 2)
										withresultEdition = true;
									else
										withresultEdition = false;
								} else {
									withresultEdition = true;
								}
							} else {
								withresultEdition = true;
							}
					}
				}
			}
			return withresultEdition;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
