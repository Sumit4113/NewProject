package com.example.services;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailApiService {

	public boolean sendEmail(String subject, String message, String to) {

		boolean f = false;

		String from = "jdsumit01@gmail.com";

		// variable for gmail
		String host = "smtp.gmail.com";

		// get the system properties
		Properties properties = System.getProperties();
		System.out.println("Properties" + properties);

		// setting important information to properties object

		// host set
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");

		// Step 1: to get the session object..
		Session session = Session.getInstance(properties, new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {

				return new PasswordAuthentication("jdsumit01@gmail.com", "fuhm qssp mtyq fsah");
			}

		});
		session.setDebug(true);

		// step 2 send the message like anything

		MimeMessage mess = new MimeMessage(session);

		try {
			// from email message send
			mess.setFrom(from);

			// adding recipient to message
			mess.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

			// adding subject to message
			mess.setSubject(subject);

			// adding text to message
//			mess.setText(message);
			mess.setContent(message, "text/html");

			// send
			// step 3: send the message using Transport class
			Transport.send(mess);

			System.out.println("Send successfull");

			f = true;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return f;

	}
}
