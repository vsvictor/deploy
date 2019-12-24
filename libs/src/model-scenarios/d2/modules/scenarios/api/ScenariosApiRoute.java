package d2.modules.scenarios.api;


import d2.modules.scenarios.api.statistics.StatisticsApiRoute;
import ic.network.http.BasicHttpAuth;
import ic.network.http.HttpRoute;
import ic.network.http.FolderHttpRoute;
import ic.storage.BindingStorage;
import ic.struct.collection.Collection;
import ic.struct.list.List;
import ic.throwables.AccessDenied;
import ic.throwables.NotExists;
import ic.throwables.WrongValue;
import org.jetbrains.annotations.NotNull;

import static ic.throwables.AccessDenied.ACCESS_DENIED;


public abstract class ScenariosApiRoute extends FolderHttpRoute.Static {


	protected abstract BindingStorage getStorage();

	protected abstract String getBearer();

	protected abstract Collection<String> getUserIds();

	protected abstract String getUserRoleById(String id) throws NotExists;

	protected abstract void notifyScenario(String subjectId, String scenarioId);

	protected abstract void onScenarioFinish(String userId, String subjectId, String scenarioId, boolean firstTime, int currentTimeScore);

	protected abstract void checkServerKey(@NotNull String serverKey) throws WrongValue, AccessDenied;
	protected abstract void checkUserAuth(BasicHttpAuth auth) throws NotExists, WrongValue, AccessDenied;


