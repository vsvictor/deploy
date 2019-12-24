package d2.polpharma.services.db.api.ophtalm;


import d2.modules.scenarios.api.ScenariosApiRoute;
import d2.modules.scenarios.model.Scenario;
import d2.modules.talk.api.TalkApiRoute;
import d2.polpharma.services.PolpharmaBackendKt;
import ic.interfaces.getter.Safe3Getter1;
import ic.network.http.*;
import ic.storage.BindingStorage;
import ic.storage.CacheStorage;
import ic.stream.ByteSequence;
import ic.struct.collection.Collection;
import ic.struct.list.List;
import ic.struct.map.CountableMap;
import ic.text.Charset;
import ic.throwables.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import static d2.polpharma.services.db.PolpharmaRedisDataBase.POLPHARMA_REDIS_DATABASE;
import static d2.polpharma.services.db.api.ophtalm.OphtikDbHostKt.getOphtikDbHost;
import static ic.ServiceAppKt.getTier;
import static ic.json.JsonArrays.jsonArrayToList;
import static ic.json.JsonArrays.toJsonArray;


public class OphtalmApiRoute extends FolderHttpRoute.Static {

	@Override protected String getName() { return "ophtalm"; }

	//private static final Directory HTML_DIR = Directory.createIfNotExists("/public");

	@Override protected List<HttpRoute> initChildren() { return new List.Default<>(

		new TalkApiRoute() {
			@Override protected String getName() { return "talk"; }
			@Override protected CacheStorage getStorage() {
				return POLPHARMA_REDIS_DATABASE.createFolderIfNotExists("ophtalm").createFolderIfNotExists("talk");
			}
			@Override protected int getUsersCount() {
				try {
					return new JSONObject(Charset.DEFAULT_HTTP.bytesToString(HttpClient.request(new HttpRequest(
						getOphtikDbHost() + "/user/count",
						"GET",
						new CountableMap.Default<>(
							"Authorization", "Bearer " + PolpharmaBackendKt.polpharmaServerKey
						),
						ByteSequence.EMPTY
					)).getBody())).getInt("usersCount");
				} catch (Throwable e) { throw new RuntimeException(e); }
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
			@Override protected void onSetSelectedTalks(String userId, String talkEventId) {
				assert userId 		!= null;
				assert talkEventId 	!= null;
				final JSONObject requestJson = new JSONObject();
				requestJson.put("userId", 		userId);
				requestJson.put("talkEventId", 	talkEventId);
				try {
					HttpClient.request(
						new HttpRequest(
							"https://www.corezoid.com/api/1/json/public/513034/3a9d0d1581ece61dc827ce718e0b6cc82e992e85",
							"POST",
							new CountableMap.Default<>(),
							Charset.DEFAULT_HTTP.stringToByteSequence(requestJson.toString())
						)
					);
				} catch (IOException e) 	{ throw new IOException.Runtime(e);
				} catch (HttpException e) 	{ throw new HttpException.Runtime(e.response); }
			}
		},

		new ScenariosApiRoute() {
			@Override protected String getName() { return "survey"; }
			@Override protected String getBearer() { return "06bc983a9307c501"; }
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
			final BindingStorage storage = (
				POLPHARMA_REDIS_DATABASE.createFolderIfNotExists("ophtalm").createFolderIfNotExists("survey")
			);
			@Override protected BindingStorage getStorage() { return storage; }
			@Override protected String getUserRoleById(String id) { return "USER"; }

			@Override protected void checkServerKey(@NotNull String serverKey) throws WrongValue, AccessDenied { PolpharmaBackendKt.checkPolpharmaServerKey(serverKey); }
			@Override protected void onScenarioFinish(String userId, String subjectId, String scenarioId, boolean firstTime, int currentTimeScore) {}
			@Override protected void checkUserAuth(BasicHttpAuth auth) throws NotExists, WrongValue, AccessDenied { PolpharmaBackendKt.checkPolpharmaSuperadminAuth(auth); }

		}

		/*new StorageHttpRoute() {

			@Override protected StreamStorage getStorage(String head) {
				return HTML_DIR;
			}

		}*/

	); }

}
