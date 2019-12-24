package ic;


import ic.apps.ic.UiCallback;
import ic.annotations.Degenerate;
import kotlin.Unit;
import org.jetbrains.annotations.NotNull;
import ic.git.Git;
import ic.git.Rejected;
import ic.struct.set.CountableSet;
import ic.storage.Directory;
import ic.throwables.*;

import static ic.AuthKt.getAuth;


@Degenerate public class Upload {


	public static void upload(

		@NotNull final Distribution distribution,

		@NotNull final CountableSet<String> packagesToUpload,

		@NotNull final UiCallback uiCallback

	) throws Fatal {

		final String commitMessage = uiCallback.askCommitMessage();

		packagesToUpload.forEach(packageName -> {

			uiCallback.onPackageUploadStarted(packageName);

			final Directory srcPackageDir = distribution.getPackageSrcDirectory(packageName);

			Git.addAll(srcPackageDir);

			try {
				Git.commit(srcPackageDir, commitMessage);
			} catch (NotNeeded notNeeded) {}

			final Store store; try {
				store = Store.load(distribution, packageName);
			} catch (NotExists notExists) { throw new InconsistentData(notExists); }

			try {
				Git.push(
					srcPackageDir,
					store.getUrl() + "/" + packageName,
					getAuth(store.getUrl(), "write", uiCallback),
					warning -> { uiCallback.handleWarning(warning); return Unit.INSTANCE; }
				);
			} catch (NotNeeded notNeeded) {
			} catch (Rejected rejected) {
				throw new Fatal.Runtime(rejected.getMessage());
			}

		});

	}


}
