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
import ic.throwables.AccessDenied;
import ic.throwables.UnableToParse;
import ic.throwables.WrongState;
import org.json.JSONObject;


public abstract class AnswerApiEndpoint extends ProtectedHttpEndpoint {


	@Override protected String getName() { return "answer"; }


	protected abstract BindingStorage getStorage();


	@Override protected HttpResponse implementSafeEndpoint(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String userId 					= requestJson.getString("userId");
		final String questionId 				= requestJson.getString("questionId");
		final String answerText 				= requestJson.getString("answerText");
		final @Nullable String answerImageUrl = requestJson.optString("answerImageUrl", null);


		final BindingStorage storage = getStorage();

		final Question question = Question.getMapper(storage).getItem(questionId);

		try {

			question.answer(userId, answerText, answerImageUrl);

		} catch (WrongState wrongState) {

			return new JsonResponse() {
				@Override protected Object getJson() {
					final JSONObject responseJson = new JSONObject();
					responseJson.put("status", "ALREADY_ANSWERED");
					return responseJson;
				}
			};

		} catch (AccessDenied accessDenied) {

			return new JsonResponse() {
				@Override protected Object getJson() {
					final JSONObject responseJson = new JSONObject();
					responseJson.put("status", "NO_RIGHTS_TO_ANSWER");
					return responseJson;
				}
			};

		}

		return new JsonResponse() {
			@Override protected Object getJson() {
				final JSONObject responseJson = new JSONObject();
				responseJson.put("status", "SUCCESS");
				return responseJson;
			}
		};

	}


}
