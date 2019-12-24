package ic.service.monitoring.api;


import ic.annotations.Necessary;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
import ic.stream.ByteInput;
import ic.stream.ByteOutput;
import ic.throwables.UnableToParse;

import static ic.serial.stream.ParseKt.parseFromStream;
import static ic.serial.stream.SerializeKt.serializeToStream;


public class ImIn extends ProtectedRequest {


	public final @NotNull String name;

	public final @NotNull Pair<String, String> sshAuth;

	public final @NotNull String appPackageName;


	@Override public Class getClassToDeclare() { return ImIn.class; }

	@Override public void serialize(ByteOutput output) {
		super.serialize(output);
		serializeToStream(output, name);
		serializeToStream(output, sshAuth);
		serializeToStream(output, appPackageName);
	}

	@Necessary public ImIn(ByteInput input) throws UnableToParse {
		super(input);
		this.name 			= parseFromStream(input);
		this.sshAuth 		= parseFromStream(input);
		this.appPackageName = parseFromStream(input);
	}


	public ImIn(@NotNull String monitorKey, @NotNull String name, @NotNull Pair<String, String> sshAuth, @NotNull String appPackageName) {
		super(monitorKey);
		this.name = name;
		this.sshAuth = sshAuth;
		this.appPackageName = appPackageName;
	}


}
