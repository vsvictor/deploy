package ic.chatbot;


import ic.interfaces.action.Action;
import ic.interfaces.action.Action1;


public abstract class Button {

	public abstract String getText();

	public static abstract class CommandButton extends Button implements Action {
		public final String command;
		protected CommandButton(String command) {
			assert command != null;
			this.command = command;
		}
	}

	public static abstract class RequestPhoneButton extends CommandButton implements Action1<String> {
		protected RequestPhoneButton(String command) {
			super(command);
		}
	}

	public static abstract class UrlButton extends Button {
		public final String url;
		protected UrlButton(String url) {
			this.url = url;
		}
	}

}
