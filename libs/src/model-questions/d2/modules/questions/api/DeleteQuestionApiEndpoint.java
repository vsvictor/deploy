package d2.modules.questions.api;


import d2.modules.questions.model.Question;
import ic.network.SocketAddress;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.JsonResponse;
import ic.network.http.ProtectedHttpEndpoint;
import ic.storage.BindingStorage;
import ic.text.Charset;
import ic.throwables.UnableToParse;
import org.json.JSONObject;


public abstract class DeleteQuestionApiEndpoint extends ProtectedHttpEndpoint {


	@Override protected String getName() { return "delete"; }


	protected abstract BindingStorage getStorage();


	@Override protected HttpResponse implementSafeEndpoint(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String questionId = requestJson.getString("questionId");

		final BindingStorage storage = getStorage();

		Question.getMapper(storage).remove(questionId);

		final JSONObject responseJson = new JSONObject(); {
			responseJson.put("status", "SUCCESS");
		}

		return new JsonResponse() {
			@Override protected Object getJson() { return responseJson; }
		};

	}


}
