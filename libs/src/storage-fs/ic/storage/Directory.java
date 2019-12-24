package ic.storage;


import java.io.*;

import org.jetbrains.annotations.NotNull;
import ic.annotations.*;
import ic.cmd.SystemConsole;
import ic.stream.*;
import ic.struct.collection.Collection;
import ic.struct.collection.ConvertCollection;
import ic.struct.order.OrderedCountableSet;
import ic.text.Charset;
import ic.text.Text;
import ic.throwables.*;
import ic.throwables.IOException;

import static ic.struct.order.Order.ALPHABETIC_STRING_ORDER;
import static ic.util.Hex.longToFixedSizeHexString;
import static ic.throwables.AlreadyExists.ALREADY_EXISTS;
import static ic.throwables.NotExists.NOT_EXISTS;
import static java.lang.System.nanoTime;


public class Directory extends StreamStorage.BaseStreamStorage {


	public static String getPublicIcPath() { return "/home/public/.ic"; }


	@ToOverride
	@Same
	protected boolean toEncodeFileNames() { return true; }


	public String encodeFileName(@NotNull String fileName) {
		if (!toEncodeFileNames()) return fileName;
		return Charset.DEFAULT_UNIX.encodeUrl(fileName);
	}

	public String decodeFileName(@NotNull String fileName) {
		if (!toEncodeFileNames()) return fileName;
		return Charset.DEFAULT_UNIX.decodeUrl(fileName);
	}


	private final File file;

	public final String absolutePath;

	private final String name;

	@Override public String getName() { return name; }


	// Keys:

	@Override public synchronized OrderedCountableSet<String> getKeys() {
		return new OrderedCountableSet.Default<>(
			ALPHABETIC_STRING_ORDER,
			new ConvertCollection<>(
				new Collection.Default<>(
					file.list()
				),
				this::decodeFileName
			)
		);
	}


	// Folders:

	@Override public synchronized boolean isFolder(String key) throws NotExists {
		final File childFile = new File(file, encodeFileName(key));
		if (!childFile.exists()) throw NOT_EXISTS;
		return childFile.isDirectory();
	}

	@Override protected synchronized Directory implementGetFolder(String key) throws NotExists {
		return new Directory(new File(file, encodeFileName(key)));
	}

	@Override protected synchronized Directory implementCreateFolder(String key) throws AlreadyExists {
		final File childFile = new File(file, encodeFileName(key));
		final String childFilePath = childFile.getAbsolutePath();
		if (childFile.exists()) throw ALREADY_EXISTS;
		if (!childFile.mkdir()) throw new IOException.Runtime("Can't create directory " + childFilePath);
		final String publicIcDirPath = getPublicIcPath();
		if (absolutePath.equals(publicIcDirPath) || absolutePath.startsWith(publicIcDirPath + "/")) {
			new SystemConsole().writeLine("chmod -R 777 " + childFilePath);
		}
		try {
			return new Directory(childFile);
		} catch (NotExists notExists) { throw new InconsistentData(notExists); }
	}


	// File input and output:

	@Override public synchronized @NotNull ByteInput getInput(@NotNull String key) throws NotExists {
		try {
			return new ByteInput.FromInputStream(
				new BufferedInputStream(
					new FileInputStream(
						new File(file, encodeFileName(key))
					)
				)
			);
		} catch (FileNotFoundException e) { throw new NotExists(e); }
	}

	@Override public synchronized @NotNull CancelableByteOutput getOutput(@NotNull final String key) {
		final File tmpChildFile = new File(file, ".tmp" + longToFixedSizeHexString(nanoTime()));
		class FileOutput extends ByteOutput.FromOutputStream implements CancelableByteOutput {
			@Override public void cancel() {
				super.close();
				assert tmpChildFile.delete();
			}
			@Override public void close() {
				super.close();
				synchronized (Directory.this) {
					final SystemConsole systemConsole = new SystemConsole();
					final String tmpChildPath = tmpChildFile.getAbsolutePath();
					final String publicIcDirPath = getPublicIcPath();
					if (absolutePath.equals(publicIcDirPath) || absolutePath.startsWith(publicIcDirPath + "/")) {
						systemConsole.writeLine("chmod 777 " + tmpChildPath);
					}
					systemConsole.writeLine("mv -f " + tmpChildPath + " " + absolutePath + "/" + encodeFileName(key));
					final Text response = systemConsole.read();
					if (!response.isEmpty()) {
						throw new IOException.Runtime(response.toString());
					}
				}
			}
			FileOutput(OutputStream outputStream) { super(outputStream); }
		}
		try {
			return new FileOutput(new BufferedOutputStream(new FileOutputStream(tmpChildFile)));
		} catch (FileNotFoundException e) { throw new IOException.Runtime(e); }
	}

