package ic.text;


import ic.throwables.End;


public class ReplaceChar extends CharInput.BaseCharInput {


	private final CharInput originIterator;

	private final char from, to;


	@Override public char getChar() throws End {
		final char c = originIterator.getChar();
		if (c == from) return to;
		else return c;
	}

	public ReplaceChar(CharInput originIterator, char from, char to) {
		this.originIterator = originIterator;
		this.from = from;
		this.to = to;
	}


}
