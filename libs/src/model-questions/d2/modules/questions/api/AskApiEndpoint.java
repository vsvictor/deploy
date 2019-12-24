package d2.modules.questions.api;


import d2.modules.questions.model.Question;
import org.jetbrains.annotations.Nullable;
import ic.network.SocketAddress;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.JsonResponse;
import ic.network.http.ProtectedHttpEndpoint;
import ic.storage.BindingStorage;
import ic.text.Charset;
import ic.throwables.UnableToParse;
import org.json.JSONObject;


public abstract class AskApiEndpoint extends ProtectedHttpEndpoint {


	@Override protected String getName() { return "ask"; }


	protected abstract BindingStorage getStorage();

	protected abstract @Nullable String getAnswererIdByCategory(@Nullable String category);


	@Override protected HttpResponse implementSafeEndpoint(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String userId 					= requestJson.getString("userId");
		final String questionText 				= requestJson.getString("questionText");
		final @Nullable String questionImageUrl = requestJson.optString("questionImageUrl", null);
		final @Nullable String category			= requestJson.optString("category", null);

		final @Nullable String answererId; {
			String value = requestJson.optString("answererId", null);
			if (value == null) value = getAnswererIdByCategory(category);
			answererId = value;
		}

		final BindingStorage storage = getStorage();

		final Question question = new Question(storage, userId, questionText, questionImageUrl, category, answererId);

		final String questionId = Question.getMapper(storage).getId(question);

		final JSONObject responseJson = new JSONObject(); {
			responseJson.put("status", "SUCCESS");
			responseJson.put("questionId", questionId);
		}

		return new JsonResponse() {
			@Override protected Object getJson() { return responseJson; }
		};

	}


}
