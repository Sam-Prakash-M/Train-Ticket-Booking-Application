package com.samprakash.profileviewmodel;

import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class MailService {

	private static String SENDER_EMAIL = "apitestdevelopment@gmail.com";
	private static String APP_PASSWORD = "xgtlitnapfxusyyn";

	public static void sendOtpEmail(String recipientEmail, String otp) throws Exception {

		// 1. Setup Mail Server Properties
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

		// 2. Authenticate
		Session session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(SENDER_EMAIL, APP_PASSWORD);
			}
		});

		// 3. Construct Message
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(SENDER_EMAIL, "Sam Railways Support"));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
		message.setSubject("Your One-Time Password (OTP) - Sam Railways");

		// HTML Body
		String htmlContent = "<div style='font-family: Arial, sans-serif; padding: 20px; border: 1px solid #ddd; border-radius: 10px; max-width: 500px;'>"
				+ "<h2 style='color: #4f46e5;'>Sam Railways</h2>" + "<p>Hello,</p>"
				+ "<p>You requested to retrieve your username. Please use the following OTP to proceed:</p>"
				+ "<h1 style='background: #f0f4f8; padding: 10px; text-align: center; letter-spacing: 5px; color: #333; border-radius: 5px;'>"
				+ otp + "</h1>"
				+ "<p style='color: #666; font-size: 12px;'>This code is valid for 5 minutes. Do not share it with anyone.</p>"
				+ "</div>";

		message.setContent(htmlContent, "text/html; charset=utf-8");

		// 4. Send
		Transport.send(message);
	}

	public static String generateOTP() {
		int otp = 100000 + (int) (Math.random() * 900000);
		return String.valueOf(otp);
	}
}
