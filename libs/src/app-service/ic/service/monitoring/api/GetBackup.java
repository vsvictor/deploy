package ic.service.monitoring.api;


import ic.annotations.Necessary;
import ic.serial.stream.StreamSerializable;
import ic.stream.ByteInput;
import ic.stream.ByteOutput;


public class GetBackup implements StreamSerializable {

	@Override public Class getClassToDeclare() { return GetBackup.class; }

	@Override public void serialize(ByteOutput output) {}

	@Necessary public GetBackup(ByteInput input) {}

	public GetBackup() {}

}
