package com.flexdms.flexims.util;

import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.flexdms.flexims.config.ConfigItem;
import com.flexdms.flexims.config.Configs;

@RequestScoped
public class EmailSender implements EmailSenderI {
	public static final Logger LOGGER = Logger.getLogger(EmailSender.class.getCanonicalName());

	Session session;

	String username;
	String password;

	public static final String MAIL_DOMAIN = "mail";
	public static final String MAIL_SMTP_DOMAIN = "mail.smtp";

	@PostConstruct
	public void createMailSession() {

		if (session != null) {
			session = null;
		}
		/*
		 * InitialContext ic = new InitialContext(); String snName =
		 * "java:app/mail/flexims"; Session session =
		 * (Session)ic.lookup(snName); return session;
		 */
		Properties props = new Properties();
		for (ConfigItem citem : Configs.getItems().values()) {
			if (citem.getName().startsWith(MAIL_DOMAIN)) {
				if (citem.getValue() != null) {
					props.put(citem.getName(), citem.getValue());
					if (citem.getName().equals(MAIL_SMTP_DOMAIN + ".username")) {
						username = citem.getValue();
					} else if (citem.getName().equals(MAIL_SMTP_DOMAIN + ".password")) {
						password = citem.getValue();
					}
				}
			}
		}
		if (password != null) {
			props.put("mail.smtp.auth", "true");
		}
		if (props.isEmpty()) {
			session = null;
		} else {
			session = Session.getInstance(props);
		}

	}

	public MimeMessage createEmptyMessage() {
		return new MimeMessage(session);
	}

	public void sendMessage(MimeMessage msg) throws MessagingException {
		if (password != null) {
			Transport t = session.getTransport("smtp");
			try {
				t.connect(username, password);
				t.sendMessage(msg, msg.getAllRecipients());
			} finally {
				t.close();
			}
		} else {
			Transport.send(msg);
		}
	}

	public void sendMessage(String to, String subject, String content)  {
		if (session == null) {
			LOGGER.info("email service is not available. Email is not sent out");
		}
		try {
			MimeMessage msg = createEmptyMessage();
			msg.setSubject(subject);
			msg.setSentDate(new Date());
			msg.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(to, false));
			msg.setText(content);
			sendMessage(msg);
		} catch (Exception e) {
			LOGGER.warning("Error when sending email " + e.getMessage());
		}
	}

}
