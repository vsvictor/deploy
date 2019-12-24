package ic.service.monitoring.api;


import ic.annotations.Necessary;
import ic.serial.stream.StreamSerializable;
import ic.stream.ByteInput;
import ic.stream.ByteOutput;
import ic.throwables.UnableToParse;

import static ic.serial.stream.ParseKt.parseFromStream;
import static ic.serial.stream.SerializeKt.serializeToStream;


public abstract class ProtectedRequest implements StreamSerializable {


	public final String monitorKey;


	@Override public void serialize(ByteOutput output) {
		serializeToStream(output, monitorKey);
	}

	@Necessary public ProtectedRequest(ByteInput input) throws UnableToParse {
		this.monitorKey = parseFromStream(input);
	}


	public ProtectedRequest(String monitorKey) {
		this.monitorKey = monitorKey;
	}


}
