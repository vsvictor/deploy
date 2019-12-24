package ic;


import ic.annotations.Necessary;
import org.jetbrains.annotations.NotNull;
import ic.stream.ByteInput;
import ic.stream.ByteOutput;
import ic.serial.stream.StreamSerializable;
import ic.throwables.NotExists;
import ic.throwables.UnableToParse;

import static ic.serial.stream.ParseKt.parseFromStream;
import static ic.serial.stream.SerializeKt.serializeToStream;


public class PackageInstance implements StreamSerializable {


	private String storeName;

	public String getStoreName() { return storeName; }

	public void setStore(@NotNull Store store) {
		this.storeName = store.name;
	}


	private String version;

	public String getVersion() { return version; }

	public void setVersion(@NotNull String branch) {
		this.version = branch;
	}


	public void save(@NotNull Distribution distribution, @NotNull String packageName) {
		distribution.getPackagesDirectory().set(packageName, this);
	}


	@Override @NotNull public Class getClassToDeclare() { return PackageInstance.class; }

	@Override public void serialize(ByteOutput output) {
		serializeToStream(output, storeName);
		serializeToStream(output, version);
	}

	@Necessary public PackageInstance(ByteInput input) throws UnableToParse {
		this.storeName 	= parseFromStream(input);
		this.version 	= parseFromStream(input);
	}


	public PackageInstance(@NotNull String storeName, @NotNull String version) {
		this.storeName = storeName;
		this.version = version;
	}


	public static PackageInstance byName(Distribution dirs, String packageName) throws NotExists {
		return dirs.getPackagesDirectory().getOrThrow(packageName);
	}


}
