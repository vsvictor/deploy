package ic.email;

public class EmailAttachment {

	public final String name;
	public final byte[] content;
	public final String type;

	public EmailAttachment(String name, byte[] content, String type) {
		this.name = name;
		this.content = content;
		this.type = type;
	}
}
