package ic.text;


import ic.annotations.Redirect;

import ic.interfaces.emptiable.Emptiable;


public class TextBuffer extends Text.BaseText implements CharOutput, Emptiable {


	private StringBuffer stringBuffer = new StringBuffer();


	public int getLength() {
		return stringBuffer.length();
	}


	// Emptiable implementation:

	@Override public void empty() {
		stringBuffer = new StringBuffer();
	}


	// Text implementation:

	@Override public CharInput getIterator() {
		return new Text.FromString(stringBuffer.toString()).getIterator();
	}


	// CharOutput implementation:

	@Override public void put(char c) 				{ stringBuffer.append(c); 	}
	@Override public void put(Character character) 	{ put((char) character); 	}

	@Override public void close() {}

	private final BaseCharOutput baseOutput = new BaseCharOutput() {
		@Redirect @Override public void put(char c) 					{ TextBuffer.this.put(c); 			}
		@Redirect @Override public void put(Character character) 		{ TextBuffer.this.put(character); 	}
		@Redirect @Override public void close() 						{ TextBuffer.this.close(); 			}
	};

	@Redirect @Override public void write(Text text) 			{ baseOutput.write(text); 		}
	@Redirect @Override public void write(String string) 		{ baseOutput.write(string); 	}
	@Redirect @Override public void writeLine(Text text) 		{ baseOutput.writeLine(text); 	}
	@Redirect @Override public void writeLine(String string) 	{ baseOutput.writeLine(string); }
	@Redirect @Override public void writeLine() 				{ baseOutput.writeLine(); 		}


	// Object implementation:

	@Override public String toString() {
		return stringBuffer.toString();
	}


	public TextBuffer() {}

	public TextBuffer(char c) {
		stringBuffer.append(c);
	}


}