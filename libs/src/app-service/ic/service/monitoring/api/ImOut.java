package ic.service.monitoring.api;


import ic.annotations.Necessary;
import org.jetbrains.annotations.NotNull;
import ic.stream.ByteInput;
import ic.stream.ByteOutput;
import ic.throwables.UnableToParse;

import static ic.serial.stream.ParseKt.parseFromStream;
import static ic.serial.stream.SerializeKt.serializeToStream;


public class ImOut extends ProtectedRequest {


	public final @NotNull String name;


	@Override public Class getClassToDeclare() { return ImOut.class; }

	@Override public void serialize(ByteOutput output) {
		super.serialize(output);
		serializeToStream(output, name);
	}

	@Necessary public ImOut(ByteInput input) throws UnableToParse {
		super(input);
		this.name = parseFromStream(input);
	}


	public ImOut(@NotNull String secretKey, @NotNull String name) {
		super(secretKey);
		this.name = name;
	}


}
