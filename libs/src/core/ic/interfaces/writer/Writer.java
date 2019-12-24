package ic.interfaces.writer;

import ic.annotations.Repeat;


public interface Writer<Type> extends SafeWriter<Type, RuntimeException> {

	@Repeat @Override void write(Type value);

}
