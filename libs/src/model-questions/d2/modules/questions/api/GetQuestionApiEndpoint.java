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
import org.json.JSONObject;

import static ic.date.Millis.dateToMillis;
import static ic.date.Millis.nullableDateToMillis;


public abstract class GetQuestionApiEndpoint extends ProtectedHttpEndpoint {


	@Override protected String getName() { return "get"; }


	protected abstract BindingStorage getStorage();

	protected abstract String userToString(String userId) throws NotExists;


	@Override protected HttpResponse implementSafeEndpoint(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));
		final String questionId = requestJson.getString("id");

		final BindingStorage storage = getStorage();

		final Mapper<Question, String> questionsMapper = Question.getMapper(storage);

		final Question question = questionsMapper.getItem(questionId);

		final JSONObject responseJson = new JSONObject(); {
			responseJson.put("status", "SUCCESS");
			responseJson.put("askedAt", dateToMillis(question.getAskedAt()));
			responseJson.put("askerId", question.getAskerId());
			try {
				responseJson.put("asker", userToString(question.getAskerId()));
			} catch (NotExists notExists) {}
			responseJson.put("questionText", question.getQuestionText());
			responseJson.putOpt("questionImageUrl", question.getQuestionImageUrl());
			responseJson.putOpt("category", question.getCategory());
			responseJson.putOpt("answeredAt", 		nullableDateToMillis(question.getAnsweredAt()));
			responseJson.putOpt("answererId", 		question.getAnswererId());
			try {
				responseJson.putOpt("answerer", question.getAnswererId() == null ? null : userToString(question.getAnswererId()));
			} catch (NotExists notExists) {}
			responseJson.putOpt("answerText", 		question.getAnswerText());
			responseJson.putOpt("answerImageUrl", 	question.getAnswerImageUrl());
		}

		return new JsonResponse.Final(responseJson);

	}


}
