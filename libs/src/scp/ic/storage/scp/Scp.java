package ic.storage.scp;


import ic.annotations.Degenerate;
import ic.annotations.Hide;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ic.cmd.SystemConsole;
import ic.storage.Directory;


public @Degenerate class Scp { @Hide private Scp() {}


	public static void download(

		@NotNull String remotePath,
		@NotNull String username, @Nullable String password,
		@NotNull Directory baseDir, @NotNull String path

	) {

		final SystemConsole systemConsole = new SystemConsole();

		systemConsole.writeLine("cd " + baseDir.absolutePath);

		if (password != null) systemConsole.write("sshpass -p '" + password + "' ");

		systemConsole.writeLine("scp -o StrictHostKeyChecking=no -r " + username + "@" + remotePath + " " + path);

	}


}
