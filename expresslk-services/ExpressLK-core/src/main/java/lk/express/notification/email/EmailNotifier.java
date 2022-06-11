package lk.express.notification.email;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import lk.express.notification.INotifier;
import lk.express.notification.NotificationCriteria;
import lk.express.notification.NotificationMessage;

/**
 * @author dilantha
 * 
 */
public class EmailNotifier implements INotifier {

	private static EmailNotifier notificationGateway;

	private static String SYSTEM_USERNAME;
	private static String SYSTEM_PASSWORD;

	private EmailNotifier() {

		init();
	}

	private void init() {
		// TODO load system settings
		SYSTEM_USERNAME = "";
		SYSTEM_PASSWORD = "";
	}

	public static EmailNotifier getInstance() {
		if (notificationGateway == null) {
			notificationGateway = new EmailNotifier();
		}
		return notificationGateway;
	}

	@Override
	public void send(NotificationCriteria criteria) {

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			@Override
			protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
				return new javax.mail.PasswordAuthentication(SYSTEM_USERNAME, SYSTEM_PASSWORD);
			}
		});

		try {

			NotificationMessage msg = criteria.getMessage();

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(SYSTEM_USERNAME));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(criteria.getAddress()));
			message.setSubject(msg.getTitle());
			message.setText(msg.getBody());
			Transport.send(message);

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}

	}

}
