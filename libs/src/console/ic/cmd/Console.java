package ic.cmd;


import ic.annotations.Redirect;
import ic.annotations.ToOverride;
import ic.annotations.Same;
import ic.interfaces.getter.Getter;
import ic.text.CharInput;
import ic.text.CharOutput;
import ic.text.Text;
import ic.throwables.NotExists;
import ic.throwables.End;
import kotlin.jvm.functions.Function0;

import java.io.Reader;


public interface Console extends CharOutput, CharInput {


	Text readMultiLine();


	@Same
	boolean isSimplified();


	abstract class BaseConsole extends BaseCharOutput implements Console {


		@Override public Text readMultiLine() {
			return readTill("\n\n\n\n");
		}


		@ToOverride
		@Override public boolean isSimplified() { return false; }


		// BaseCharInput implementation:

		private BaseCharInput baseInput = new BaseCharInput() {
			@Redirect @Override public char getChar() throws End { return BaseConsole.this.getChar(); }
		};

		@Redirect @Override public Character get() throws End 														{ return baseInput.get(); 							}
		@Redirect @Override public Character getOrThrow() throws NotExists, End 									{ return baseInput.getOrThrow(); 					}
		@Redirect @Override public Character getNotNull() throws NotExists.Runtime, End 							{ return baseInput.getNotNull(); 					}
		@Redirect @Override public Character get(Function0<? extends Character> defaultValueGetter) throws End	 			{ return baseInput.get(defaultValueGetter); 		}
		@Redirect @Override public Text read() 																		{ return baseInput.read(); 							}
		@Redirect @Override public Text read(int amount) 															{ return baseInput.read(amount); 					}
		@Redirect @Override public Text safeRead(int amount) throws End 											{ return baseInput.safeRead(amount); 				}
		@Redirect @Override public Text readTill(char c) 															{ return baseInput.readTill(c); 					}
		@Redirect @Override public Text readTill(String string) 													{ return baseInput.readTill(string); 				}
		@Redirect @Override public Text readLine() 																	{ return baseInput.readLine(); 						}
		@Redirect @Override public Text safeReadTill(char c) throws NotExists 										{ return baseInput.safeReadTill(c); 				}
		@Redirect @Override public Text safeReadTill(String string) throws NotExists 								{ return baseInput.safeReadTill(string); 			}
		@Redirect @Override public Text safeReadTillIgnoreCase(String string) throws RuntimeException, NotExists 	{ return baseInput.safeReadTillIgnoreCase(string); 	}
		@Redirect @Override public Text safeReadLine() throws RuntimeException, NotExists 							{ return baseInput.safeReadLine();			 		}
		@Redirect @Override public Reader toReader() 																{ return baseInput.toReader(); 						}
		@Redirect @Override public void skip() 																		{ baseInput.skip();	 								}
		@Redirect @Override public void skip(int amount) throws End 												{ baseInput.skip(amount); 							}


	}


}
