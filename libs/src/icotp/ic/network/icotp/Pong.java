package ic.network.icotp;


import ic.annotations.Necessary;
import ic.serial.stream.StreamSerializable;
import ic.stream.ByteInput;
import ic.stream.ByteOutput;


public final class Pong implements StreamSerializable {

	@Override public Class getClassToDeclare() { return Pong.class; }

	@Override public void serialize(ByteOutput output) {

	}

	@Necessary public Pong(ByteInput input) {

	}

	public Pong() {}

}
