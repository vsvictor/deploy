package ic.cmd;


import ic.annotations.Overload;
import ic.interfaces.action.Action1;
import ic.interfaces.action.Action2;
import ic.annotations.Same;
import ic.text.Text;
import ic.throwables.Fatal;
import ic.throwables.InvalidSyntax;
import org.jetbrains.annotations.NotNull;

import static ic.TextResources.getResString;


public interface Command extends Action2<Console, Text>, Action1<Console> {


	/**
	 * For example "commit"
	 */
	@Same String getName();

	/**
	 * For example "commit -m \"<message>\""
	 */
	@Same String getSyntax();

	@Same String getDescription();


	abstract class BaseCommand implements Command {

		protected abstract void implementRun(Console console, Text args) throws InvalidSyntax, Fatal;

		@Override public final void run(@NotNull Console console, @NotNull Text args) {
			try {
				implementRun(console, args);
			} catch (InvalidSyntax invalidSyntax) {
				console.writeLine(
					getResString("cmd/invalid-syntax")
					.replace("$(actual)", 	getName() + " " + args.toString())
					.replace("$(correct)", 	getSyntax())
				);
			} catch (Fatal | Fatal.Runtime fatal) {
				console.writeLine(
					fatal.getMessage()
				);
			}
		}

		@Override @Overload public final void run(Console console) {
			assert console != null;
			run(console, Text.EMPTY);
		}

	}


}
