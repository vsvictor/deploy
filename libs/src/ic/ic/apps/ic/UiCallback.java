package ic.apps.ic;


import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
import ic.text.Text;


public abstract class UiCallback {

	public abstract void onPackageAddStarted(@NotNull String packageName);
	public abstract void onPackageUpdateStarted(@NotNull String packageName);
	public abstract void onPackageBuildStarted(@NotNull String packageName);
	public abstract void onPackageUploadStarted(@NotNull String packageName);
	public abstract void onPackageTestStarted(@NotNull String packageName);
	public abstract void onPackageTestResult(@NotNull String packageName, @NotNull Text testResult);

	public abstract void onExportCloneStarted(@NotNull String url);

	public abstract void handleWarning(@NotNull String warning);

	public abstract Pair<String, String> askAuth(@NotNull String destination, @NotNull Pair<String, String> existingAuth);

	public abstract String askCommitMessage();

}
