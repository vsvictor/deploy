package ic.text;


import org.jetbrains.annotations.NotNull;
import ic.struct.sequence.ConvertSequence;


public class SplitString extends ConvertSequence<String, Text> {

	public SplitString(@NotNull String string, char divider) {
		super(
			new SplitText(string, divider),
			Text::toString
		);
	}

}
