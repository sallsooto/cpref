package sn.cperf.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import sn.cperf.dao.ParamRepository;
@Service
public class MailServiceImpl implements MailService{
	@Autowired
	public JavaMailSender sender;
	
	@Autowired Environment env;
	@Autowired ParamRepository paramRepository;

	@Override
	public void sendSimpleMail(String to, String subject, String text) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(to);
			message.setSubject(subject);
			message.setText(text);
			sender.send(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendHtmlTextMail(String to, String subject, String htmltext) {
		try {
			MimeMessage message = sender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message);
			helper.setTo(to);
			helper.setText(htmltext, true); // set to html
			helper.setSubject(subject);
			sender.send(message);
		} catch (MailException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendMailForAdminOnCreatedUserAccount(String attachementUri, String registrer) {
		String subject = "Nouveau compte créé dans C'perf";
		String to = getMainAdminEmail();
		String text = registrer + " a crée un nouveau compte " + "<a href='" + attachementUri
				+ "'>Cliquez ici si vous souhaitez l'activer maintenant .</a>";
		this.sendSimpleMail(to, subject, text);
	}

	@Override
	public void sendHtmlMailForAdminOnCreatedUserAccount(String attachementUri, String registrer) {
		try {
			String text = registrer + " a crée un nouveau compte " + "<a href='" + attachementUri
					+ "'>Cliquez ici si vous souhaitez l'activer maintenant .</a>";
			sendHtmlTextMail(getMainAdminEmail(), "C'perf nouveau compte", text);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void sendUserMailWheneHirAcountIsActived(String appCtxUri,String userName, String userEmail) {
		String text="Bonjour " + userName + ", vôtre compte est activé <a href='"+appCtxUri +"'> cliquez ici pour vous connecter</a>";
		this.sendHtmlTextMail(userEmail, "C'perf compte activation", text);
	}

	@Override
	public String getMainAdminEmail() {
		String adminEmail =null;
		try {
			adminEmail = paramRepository.findBySlug("admin_email").getParam();
		} catch (Exception e) {
		}
		if(adminEmail == null) {
			try {
				adminEmail = env.getProperty("spring.cperf.admin.email");
			} catch (Exception e) {
			}
		}
		return adminEmail;
	}
}
