package ic.text;


import ic.annotations.Overload;
import ic.annotations.ToOverride;
import ic.interfaces.closeable.Closeable;
import ic.interfaces.putter.Putter;
import ic.interfaces.writer.Writer;
import ic.throwables.End;
import org.jetbrains.annotations.NotNull;


public interface CharOutput extends

	Putter<Character>, Writer<Text>, Closeable

{

	void put(char c);

	@Overload void write(String string);

	void writeLine(Text text);

	@Overload void writeLine(String string);

	@Overload void writeLine();


	abstract class BaseCharOutput implements CharOutput {

		@ToOverride
		@Override public void put(Character c) {
			put((char) c);
		}

		@Override public void write(@NotNull Text text) {

			final CharInput charInput = text.getIterator(); try {
				while (true) {
					put(charInput.getChar());
				}
			} catch (End end) {}

		}

		@ToOverride
		@Override @Overload public void write(@NotNull String string) {
			write(new Text.FromString(string));
		}

		@Override public void writeLine(Text text) {
			write(text);
			put('\n');
		}

		@Override public void writeLine(@NotNull String string) {
			write(string);
			put('\n');
		}

		@Override public void writeLine() {
			put('\n');
		}

	}


}