	public synchronized @NotNull ByteOutput getAppendOutput(@NotNull final String key) {
		try {
			final File childFile = new File(file, encodeFileName(key));
			return new ByteOutput.FromOutputStream(new BufferedOutputStream(new FileOutputStream(childFile, true)));
		} catch (FileNotFoundException e) { throw new IOException.Runtime(e); }
	}


	// Remove:

	@Override public synchronized void removeOrThrow(String key) throws NotExists {
		final File childFile = new File(file, encodeFileName(key));
		if (!childFile.exists()) throw NOT_EXISTS;
		if (file.isDirectory()) {
			final SystemConsole systemConsole = new SystemConsole();
			systemConsole.writeLine("rm -rf " + childFile.getAbsolutePath());
			final Text response = systemConsole.read();
			if (!response.isEmpty()) {
				throw new IOException.Runtime(response.toString());
			}
		} else {
			if (!childFile.delete()) throw new IOException.Runtime("Can't delete file" + childFile.getAbsolutePath());
		}
	}


	// Narrowing Storage methods to Directory:

	@Narrowing @Repeat @Override public Directory getFolder(String key) throws NotExists.Runtime 								{ return (Directory) super.getFolder(key); 							}
	@Narrowing @Repeat @Override public Directory createFolder(String key) throws AlreadyExists.Runtime 						{ return (Directory) super.createFolder(key); 						}
	@Narrowing @Repeat @Override public Directory createFolderOrThrow(String key) throws AlreadyExists 							{ return (Directory) super.createFolderOrThrow(key); 				}
	@Narrowing @Repeat @Override public Directory getFolderOrThrow(String key) throws NotExists 								{ return (Directory) super.getFolderOrThrow(key); 					}
	@Narrowing @Repeat @Override public synchronized Directory createFolderIfNotExists(String key) throws InvalidValue.Runtime 	{ return (Directory) super.createFolderIfNotExists(key); 			}


	// Moving folders:

	@Override protected void implementSetFolder(@NotNull String key, @NotNull Storage value) {
		if (value instanceof Directory) { final Directory directory = (Directory) value;
			final File childFile = new File(file, encodeFileName(key));
			final SystemConsole systemConsole = new SystemConsole();
			if (childFile.isDirectory()) {
				systemConsole.writeLine("rm -rf " + childFile.getAbsolutePath());
				final Text response = systemConsole.read();
				if (!response.isEmpty()) {
					throw new IOException.Runtime(response.toString());
				}
			}
			systemConsole.writeLine("yes | cp -rf " + directory.absolutePath + " " + childFile.getAbsolutePath());
			final Text response = systemConsole.read();
			if (!response.isEmpty()) {
				throw new IOException.Runtime(response.toString());
			}
		} else {
			super.implementSetFolder(key, value);
		}
	}

	@Override protected synchronized void implementSetNonFolder(@NotNull String key, @NotNull Object value) {
		final File childFile = new File(file, encodeFileName(key));
		if (childFile.isDirectory()) {
			final SystemConsole systemConsole = new SystemConsole();
			systemConsole.writeLine("rm -rf " + childFile.getAbsolutePath());
			final Text response = systemConsole.read();
			if (!response.isEmpty()) {
				throw new IOException.Runtime(response.toString());
			}
		}
		super.implementSetNonFolder(key, value);
	}


	// Parent:

	@Override public Directory getParent() {
		final File parentFile = file.getParentFile();
		if (parentFile == null) return null;
		try {
			return new Directory(parentFile);
		} catch (NotExists notExists) { throw new InconsistentData(notExists); }
	}


	// Relative path:

	@SuppressWarnings("ConstantConditions")
	public String relativizePath(@NotNull String path) {
		assert path != null;
		final String[] baseWords = this.absolutePath.substring(1).split("/");
		final String[] pathWords = path.substring(1).split("/");
		int wordIndex = 0;
		while (true) {
			if (wordIndex < baseWords.length && wordIndex < pathWords.length) {
				if (pathWords[wordIndex].equals(baseWords[wordIndex])) {
					wordIndex++;
					continue;
				} else {
					final StringBuilder stringBuilder = new StringBuilder();
					for (int i = wordIndex; i < baseWords.length; i++) {
						if (stringBuilder.length() > 0) stringBuilder.append('/');
						stringBuilder.append("..");
					}
					for (int i = wordIndex; i < pathWords.length; i++) {
						stringBuilder.append('/');
						stringBuilder.append(pathWords[i]);
					}
					return stringBuilder.toString();
				}
			} else
			if (wordIndex < baseWords.length && wordIndex >= pathWords.length) {
				final StringBuilder stringBuilder = new StringBuilder();
				for (int i = wordIndex; i < baseWords.length; i++) {
					if (stringBuilder.length() > 0) stringBuilder.append('/');
					stringBuilder.append("..");
				}
				return stringBuilder.toString();
			} else
			if (wordIndex >= baseWords.length && wordIndex < pathWords.length) {
				final StringBuilder stringBuilder = new StringBuilder();
				for (int i = wordIndex; i < pathWords.length; i++) {
					if (stringBuilder.length() > 0) stringBuilder.append('/');
					stringBuilder.append(pathWords[i]);
				}
				return stringBuilder.toString();
			} else
			if (wordIndex >= baseWords.length && wordIndex >= pathWords.length) {
				return ".";
			}
		}
	}


