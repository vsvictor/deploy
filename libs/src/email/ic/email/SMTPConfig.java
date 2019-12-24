package ic.email;

public class SMTPConfig {

	public final String host;
	public final int port;

	public final EmailAccount emailAccount;

	public SMTPConfig(String host, int port, EmailAccount emailAccount) {
		this.host = host;
		this.port = port;
		this.emailAccount = emailAccount;
	}

	public static class Gmail extends SMTPConfig {

		public Gmail(EmailAccount emailAccount) {
			super("smtp.gmail.com", 587, emailAccount);
		}

	}

}
