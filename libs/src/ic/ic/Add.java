package ic;


import ic.apps.ic.UiCallback;
import ic.annotations.Degenerate;
import ic.annotations.Hide;
import org.jetbrains.annotations.NotNull;
import ic.struct.set.CountableSet;
import ic.throwables.AlreadyExists;
import ic.throwables.NotExists;
import ic.throwables.Fatal;

import static ic.AddPackage.addPackage;
import static ic.Update.update;


@Degenerate public class Add { @Hide private Add() {}


	public static void add(

		@NotNull final Distribution distribution,

		@NotNull final String packageName,

		@NotNull final UiCallback uiCallback

	) throws AlreadyExists, NotExists, Fatal {

		addPackage(distribution, packageName, uiCallback);

		update(
			distribution,
			new CountableSet.Default<>(packageName),
			new CountableSet.Default<>(packageName),
			uiCallback
		);

	}


}
