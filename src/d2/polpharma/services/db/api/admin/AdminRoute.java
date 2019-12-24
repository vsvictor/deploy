package d2.polpharma.services.db.api.admin;


import d2.modules.scenarios.admin.ScenariosAdminRoute;
import d2.modules.scenarios.model.Scenario;
import d2.modules.talk.admin.TalkAdminRoute;
import d2.polpharma.services.PolpharmaBackendKt;
import d2.polpharma.services.db.model.PharmUser;
import ic.interfaces.getter.Safe3Getter1;
import ic.network.http.*;
import ic.storage.BindingStorage;
import ic.storage.CacheStorage;
import ic.storage.StreamStorage;
import ic.stream.ByteSequence;
import ic.struct.collection.Collection;
import ic.struct.list.List;
import ic.struct.map.CountableMap;
import ic.text.Charset;
import ic.throwables.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import static d2.polpharma.services.PolpharmaBackendKt.*;
import static d2.polpharma.services.db.PolpharmaRedisDataBase.POLPHARMA_REDIS_DATABASE;
import static d2.polpharma.services.db.api.ophtalm.OphtikDbHostKt.getOphtikDbHost;
import static ic.ServiceAppKt.getTier;
import static ic.json.JsonArrays.jsonArrayToList;
import static ic.json.JsonArrays.toJsonArray;
import static ic.Assets.resources;


public class AdminRoute extends FolderHttpRoute.Static {

	@NotNull @Override protected String getName() { return "admin"; }

	@NotNull @Override protected List<HttpRoute> initChildren() { return new List.Default<>(

		new FolderHttpRoute.Static() {

			@Override protected String getName() { return "ophtalm"; }

			@Override protected List<HttpRoute> initChildren() { return new List.Default<>(

				new TalkAdminRoute() {
					@Override protected String getName() { return "talk"; }
					@Override protected String getPassword() { return "1e7082351284c8af"; }
					@Override protected void checkUserAuth(@NotNull BasicHttpAuth auth) throws NotExists, WrongValue { PolpharmaBackendKt.checkPolpharmaSuperadminAuth(auth); }
					@Override protected CacheStorage getStorage() {
						return POLPHARMA_REDIS_DATABASE.createFolderIfNotExists("ophtalm").createFolderIfNotExists("talk");
					}
					@Override protected Collection<String> getUserIds() {
						try {
							return jsonArrayToList(
								new JSONObject(Charset.DEFAULT_HTTP.bytesToString(HttpClient.request(new HttpRequest(
									getOphtikDbHost() + "/user/ids",
									"GET",
									new CountableMap.Default<>(
										"Authorization", "Bearer " + PolpharmaBackendKt.polpharmaServerKey
									),
									ByteSequence.EMPTY
								)).getBody())).getJSONArray("userIds"),
								(Safe3Getter1<String, String, JSONException, UnableToParse, Skip>) id -> id
							);
						} catch (Throwable e) { throw new RuntimeException(e); }
					}

					@Override protected String getUserName(String userId) {
						try {
							final JSONObject requestJson = new JSONObject();
							requestJson.put("id", userId);
							final JSONObject userJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(HttpClient.request(new HttpRequest(
								getOphtikDbHost() + "/user/get",
								"GET",
								new CountableMap.Default<>(
									"Authorization", "Bearer " + PolpharmaBackendKt.polpharmaServerKey
								),
								Charset.DEFAULT_HTTP.stringToByteSequence(requestJson.toString())
							)).getBody()));
							return userJson.optString("surname") + " " + userJson.optString("name");
						} catch (Throwable e) { throw new RuntimeException(e); }
					}

					@Override protected String getUserPhone(String userId) {
						try {
							final JSONObject requestJson = new JSONObject();
							requestJson.put("id", userId);
							final JSONObject userJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(HttpClient.request(new HttpRequest(
								getOphtikDbHost() + "/user/get",
								"GET",
								new CountableMap.Default<>(
									"Authorization", "Bearer " + PolpharmaBackendKt.polpharmaServerKey
								),
								Charset.DEFAULT_HTTP.stringToByteSequence(requestJson.toString())
							)).getBody()));
							return userJson.getString("phone");
						} catch (Throwable e) { throw new RuntimeException(e); }
					}
				},

				new ScenariosAdminRoute() {
					@Override protected String getName() 		{ return "survey"; 				}
					@Override protected String getPassword() 	{ return "1e7082351284c8af"; 	}

					@Override protected void checkUserAuth(BasicHttpAuth auth) throws NotExists, WrongValue, AccessDenied {
						checkPolpharmaSuperadminAuth(auth);
					}
					final BindingStorage storage = (
						POLPHARMA_REDIS_DATABASE.createFolderIfNotExists("ophtalm") .createFolderIfNotExists("survey")
					); @Override protected BindingStorage getStorage() { return storage; }
					@Override protected Collection<String> getUserIds() {
						try {
							return jsonArrayToList(
								new JSONObject(Charset.DEFAULT_HTTP.bytesToString(HttpClient.request(new HttpRequest(
									getOphtikDbHost() + "/user/ids",
									"GET",
									new CountableMap.Default<>(
										"Authorization", "Bearer " + PolpharmaBackendKt.polpharmaServerKey
									),
									ByteSequence.EMPTY
								)).getBody())).getJSONArray("userIds"),
								(Safe3Getter1<String, String, JSONException, UnableToParse, Skip>) id -> id
							);
						} catch (Throwable e) { throw new RuntimeException(e); }
					}
					@Override protected void notifyScenario(String subjectId, String scenarioId) {
						final Scenario scenario = Scenario.getScenariosMapper(storage, subjectId).getItem(scenarioId);
						final JSONObject requestJson = new JSONObject();
						requestJson.put("subjectId", subjectId);
						requestJson.put("scenarioId", scenarioId);
						requestJson.put("title", scenario.getTitle());
						requestJson.put("roles", toJsonArray(scenario.getRoles(), role -> role));
						if (getTier().equals("stage")) {
							try {
								HttpClient.request(new HttpRequest(
									"https://www.corezoid.com/api/1/json/public/538465/6e450f688ec59d3249e2007d8b8e2bb705b0c6c4", "POST",
									Charset.DEFAULT_HTTP.stringToByteSequence(requestJson.toString())
								));
							} catch (IOException e) 	{ throw new IOException.Runtime(e);
							} catch (HttpException e) 	{ throw new HttpException.Runtime(e); }
						} else
						if (getTier().equals("prod")) {
							try {
								HttpClient.request(new HttpRequest(
									"https://www.corezoid.com/api/1/json/public/540038/8d719a98e84a87a61f5c31935257745bb41ce479", "POST",
									Charset.DEFAULT_HTTP.stringToByteSequence(requestJson.toString())
								));
							} catch (IOException e) 	{ throw new IOException.Runtime(e);
							} catch (HttpException e) 	{ throw new HttpException.Runtime(e); }
						}
					}
					@Override protected String uploadImage(ByteSequence image, String name) {
						try {
							HttpClient.request(new HttpRequest(
								"https://images.bd-polpharma.com.ua/upload/api/upload-image?name=" + Charset.DEFAULT_HTTP.encodeUrl(name) + ".png", "POST",
								new CountableMap.Default<>(
									"Authorization", "Bearer 1e7082351284c8af"
								),
								image
							));
							return "https://images.bd-polpharma.com.ua/upload/" + Charset.DEFAULT_HTTP.encodeUrl(name) + ".png";
						} catch (IOException e) 	{ throw new IOException.Runtime(e);
						} catch (HttpException e) 	{ throw new HttpException.Runtime(e); }
					}
					@Override protected String getImagesPageUrl() { return "https://images.bd-polpharma.com.ua/upload/adminka"; }
				}

			); }

		},

