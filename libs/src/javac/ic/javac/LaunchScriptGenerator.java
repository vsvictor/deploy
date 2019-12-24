package ic.javac;


import ic.annotations.Default;
import org.jetbrains.annotations.NotNull;
import ic.cmd.SystemConsole;
import ic.storage.Directory;
import ic.struct.collection.Collection;
import ic.struct.collection.ConvertCollection;
import ic.struct.list.List;
import ic.struct.map.CountableMap;
import ic.text.Charset;
import ic.text.JoinStrings;
import ic.text.Text;
import ic.text.TextBuffer;


public class LaunchScriptGenerator {


	@Default protected String directoryToPath(Directory directory) {
		return directory.absolutePath;
	}


	public void generateLaunchScript(

		final @NotNull Directory directory,
		final @NotNull String scriptName,
		final @NotNull Collection<Directory> classDirs,
		final @NotNull Collection<Directory> jarDirs,
		final @NotNull String mainClassName,
		final @NotNull Text args,
		final @NotNull CountableMap<String, String> envs

	) {

		final SystemConsole systemConsole = new SystemConsole();

		final TextBuffer launchScript = new TextBuffer();

		launchScript.writeLine("#!/bin/bash");

		envs.getKeys().forEach(envKey -> {
			launchScript.write(envKey);
			launchScript.put('=');
			launchScript.write(envs.getNotNull(envKey));
			launchScript.put(' ');
		});

		launchScript.writeLine("java " +
			("-classpath " +
				new JoinStrings(
					new List.Default<>(
						new ConvertCollection<>(
							classDirs,
							this::directoryToPath
						)
					),
					':'
				).getStringValue() +
				":" +
				new JoinStrings(
					new List.Default<>(
						new ConvertCollection<>(
							jarDirs,
							jarDir -> "'" + directoryToPath(jarDir) + "/*'"
						)
					),
					':'
				).getStringValue()
			) + " " +
			"-ea" + " " +
			mainClassName + " " +
			(args.isEmpty() ? "" : args.getStringValue() + " ") +
			"$@"
		);

		directory.write(scriptName, Charset.DEFAULT_UNIX.textToByteSequence(launchScript));

		systemConsole.writeLine("chmod +x " + directory.absolutePath + "/" + scriptName);

	}


}