	// Symbolic links:

	public void createSymbolicLink(String toName, Directory fromDirectory, String fromName) throws AlreadyExists {

		final SystemConsole systemConsole = new SystemConsole();

		systemConsole.writeLine("ln -s " + fromDirectory.absolutePath + "/" + fromName + " " + absolutePath + "/" + toName);

		final Text response = systemConsole.read();

		if (response.endsWith(": File exists")) throw new AlreadyExists("toDirectory: " + absolutePath + ", toName: " + toName);

		if (!response.isEmpty()) throw new RuntimeException(response.toString());

	}

	public void createSymbolicLink(final @NotNull String toName, final @NotNull Directory from, final boolean relative) throws AlreadyExists {

		final String fromPath; {
			if (relative) {
				fromPath = relativizePath(from.absolutePath);
			} else {
				fromPath = from.absolutePath;
			}
		}

		if (contains(toName)) throw new AlreadyExists("toDirectory: " + absolutePath + ", toName: " + toName);

		final SystemConsole systemConsole = new SystemConsole();

		systemConsole.writeLine("ln -s " + fromPath + " " + absolutePath + "/" + toName);

		final Text response = systemConsole.read();

		if (response.toString().trim().endsWith(": File exists")) throw new AlreadyExists("toDirectory: " + absolutePath + ", toName: " + toName);

		if (!response.isEmpty()) throw new RuntimeException(response.toString());

	}


	// Constructors:

	public Directory(@NotNull File file) throws NotExists {
		if (!file.exists()) throw new NotExists("Directory " + file.getAbsolutePath() + " doesn't exist");
		if (!file.isDirectory()) throw new InvalidValue.Runtime("File " + file.getAbsolutePath() + " is not a directory");
		this.file = file;
		{ String absolutePath = file.getAbsolutePath();
			if (absolutePath.endsWith("/")) absolutePath = absolutePath.substring(0, absolutePath.length() - "/".length());
			this.absolutePath = absolutePath;
		}
		this.name = file.getName();
	}

	public Directory(@NotNull String path) throws NotExists, InvalidValue.Runtime {
		this(new File(path));
	}


	// Static:

	public static Directory getExistingOrThrow(@NotNull String path) throws NotExists {
		return new Directory(path);
	}

	public static Directory getExisting(@NotNull String path) throws NotExists.Runtime {
		try {
			return getExistingOrThrow(path);
		} catch (NotExists notExists) { throw new NotExists.Runtime(notExists); }
	}

	public static Directory getExistingOrThrow(@NotNull Directory baseDir, @NotNull String path) throws NotExists {
		if (!path.startsWith("/")) {
			path = baseDir.absolutePath + "/" + path;
		}
		return getExistingOrThrow(path);
	}

	public static Directory getExisting(@NotNull Directory baseDir, @NotNull String path) throws NotExists.Runtime {
		try {
			return getExistingOrThrow(baseDir, path);
		} catch (NotExists notExists) { throw new NotExists.Runtime(notExists); }
	}

	public static Directory create(String path) throws AlreadyExists.Runtime {
		final File file = new File(path);
		if (file.exists()) throw new AlreadyExists.Runtime();
		if (!file.mkdirs()) throw new AccessDenied.Runtime("Can't create directory " + path);
		try {
			return new Directory(path);
		} catch (NotExists notExists) { throw new InconsistentData(notExists); }
	}

	public static Directory createIfNotExists(String path) {
		final File file = new File(path);
		if (!file.exists()) {
			if (!file.mkdirs()) throw new AccessDenied.Runtime("Can't create directory " + path);
		}
		try {
			return new Directory(file);
		} catch (NotExists notExists) { throw new InconsistentData(notExists); }
	}

	public static Directory createIfNotExists(@NotNull Directory baseDir, String path) {
		if (!path.startsWith("/")) {
			path = baseDir.absolutePath + "/" + path;
		}
		return createIfNotExists(path);
	}


	public static final Directory WORKDIR; static {
		try {
			WORKDIR = new Directory(System.getProperty("user.dir"));
		} catch (NotExists notExists) { throw new InconsistentData(notExists); }
	}


}
