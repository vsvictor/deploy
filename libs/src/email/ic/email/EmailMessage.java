package ic.email;

import java.util.ArrayList;
import java.util.List;

public class EmailMessage {

	public final String subject;
	public final String text;
	public final List<EmailAttachment> attachments;

	public EmailMessage(String subject, String text, List<EmailAttachment> attachments) {
		this.subject = subject;
		this.text = text;
		this.attachments = attachments;
	}

	public EmailMessage(String subject, String text) {
		this.subject = subject;
		this.text = text;
		this.attachments = new ArrayList<>();
	}

}