		new FolderHttpRoute.Static() {

			@Override protected String getName() { return "pharm"; }

			@Override protected List<HttpRoute> initChildren() { return new List.Default<HttpRoute>(

				new UploadPharmaciesCsvApiMethod(),

				new ScenariosAdminRoute() {
					@Override protected String getName() { return "education"; }
					@Override protected String getPassword() { return "1e7082351284c8af"; }

					@Override protected void checkUserAuth(BasicHttpAuth auth) throws NotExists, WrongValue, AccessDenied {
						checkPolpharmaSuperadminAuth(auth);
					}
					final BindingStorage storage = (
						POLPHARMA_REDIS_DATABASE
						.createFolderIfNotExists("education")
						.createFolderIfNotExists("pharm")
					); @Override protected BindingStorage getStorage() { return storage; }
					@Override protected Collection<String> getUserIds() {
						return PharmUser.getPharmUserIds();
					}
					@Override protected void notifyScenario(String subjectId, String scenarioId) {}
					@Override protected String uploadImage(ByteSequence image, String name) {
						try {
							HttpClient.request(new HttpRequest(
								"https://images.bd-polpharma.com.ua/upload/api/upload-image?name=" + Charset.DEFAULT_HTTP.encodeUrl(name) + ".png", "POST",
								new CountableMap.Default<>(
									"Authorization", "Bearer 1e7082351284c8af"
								),
								image
							));
							return "https://images.bd-polpharma.com.ua/upload/" + Charset.DEFAULT_HTTP.encodeUrl(name) + ".png";
						} catch (IOException e) 	{ throw new IOException.Runtime(e);
						} catch (HttpException e) 	{ throw new HttpException.Runtime(e); }
					}
					@Override protected String getImagesPageUrl() { return "https://images.bd-polpharma.com.ua/upload/adminka"; }
				},

				new ScenariosAdminRoute() {
					@Override protected String getName() 		{ return "survey"; 				}
					@Override protected String getPassword() 	{ return "1e7082351284c8af"; 	}
					@Override protected void checkUserAuth(BasicHttpAuth auth) throws NotExists, WrongValue, AccessDenied {
						checkPolpharmaSuperadminAuth(auth);
					}
					final BindingStorage storage = (
						POLPHARMA_REDIS_DATABASE
						.createFolderIfNotExists("pharm")
						.createFolderIfNotExists("survey")
					); @Override protected BindingStorage getStorage() { return storage; }
					@Override protected Collection<String> getUserIds() {
						return PharmUser.getPharmUserIds();
					}
					@Override protected void notifyScenario(String subjectId, String scenarioId) {}
					@Override protected String uploadImage(ByteSequence image, String name) {
						try {
							HttpClient.request(new HttpRequest(
								"https://images.bd-polpharma.com.ua/upload/api/upload-image?name=" + Charset.DEFAULT_HTTP.encodeUrl(name) + ".png", "POST",
								new CountableMap.Default<String, String>(
									"Authorization", "Bearer 1e7082351284c8af"
								),
								image
							));
							return "https://images.bd-polpharma.com.ua/upload/" + Charset.DEFAULT_HTTP.encodeUrl(name) + ".png";
						} catch (IOException e) 	{ throw new IOException.Runtime(e);
						} catch (HttpException e) 	{ throw new HttpException.Runtime(e); }
					}
					@Override protected String getImagesPageUrl() { return "https://images.bd-polpharma.com.ua/upload/adminka"; }
				}

			); }

		},

		new StorageHttpRoute() {
			@Override protected StreamStorage getStorage(String head) {
				final StreamStorage htmlFolder = resources.get("html");
				return htmlFolder.get("admin");
			}
		}

	); }

}
