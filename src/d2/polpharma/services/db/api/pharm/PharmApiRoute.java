package d2.polpharma.services.db.api.pharm;


import d2.modules.scenarios.api.ScenariosApiRoute;
import d2.polpharma.services.PolpharmaBackendKt;
import d2.polpharma.services.db.model.PharmUser;
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

import static d2.polpharma.services.db.PolpharmaRedisDataBase.POLPHARMA_REDIS_DATABASE;
import static d2.polpharma.services.db.model.PharmUser.getPharmUserById;


public class PharmApiRoute extends FolderHttpRoute.Static {

	@Override protected String getName() { return "pharm"; }

	@Override protected List<HttpRoute> initChildren() { return new List.Default<HttpRoute>(

		new PharmUserApiMethod(),

		new CreatePharmUserApiMethod(),
		new GetPharmUserApiMethod(),
		new ModifyPharmUserApiMethod(),

		new SearchPharmUsersApiMethod(),

		new GetPharmaciesByLocationApiMethod(),

		new GetRegionsApiMethod(),
		new GetCitiesApiMethod(),
		new GetPharmaciesByAddressApiMethod(),

		new ScenariosApiRoute() {
			@Override protected String getName() 	{ return "education"; 			}
			@Override protected String getBearer() 	{ return "06bc983a9307c501"; 	}
			@Override protected Collection<String> getUserIds() { return PharmUser.getPharmUserIds(); }
			final BindingStorage storage = (
				POLPHARMA_REDIS_DATABASE
				.createFolderIfNotExists("education")
				.createFolderIfNotExists("pharm")
			); @Override protected BindingStorage getStorage() { return storage; }
			@Override protected String getUserRoleById(String id) throws NotExists { return getPharmUserById(id).getRole(); }
			@Override protected void notifyScenario(String subjectId, String scenarioId) {}
			@Override protected void onScenarioFinish(String userId, String subjectId, String scenarioId, boolean firstTime, int currentTimeScore) {}
			@Override protected void checkServerKey(@NotNull String serverKey) throws WrongValue, AccessDenied { PolpharmaBackendKt.checkPolpharmaServerKey(serverKey); }
			@Override protected void checkUserAuth(BasicHttpAuth auth) throws NotExists, WrongValue { PolpharmaBackendKt.checkPolpharmaSuperadminAuth(auth); }
		},

		new ScenariosApiRoute() {
			@Override protected String getName() 	{ return "survey"; 				}
			@Override protected String getBearer() 	{ return "06bc983a9307c501"; 	}
			@Override protected Collection<String> getUserIds() { return PharmUser.getPharmUserIds(); }
			final BindingStorage storage = (
				POLPHARMA_REDIS_DATABASE
				.createFolderIfNotExists("pharm")
				.createFolderIfNotExists("survey")
			); @Override protected BindingStorage getStorage() { return storage; }
			@Override protected String getUserRoleById(String id) throws NotExists { return getPharmUserById(id).getRole(); }
			@Override protected void notifyScenario(String subjectId, String scenarioId) {}
			@Override protected void checkServerKey(@NotNull String serverKey) throws WrongValue, AccessDenied { PolpharmaBackendKt.checkPolpharmaServerKey(serverKey); }
			@Override protected void onScenarioFinish(String userId, String subjectId, String scenarioId, boolean firstTime, int currentTimeScore) {}
			@Override protected void checkUserAuth(BasicHttpAuth auth) throws NotExists, WrongValue { PolpharmaBackendKt.checkPolpharmaSuperadminAuth(auth); }
		}

	); }

}
