package ic.cmd;


import ic.annotations.Degenerate;
import ic.annotations.Hide;
import org.jetbrains.annotations.NotNull;
import ic.struct.list.ConvertList;
import ic.struct.list.List;

import static ic.cmd.TableFormatter.writeTable;


@Degenerate class WriteCommandList { @Hide private WriteCommandList() {}


	static void writeCommandList(final @NotNull Console console, final @NotNull List<Command> commands) {

		console.writeLine("Available commands:");

		writeTable(
			console,
			new ConvertList<>(
				commands,
				command -> new List.Default<>(
					command.getSyntax(),
					"-",
					command.getDescription()
				)
			),
			1
		);

	}


}
