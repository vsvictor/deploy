package ic.text;


import ic.annotations.Default;
import ic.annotations.Same;
import ic.struct.sequence.Series;
import ic.throwables.End;


public class SplitInput implements Series<Text> {


	private final CharInput charInput;

	private final char divider;


	@Default @Same
	protected boolean toSkipEmpty() { return false; }


	private TextBuffer textBuffer;

	private boolean end = false;

	@Override public Text get() throws End {

		if (end) throw new End();

		while (true) {

			if (textBuffer == null) textBuffer = new TextBuffer();

			try {

				final char c = charInput.get();

				if (c == divider) {

					final TextBuffer textBuffer = this.textBuffer;

					this.textBuffer = null;

					if (toSkipEmpty() && textBuffer.isEmpty()) continue;

					return textBuffer;

				}

				textBuffer.put(c);

			} catch (End end) {

				this.end = true;

				final TextBuffer textBuffer = this.textBuffer;

				this.textBuffer = null;

				if (toSkipEmpty() && textBuffer.isEmpty()) continue;

				return textBuffer;

			}

		}

	}


	public SplitInput(CharInput charInput, char divider) {
		this.charInput = charInput;
		this.divider = divider;
	}


}
