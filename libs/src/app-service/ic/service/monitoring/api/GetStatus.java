package ic.service.monitoring.api;


import ic.annotations.Necessary;
import ic.serial.stream.StreamSerializable;
import ic.stream.ByteInput;
import ic.stream.ByteOutput;


public class GetStatus implements StreamSerializable {

	@Override public Class getClassToDeclare() { return GetStatus.class; }

	@Override public void serialize(ByteOutput output) {}

	@Necessary public GetStatus(ByteInput input) {}

	public GetStatus() {}

}
