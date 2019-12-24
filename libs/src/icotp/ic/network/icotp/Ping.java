package ic.network.icotp;


import ic.annotations.Necessary;
import ic.serial.stream.StreamSerializable;
import ic.stream.ByteInput;
import ic.stream.ByteOutput;


public final class Ping implements StreamSerializable {

	@Override public Class getClassToDeclare() { return Ping.class; }

	@Override public void serialize(ByteOutput output) {

	}

	@Necessary public Ping(ByteInput input) {

	}

	public Ping() {}

}