	@Override protected @NotNull List<HttpRoute> initChildren() { return new List.Default<>(

		new AddScenarioApiMethod() {
			@Override protected void checkServerKey(@NotNull String serverKey) throws WrongValue, AccessDenied { ScenariosApiRoute.this.checkServerKey(serverKey); }
			@Override protected void checkUserAuth(@NotNull BasicHttpAuth auth) throws NotExists, WrongValue, AccessDenied { ScenariosApiRoute.this.checkUserAuth(auth); }
			@Override protected BindingStorage getStorage() { return ScenariosApiRoute.this.getStorage(); }
		},

		new AddSubjectApiMethod() {
			@Override protected void checkServerKey(@NotNull String serverKey) throws WrongValue, AccessDenied { ScenariosApiRoute.this.checkServerKey(serverKey); }
			@Override protected void checkUserAuth(@NotNull BasicHttpAuth auth) throws NotExists, WrongValue, AccessDenied { ScenariosApiRoute.this.checkUserAuth(auth); }
			@Override protected BindingStorage getStorage() { return ScenariosApiRoute.this.getStorage(); }
		},

		new ClearSubjectApiMethod() {
			@Override protected void checkServerKey(@NotNull String serverKey) throws WrongValue, AccessDenied { ScenariosApiRoute.this.checkServerKey(serverKey); }
			@Override protected void checkUserAuth(@NotNull BasicHttpAuth auth) throws NotExists, WrongValue, AccessDenied { ScenariosApiRoute.this.checkUserAuth(auth); }
			@Override protected BindingStorage getStorage() { return ScenariosApiRoute.this.getStorage(); }
		},

		new DeleteScenarioApiMethod() {
			@Override protected void checkServerKey(@NotNull String serverKey) throws WrongValue, AccessDenied { ScenariosApiRoute.this.checkServerKey(serverKey); }
			@Override protected void checkUserAuth(@NotNull BasicHttpAuth auth) throws NotExists, WrongValue, AccessDenied { ScenariosApiRoute.this.checkUserAuth(auth); }
			@Override protected BindingStorage getStorage() { return ScenariosApiRoute.this.getStorage(); }
		},

		new DeleteSubjectApiMethod() {
			@Override protected void checkServerKey(@NotNull String serverKey) throws WrongValue, AccessDenied { ScenariosApiRoute.this.checkServerKey(serverKey); }
			@Override protected void checkUserAuth(@NotNull BasicHttpAuth auth) throws NotExists, WrongValue, AccessDenied { ScenariosApiRoute.this.checkUserAuth(auth); }
			@Override protected BindingStorage getStorage() { return ScenariosApiRoute.this.getStorage(); }
		},

		new GetScenarioApiMethod() {
			@Override protected void checkServerKey(@NotNull String serverKey) throws WrongValue, AccessDenied { ScenariosApiRoute.this.checkServerKey(serverKey); }
			@Override protected void checkUserAuth(@NotNull BasicHttpAuth auth) throws NotExists, WrongValue, AccessDenied { ScenariosApiRoute.this.checkUserAuth(auth); }
			@Override protected BindingStorage getStorage() { return ScenariosApiRoute.this.getStorage(); }
		},

		new GetSubjectsApiMethod() {
			@Override protected void checkServerKey(@NotNull String serverKey) throws WrongValue, AccessDenied { ScenariosApiRoute.this.checkServerKey(serverKey); }
			@Override protected void checkUserAuth(@NotNull BasicHttpAuth auth) throws NotExists, WrongValue, AccessDenied { ScenariosApiRoute.this.checkUserAuth(auth); }
			@Override protected BindingStorage getStorage() { return ScenariosApiRoute.this.getStorage(); }
			@Override protected String getUserRoleById(String id) throws NotExists { return ScenariosApiRoute.this.getUserRoleById(id); }
		},

		new GetSubjectApiMethod() {
			@Override protected void checkServerKey(@NotNull String serverKey) throws WrongValue, AccessDenied { ScenariosApiRoute.this.checkServerKey(serverKey); }
			@Override protected void checkUserAuth(@NotNull BasicHttpAuth auth) throws NotExists, WrongValue, AccessDenied { ScenariosApiRoute.this.checkUserAuth(auth); }
			@Override protected BindingStorage getStorage() { return ScenariosApiRoute.this.getStorage(); }
		},

		new GetScenariosApiEndpoint() {
			@Override protected void checkServerKey(@NotNull String serverKey) throws WrongValue, AccessDenied { ScenariosApiRoute.this.checkServerKey(serverKey); }
			@Override protected void checkUserAuth(@NotNull BasicHttpAuth auth) throws NotExists, WrongValue, AccessDenied { ScenariosApiRoute.this.checkUserAuth(auth); }
			@Override protected BindingStorage getStorage() { return ScenariosApiRoute.this.getStorage(); }
			@Override protected String getUserRoleById(String id) throws NotExists { return ScenariosApiRoute.this.getUserRoleById(id); }
		},

		new GetBlockApiMethod() {
			@Override protected String getBearer() { return ScenariosApiRoute.this.getBearer(); }
			@Override protected BindingStorage getStorage() { return ScenariosApiRoute.this.getStorage(); }
			@Override protected void onScenarioFinish(String userId, String subjectId, String scenarioId, boolean firstTime, int currentTimeScore) {
				ScenariosApiRoute.this.onScenarioFinish(userId, subjectId, scenarioId, firstTime, currentTimeScore);
			}
			@Override protected String getUserRoleById(String id) throws NotExists { return ScenariosApiRoute.this.getUserRoleById(id); }
		},

		new HistoryStatisticsApiEndpoint() {
			@Override protected void checkServerKey(@NotNull String serverKey) throws AccessDenied { throw ACCESS_DENIED; }
			@Override protected void checkUserAuth(@NotNull BasicHttpAuth auth) throws NotExists, WrongValue, AccessDenied { ScenariosApiRoute.this.checkUserAuth(auth); }
			@Override protected Collection<String> getUserIds() { return ScenariosApiRoute.this.getUserIds(); }
			@Override protected BindingStorage getStorage() { return ScenariosApiRoute.this.getStorage(); }
		},

		new CumulativeStatisticsApiMethod() {
			@Override protected void checkServerKey(@NotNull String serverKey) throws AccessDenied { throw ACCESS_DENIED; }
			@Override protected void checkUserAuth(@NotNull BasicHttpAuth auth) throws NotExists, WrongValue, AccessDenied { ScenariosApiRoute.this.checkUserAuth(auth); }
			@Override protected Collection<String> getUserIds() { return ScenariosApiRoute.this.getUserIds(); }
			@Override protected BindingStorage getStorage() { return ScenariosApiRoute.this.getStorage(); }
		},

		new GetStatisticsApiMethod() {
			@Override protected String getBearer() { return ScenariosApiRoute.this.getBearer(); }
			@Override protected BindingStorage getStorage() { return ScenariosApiRoute.this.getStorage(); }
		},

		new NotifyScenarioApiMethod() {
			@Override protected void checkServerKey(@NotNull String serverKey) throws WrongValue, AccessDenied { ScenariosApiRoute.this.checkServerKey(serverKey); }
			@Override protected void checkUserAuth(@NotNull BasicHttpAuth auth) throws NotExists, WrongValue, AccessDenied { ScenariosApiRoute.this.checkUserAuth(auth); }
			@Override protected BindingStorage getStorage() { return ScenariosApiRoute.this.getStorage(); }
			@Override protected void notifyScenario(String subjectId, String scenarioId) { ScenariosApiRoute.this.notifyScenario(subjectId, scenarioId); }
		},

		new ScenarioStatisticsApiMethod() {
			@Override protected void checkServerKey(@NotNull String serverKey) throws AccessDenied { throw ACCESS_DENIED; }
			@Override protected void checkUserAuth(@NotNull BasicHttpAuth auth) throws NotExists, WrongValue, AccessDenied { ScenariosApiRoute.this.checkUserAuth(auth); }
			@Override protected Collection<String> getUserIds() { return ScenariosApiRoute.this.getUserIds(); }
			@Override protected BindingStorage getStorage() { return ScenariosApiRoute.this.getStorage(); }
		},

		new SetScenarioRolesApiMethod() {
			@Override protected void checkServerKey(@NotNull String serverKey) throws AccessDenied { throw ACCESS_DENIED; }
			@Override protected void checkUserAuth(@NotNull BasicHttpAuth auth) throws NotExists, WrongValue, AccessDenied { ScenariosApiRoute.this.checkUserAuth(auth); }
			@Override protected BindingStorage getStorage() { return ScenariosApiRoute.this.getStorage(); }
		},

		new SetScenarioTitleApiMethod() {
			@Override protected void checkServerKey(@NotNull String serverKey) throws AccessDenied { throw ACCESS_DENIED; }
			@Override protected void checkUserAuth(@NotNull BasicHttpAuth auth) throws NotExists, WrongValue, AccessDenied { ScenariosApiRoute.this.checkUserAuth(auth); }
			@Override protected BindingStorage getStorage() { return ScenariosApiRoute.this.getStorage(); }
		},

		new SetSubjectRolesApiMethod() {
			@Override protected void checkServerKey(@NotNull String serverKey) throws AccessDenied { throw ACCESS_DENIED; }
			@Override protected void checkUserAuth(@NotNull BasicHttpAuth auth) throws NotExists, WrongValue, AccessDenied { ScenariosApiRoute.this.checkUserAuth(auth); }
			@Override protected BindingStorage getStorage() { return ScenariosApiRoute.this.getStorage(); }
		},

		new SetSubjectTitleApiMethod() {
			@Override protected void checkServerKey(@NotNull String serverKey) throws AccessDenied { throw ACCESS_DENIED; }
			@Override protected void checkUserAuth(@NotNull BasicHttpAuth auth) throws NotExists, WrongValue, AccessDenied { ScenariosApiRoute.this.checkUserAuth(auth); }
			@Override protected BindingStorage getStorage() { return ScenariosApiRoute.this.getStorage(); }
		},

		new UploadScenarioApiMethod() {
			@Override protected void checkServerKey(@NotNull String serverKey) throws AccessDenied { throw ACCESS_DENIED; }
			@Override protected void checkUserAuth(@NotNull BasicHttpAuth auth) throws NotExists, WrongValue, AccessDenied { ScenariosApiRoute.this.checkUserAuth(auth); }
			@Override protected BindingStorage getStorage() { return ScenariosApiRoute.this.getStorage(); }
		},

		new StatisticsApiRoute() {
			@Override protected void checkServerKey(@NotNull String serverKey) throws AccessDenied { throw ACCESS_DENIED; }
			@Override protected void checkUserAuth(@NotNull BasicHttpAuth auth) throws NotExists, WrongValue, AccessDenied { ScenariosApiRoute.this.checkUserAuth(auth); }
			@Override protected Collection<String> getUserIds() { return ScenariosApiRoute.this.getUserIds(); }
			@Override protected BindingStorage getStorage() { return ScenariosApiRoute.this.getStorage(); }
		}

	); }


}
