package ic.text;


import ic.interfaces.action.SafeAction;
import ic.annotations.*;

import ic.struct.sequence.Series;
import ic.throwables.NotExists;
import ic.throwables.End;

import java.io.IOException;
import java.io.Reader;

import static ic.throwables.End.END;
import static ic.throwables.NotExists.NOT_EXISTS;


public interface CharInput extends Series<Character> {


	char getChar() throws End;


	Text read();

	Text read(int amount);

	Text safeRead(int amount) throws End;

	Text readTill(String string);

	@Overload Text readTill(char c);

	Text readLine();

	Text safeReadTill(String string) throws NotExists;

	Text safeReadTillIgnoreCase(String string) throws NotExists;

	@Overload Text safeReadTill(char c) throws NotExists;

	Text safeReadLine() throws NotExists;

	Reader toReader();


	abstract class BaseCharInput implements CharInput {

		@Override public Character get() throws End {
			return getChar();
		}

		@Override public Text read() {
			final TextBuffer textBuffer = new TextBuffer();
			try { while (true) {
				textBuffer.put(getChar());
			} } catch (End end) {}
			return textBuffer;
		}

		@Override public Text read(int amount) {
			final TextBuffer textBuffer = new TextBuffer();
			try {
				for (int i = 0; i < amount; i++) {
					textBuffer.put(getChar());
				}
			} catch (End end) {}
			return textBuffer;
		}

		@Override public Text safeRead(int amount) throws End {
			final TextBuffer textBuffer = new TextBuffer();
			for (int i = 0; i < amount; i++) {
				textBuffer.put(getChar());
			}
			return textBuffer;
		}

		private <NotFoundThrowable extends Throwable> Text implementReadTill(String string, boolean ignoreCase, SafeAction<NotFoundThrowable> notFoundAction) throws NotFoundThrowable {
			final TextBuffer textBuffer = new TextBuffer();
			int stringIndex = 0;
			try {
				while (true) {
					char c = getChar();
					boolean equal; {
						if (ignoreCase) {
							equal = Character.toLowerCase(c) == Character.toLowerCase(string.charAt(stringIndex));
						} else {
							equal = c == string.charAt(stringIndex);
						}
					}
					if (equal) {
						stringIndex++;
						if (stringIndex >= string.length()) {
							break;
						}
					} else {
						for (int i = 0; i < stringIndex; i++) {
							textBuffer.put(string.charAt(i));
						}
						stringIndex = 0;
						textBuffer.put(c);
					}
				}
			} catch (End end) {
				notFoundAction.run();
			}
			return textBuffer;
		}

		private static final SafeAction<RuntimeException> UNSAFE_NOT_FOUND_ACTION = new SafeAction<RuntimeException>() {
			@DoesNothing @Override public void run() {}
		};

		@Override public Text readTill(String string) {
			return implementReadTill(string, false, UNSAFE_NOT_FOUND_ACTION);
		}

		@Overload @Override public Text readTill(char c) {
			return readTill(Character.toString(c));
		}

		@Override public Text readLine() {
			return readTill('\n');
		}

		private static final SafeAction<NotExists> SAFE_NOT_FOUND_ACTION = new SafeAction<NotExists>() {
			@AlwaysThrows @Override public void run() throws NotExists { throw NOT_EXISTS; }
		};

		@Override public Text safeReadTill(String string) throws NotExists {
			return implementReadTill(string, false, SAFE_NOT_FOUND_ACTION);
		}

		@Override public Text safeReadTillIgnoreCase(String string) throws NotExists {
			return implementReadTill(string, true, SAFE_NOT_FOUND_ACTION);
		}

		@Overload @Override public Text safeReadTill(char c) throws NotExists {
			return safeReadTill(Character.toString(c));
		}

		@Override public Text safeReadLine() throws NotExists {
			return safeReadTill('\n');
		}

		@Override public Reader toReader() {
			return new Reader() {
				@Override public int read() throws IOException {
					try {
						return BaseCharInput.this.getChar();
					} catch (End end) {
						return -1;
					}
				}
				@Override public int read(char[] chars, int offset, int length) throws java.io.IOException {
					for (int i = offset; i < offset + length; i++) {
						try {
							chars[i] = BaseCharInput.this.getChar();
						} catch (End end) {
							if (i == 0) return -1;
							return i;
						}
					}
					return length;
				}
				@Override public boolean ready() throws IOException {
					return true;
				}
				@Override public void close() throws java.io.IOException {}
			};
		}

	}


	public static final CharInput EMPTY = new BaseCharInput() {
		@Override public char getChar() throws End {
			throw END;
		}
	};


}
