package ic.system.onboot;


import ic.annotations.Degenerate;
import ic.struct.collection.Collection;
import ic.text.ReplaceText;
import ic.text.Text;
import kotlin.Pair;

import static ic.TextResources.getResText;
import static ic.system.bash.BashKt.scriptToCommand;
import static ic.system.bash.BashKt.singleQuote;


public @Degenerate class OnBoot {


	public static String getInstallCommand() {

		final Text install = getResText("onboot/install");
		final Text rcLocal = getResText("onboot/rc-local");

		return scriptToCommand(
			new ReplaceText(
				install,
				new Collection.Default<>(
					new Pair<>("$(rcLocal)", singleQuote(rcLocal).toString())
				)
			)
		);

	}


}
