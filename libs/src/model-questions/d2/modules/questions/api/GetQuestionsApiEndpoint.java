package d2.modules.questions.api;


import d2.modules.questions.model.Question;
import ic.id.Mapper;
import ic.network.SocketAddress;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.JsonResponse;
import ic.network.http.ProtectedHttpEndpoint;
import ic.storage.BindingStorage;
import ic.text.Charset;
import ic.throwables.NotExists;
import ic.throwables.UnableToParse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.Objects;

import static ic.date.Millis.dateToMillis;
import static ic.date.Millis.nullableDateToMillis;
import static ic.json.JsonArrays.toJsonArray;
import static ic.throwables.Skip.SKIP;


@Deprecated
public abstract class GetQuestionsApiEndpoint extends ProtectedHttpEndpoint {


	@Override protected String getName() { return "get_questions"; }


	protected abstract BindingStorage getStorage();

	protected abstract String userToString(String userId) throws NotExists;


	@Override protected HttpResponse implementSafeEndpoint(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final @Nullable String askerId 		= requestJson.optString("askerId", 		null);
		final @Nullable String category 	= requestJson.optString("category", 	null);
		final @Nullable String answererId 	= requestJson.optString("answererId", 	null);

		final BindingStorage storage = getStorage();

		final Mapper<Question, String> questionsMapper = Question.getMapper(storage);

		final JSONObject responseJson = new JSONObject(); {
			responseJson.put("status", "SUCCESS");
		}

		responseJson.put("questions", toJsonArray(questionsMapper.getItems(), question -> {

			if (askerId != null  	&& !Objects.equals(question.getAskerId(),    		askerId)) 	 throw SKIP;
			if (category != null 	&& !Objects.equals(question.getCategory(),   		category)) 	 throw SKIP;
			if (answererId != null 	&& !Objects.equals(question.getAnswererId(), 	answererId)) throw SKIP;

			final JSONObject questionJson = new JSONObject();
			questionJson.put("askedAt", 			dateToMillis(question.getAskedAt()));
			questionJson.put("askerId", question.getAskerId());
			try {
				questionJson.put("asker", userToString(question.getAskerId()));
			} catch (NotExists notExists) {}
			questionJson.put("questionText", question.getQuestionText());
			questionJson.putOpt("questionImageUrl", question.getQuestionImageUrl());
			questionJson.putOpt("category", question.getCategory());
			questionJson.putOpt("answeredAt", 		nullableDateToMillis(question.getAnsweredAt()));
			questionJson.putOpt("answererId", 		question.getAnswererId());
			try {
				questionJson.putOpt("answerer", question.getAnswererId() == null ? null : userToString(question.getAnswererId()));
			} catch (NotExists notExists) {}
			questionJson.putOpt("answerText", 		question.getAnswerText());
			questionJson.putOpt("answerImageUrl", 	question.getAnswerImageUrl());
			return questionJson;

		}));


		return new JsonResponse() {
			@NotNull @Override protected Object getJson() { return responseJson; }
		};

	}


}
