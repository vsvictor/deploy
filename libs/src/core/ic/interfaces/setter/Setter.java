package ic.interfaces.setter;


import ic.annotations.Repeat;


public interface Setter<Type> extends SafeSetter<Type, RuntimeException> {

	@Repeat @Override void set (Type value);

}
