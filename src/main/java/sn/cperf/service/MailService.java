package sn.cperf.service;

public interface MailService {
	public void sendSimpleMail(String to, String subject, String text);
	public void sendHtmlTextMail(String to,String subject,String htmltext);
	// methode qui envoie d'email à l'administrateur lors de la création d'un nouveau compte
	public void sendMailForAdminOnCreatedUserAccount(String attachementUri,String registrer);
	public void sendHtmlMailForAdminOnCreatedUserAccount(String attachementUri,String registrer);
	public void sendUserMailWheneHirAcountIsActived(String appLoginUri,String userName,String userEmail);
	public String getMainAdminEmail();
}
