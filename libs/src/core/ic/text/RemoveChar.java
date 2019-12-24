package ic.text;


import ic.throwables.End;


public class RemoveChar extends CharInput.BaseCharInput {


	private final CharInput originCharInput;

	private final char charToRemove;


	@Override public char getChar() throws End {
		while (true) {
			char c = originCharInput.getChar();
			if (c == charToRemove) continue;
			else return c;
		}
	}


	public RemoveChar(CharInput originCharInput, char charToRemove) {
		this.originCharInput = originCharInput;
		this.charToRemove = charToRemove;
	}

}
