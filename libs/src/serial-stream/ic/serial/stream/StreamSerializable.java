package ic.serial.stream;


import ic.serial.Serializable;
import ic.stream.ByteOutput;
import org.jetbrains.annotations.NotNull;


public interface StreamSerializable extends Serializable {

	void serialize(@NotNull ByteOutput output);

}
