package ic.email;

import ic.annotations.Degenerate;
import ic.annotations.Hide;
import org.jetbrains.annotations.NotNull;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.util.Properties;

public @Degenerate class Email { @Hide private Email() {}

	/**
	 * This method send email message with attachments to recipient,
	 * use Gmail.com as host with smtp tsl
	 *
	 * @param to - recipient email
	 * @param emailMessage - contain subject, text, @Nullable attachments
	 * @param smtpConfig - email service config which contain EmailAccount
	 */
	public static void sendEmail(@NotNull String to, @NotNull EmailMessage emailMessage, @NotNull SMTPConfig smtpConfig) {

		Properties prop = new Properties();
		prop.put("mail.smtp.host", smtpConfig.host);
		prop.put("mail.smtp.port", String.valueOf(smtpConfig.port));
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.smtp.starttls.enable", "true"); //TLS

		Session session = Session.getInstance(
			prop,
			new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(smtpConfig.emailAccount.name, smtpConfig.emailAccount.password);
				}
			}
		);

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(smtpConfig.emailAccount.name));

			message.setRecipients(
				Message.RecipientType.TO,
				InternetAddress.parse(to)
			);

			message.setSubject(emailMessage.subject);

			// Create the message part
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(emailMessage.text, "text/html; charset=utf-8");

			// Create a multipart message
			final Multipart multipart = new MimeMultipart();

			// Set text message part
			multipart.addBodyPart(messageBodyPart);

			emailMessage.attachments.forEach(attachment -> {
				try {
					// Add attachments
					BodyPart attachmentBodyPart = new MimeBodyPart();
					DataSource source = new ByteArrayDataSource(attachment.content, attachment.type);
					attachmentBodyPart.setDataHandler(new DataHandler(source));
					attachmentBodyPart.setFileName(attachment.name);
					multipart.addBodyPart(attachmentBodyPart);
				} catch (MessagingException e) {
					throw new RuntimeException(e);
				}
			});

			// Send the complete message parts
			message.setContent(multipart);

			Transport.send(message);

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}

	}

}
