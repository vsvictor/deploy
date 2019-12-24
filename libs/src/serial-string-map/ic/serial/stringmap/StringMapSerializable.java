package ic.serial.stringmap;


import ic.interfaces.setter.Setter1;
import ic.serial.Serializable;


public interface StringMapSerializable extends Serializable {

	String KEY_CLASS = "class";

	void serialize(Setter1<String, String> output);

}
