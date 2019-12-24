package d2.modules.scenarios.admin;


import d2.modules.scenarios.api.HistoryStatisticsApiEndpoint;
import ic.network.http.BasicHttpAuth;
import ic.network.http.FolderHttpRoute;
import ic.network.http.HttpRoute;
import ic.network.http.StorageHttpRoute;
import ic.storage.BindingStorage;
import ic.storage.CacheStorage;
import ic.storage.StreamStorage;
import ic.stream.ByteSequence;
import ic.struct.collection.Collection;
import ic.struct.list.List;
import ic.throwables.AccessDenied;
import ic.throwables.NotExists;
import ic.throwables.WrongValue;
import static ic.Assets.resources;

import static ic.throwables.AccessDenied.ACCESS_DENIED;


public @Deprecated abstract class ScenariosAdminRoute extends FolderHttpRoute.Static {

	@Override protected abstract String getName();

	protected abstract String getPassword();

	protected abstract BindingStorage getStorage();

	protected abstract Collection<String> getUserIds();

	protected abstract void notifyScenario(String subjectId, String scenarioId);

	protected abstract String uploadImage(ByteSequence image, String name);

	protected abstract String getImagesPageUrl();

	protected abstract void checkUserAuth(BasicHttpAuth auth) throws NotExists, WrongValue, AccessDenied;

	@Override protected List<HttpRoute> initChildren() { return new List.Default<>(

		new FolderHttpRoute.Static() {
			@Override protected String getName() { return "api"; }
			@Override protected List<HttpRoute> initChildren() { return new List.Default<>(

				new UploadScenarioApiMethod() {
					@Override protected String getBearer() 			{ return ScenariosAdminRoute.this.getPassword(); 	}
					@Override protected BindingStorage getStorage() { return ScenariosAdminRoute.this.getStorage(); 	}
				},
				new AddSubjectApiMethod() {
					@Override protected String getBearer() 			{ return ScenariosAdminRoute.this.getPassword(); 	}
					@Override protected BindingStorage getStorage() { return ScenariosAdminRoute.this.getStorage(); 	}
				},
				new SetSubjectRolesApiMethod() {
					@Override protected String getBearer() 			{ return ScenariosAdminRoute.this.getPassword(); 	}
					@Override protected BindingStorage getStorage() { return ScenariosAdminRoute.this.getStorage(); 	}
				},
				new SetSubjectTitleApiMethod() {
					@Override protected String getBearer() 			{ return ScenariosAdminRoute.this.getPassword(); 	}
					@Override protected BindingStorage getStorage() { return ScenariosAdminRoute.this.getStorage(); 	}
				},
				new GetSubjectsApiMethod() {
					@Override protected String getBearer() 			{ return ScenariosAdminRoute.this.getPassword(); 	}
					@Override protected BindingStorage getStorage() { return ScenariosAdminRoute.this.getStorage(); 	}
				},
				new ClearSubjectApiMethod() {
					@Override protected String getBearer() 			{ return ScenariosAdminRoute.this.getPassword(); 	}
					@Override protected BindingStorage getStorage() { return ScenariosAdminRoute.this.getStorage(); 	}
				},
				new DeleteSubjectApiMethod() {
					@Override protected String getBearer() 			{ return ScenariosAdminRoute.this.getPassword(); 	}
					@Override protected BindingStorage getStorage() { return ScenariosAdminRoute.this.getStorage(); 	}
				},
				new GetSubjectApiMethod() {
					@Override protected String getBearer() 			{ return ScenariosAdminRoute.this.getPassword(); 	}
					@Override protected BindingStorage getStorage() { return ScenariosAdminRoute.this.getStorage(); 	}
				},
				new GetScenarioApiMethod() {
					@Override protected String getBearer() 			{ return ScenariosAdminRoute.this.getPassword(); 	}
					@Override protected BindingStorage getStorage() { return ScenariosAdminRoute.this.getStorage(); 	}
				},
				new SetScenarioTitleApiMethod() {
					@Override protected String getBearer() 			{ return ScenariosAdminRoute.this.getPassword(); 	}
					@Override protected BindingStorage getStorage() { return ScenariosAdminRoute.this.getStorage(); 	}
				},
				new SetScenarioRolesApiMethod() {
					@Override protected String getBearer() 			{ return ScenariosAdminRoute.this.getPassword(); 	}
					@Override protected BindingStorage getStorage() { return ScenariosAdminRoute.this.getStorage(); 	}
				},
				new DeleteScenarioApiMethod() {
					@Override protected String getBearer() 			{ return ScenariosAdminRoute.this.getPassword(); 	}
					@Override protected BindingStorage getStorage() { return ScenariosAdminRoute.this.getStorage(); 	}
				},
				new AddScenarioApiMethod() {
					@Override protected String getBearer() 			{ return ScenariosAdminRoute.this.getPassword(); 	}
					@Override protected BindingStorage getStorage() { return ScenariosAdminRoute.this.getStorage(); 	}
				},
				new NotifyScenarioApiMethod() {
					@Override protected String getBearer() 											{ return ScenariosAdminRoute.this.getPassword(); 					}
					@Override protected CacheStorage getStorage() 									{ return ScenariosAdminRoute.this.getStorage(); 					}
					@Override protected void notifyScenario(String subjectId, String scenarioId) 	{ ScenariosAdminRoute.this.notifyScenario(subjectId, scenarioId); 	}
				},

				new UploadImageApiMethod() {
					@Override protected String getBearer() 									{ return ScenariosAdminRoute.this.getPassword(); 			}
					@Override protected String uploadImage(ByteSequence image, String name) { return ScenariosAdminRoute.this.uploadImage(image, name); }
				},
				new GetImagesPageUrlApiMethod() {
					@Override protected String getBearer() 			{ return ScenariosAdminRoute.this.getPassword(); 	}
					@Override protected String getImagesPageUrl() { return ScenariosAdminRoute.this.getImagesPageUrl(); }
				},

				new StatisticsCsvApiMethod() {
					@Override protected BindingStorage getStorage() 	{ return ScenariosAdminRoute.this.getStorage(); }
					@Override protected Collection<String> getUserIds() { return ScenariosAdminRoute.this.getUserIds(); }
				},
				new HistoryStatisticsApiEndpoint() {
					@Override protected void checkServerKey(String serverKey) throws AccessDenied { throw ACCESS_DENIED; }
					@Override protected void checkUserAuth(BasicHttpAuth auth) throws NotExists, WrongValue, AccessDenied { ScenariosAdminRoute.this.checkUserAuth(auth); }
					@Override protected BindingStorage getStorage() 	{ return ScenariosAdminRoute.this.getStorage(); 	}
					@Override protected Collection<String> getUserIds() { return ScenariosAdminRoute.this.getUserIds(); 	}
				}

			); }
		},

		new StorageHttpRoute() {
			private final StreamStorage storage = (
				resources.getFolder("html").getFolder("education")
			); @Override protected StreamStorage getStorage(String head) { return storage; }
		},

		new StorageHttpRoute() {
			@Override protected StreamStorage getStorage(String head) { return resources; }
		}


	); }

}
