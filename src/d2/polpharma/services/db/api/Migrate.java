package d2.polpharma.services.db.api;


import ic.network.SocketAddress;
import ic.network.http.HttpEndpoint;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.JsonResponse;
import ic.throwables.UnableToParse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import static d2.polpharma.services.db.PolpharmaRedisDataBase.POLPHARMA_REDIS_DATABASE;


public class Migrate extends HttpEndpoint {


	@Nullable @Override protected String getName() { return "migrate"; }

	@NotNull @Override protected HttpResponse implementEndpoint(@NotNull SocketAddress socketAddress, @NotNull HttpRequest request) throws UnableToParse {

		final JSONObject responseJson = new JSONObject();

		POLPHARMA_REDIS_DATABASE.createFolderIfNotExists("ophtalm").remove("survey");

		return new JsonResponse.Final(responseJson);

	}

}
