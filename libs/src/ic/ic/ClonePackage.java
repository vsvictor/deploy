package ic;


import ic.apps.ic.UiCallback;
import ic.interfaces.action.Action1;
import ic.annotations.Degenerate;
import ic.annotations.Hide;
import org.jetbrains.annotations.NotNull;
import ic.git.Git;
import ic.storage.Directory;
import ic.throwables.*;

import static ic.AuthKt.getAuth;


@Degenerate class ClonePackage { @Hide private ClonePackage() {}


	static Directory clonePackage(

		@NotNull Distribution distribution,

		@NotNull String packageName,
		@NotNull Store store,

		@NotNull final UiCallback uiCallback

	) throws NotExists {

		try {
			return Git.clone(
				store.getUrl() + "/" + packageName,
				distribution.rootDirectory,
				distribution.getPackageSrcPath(store.name, packageName),
				getAuth(store.getUrl(), "read", uiCallback),
				new Action1<String>() { @Override public void run(String warning) {
					uiCallback.handleWarning(warning);
				} }
			);
		} catch (AlreadyExists alreadyExists) 	{ throw new InconsistentData(alreadyExists);
		} catch (AccessDenied accessDenied) 	{ throw new Fatal.Runtime(accessDenied); }

	}


}
