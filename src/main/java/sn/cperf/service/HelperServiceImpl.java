package sn.cperf.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sn.cperf.dao.IndicateurRepository;
import sn.cperf.model.Indicateur;

@Component
public class HelperServiceImpl implements HelperService{

	@Autowired IndicatorService indicatorService;
	@Autowired IndicateurRepository indicatorRepository;
	@Override
	public boolean checkIndicatorDataResultEditable(Long indicatorId) {
		if(indicatorId != null && indicatorId >0) {
			Indicateur indicateur = new Indicateur();
			try {
				Optional<Indicateur> opIndicator = indicatorRepository.findById(indicatorId);
				if(opIndicator.isPresent())
					indicateur = opIndicator.get();
				if(indicateur != null && indicateur.getId() != null)
					return indicatorService.checkIndicatorDataResultEditable(indicateur);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// TODO Auto-generated method stub
		return false;
	}
	
}
