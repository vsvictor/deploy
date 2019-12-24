package ic.text;


import ic.annotations.Same;
import ic.struct.sequence.Sequence;
import ic.struct.sequence.Series;


public class SplitText implements Sequence<Text> {


	private final Text text;

	private final char divider;


	@ic.annotations.Default @Same
	protected boolean toSkipEmpty() { return false; }


	@Override public Series<Text> getIterator() {

		return new SplitInput(text.getIterator(), divider) {
			@Override protected boolean toSkipEmpty() { return SplitText.this.toSkipEmpty(); }
		};

	}


	public SplitText(Text text, char divider) {
		this.text = text;
		this.divider = divider;
	}

	public SplitText(String string, char divider) {
		this(new Text.FromString(string), divider);
	}


}
