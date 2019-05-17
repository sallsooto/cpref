package sn.cperf.service;

import sn.cperf.model.User;

public interface CperfService {
	//public boolean userHasRole(User user, String role);
	public User getLoged();
	// ajouter tous les élément ou entity nécéssaire 
	// pour le bon fonctionnement de l'application qui doivent être dans la base de données
	public void doAllNecessaryOperationsAfterLunchApplication();
}
