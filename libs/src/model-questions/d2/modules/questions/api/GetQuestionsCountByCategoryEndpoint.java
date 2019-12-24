package d2.modules.questions.api;


import d2.modules.questions.model.Question;
import ic.network.SocketAddress;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.JsonResponse;
import ic.network.http.ProtectedHttpEndpoint;
import ic.storage.BindingStorage;
import ic.struct.map.EditableMap;
import ic.throwables.UnableToParse;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;


public abstract class GetQuestionsCountByCategoryEndpoint extends ProtectedHttpEndpoint {


	@Override protected String getName() { return "get_questions_count_by_category"; }


	protected abstract BindingStorage getStorage();


	@Override protected HttpResponse implementSafeEndpoint(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final BindingStorage storage = getStorage();

		final JSONObject responseJson = new JSONObject(); {
			responseJson.put("status", "SUCCESS");
		}

		final JSONObject questionsCountByCategoryJson = new JSONObject();

		final EditableMap<Integer, String> questionsCountByCategory 		= new EditableMap.Default<>();
		final EditableMap<Integer, String> answeredQuestionsCountByCategory = new EditableMap.Default<>();

		Question.getMapper(storage).getItems().forEach(question -> {
			final String category = question.getCategory() == null ? "null" : question.getCategory();
			questionsCountByCategory.set(category, questionsCountByCategory.get(category, () -> 0) + 1);
			if (question.getAnswererId() != null) {
				answeredQuestionsCountByCategory.set(category, answeredQuestionsCountByCategory.get(category, () -> 0) + 1);
			}
		});

		questionsCountByCategory.getKeys().forEach(category -> {
			final JSONObject questionsCountJson = new JSONObject();
			questionsCountJson.put("questionsCount", 			questionsCountByCategory.getNotNull(category));
			questionsCountJson.put("answeredQuestionsCount", 	answeredQuestionsCountByCategory.get(category, () -> 0));
			questionsCountByCategoryJson.put(category, questionsCountJson);
		});

		responseJson.put("questionsCountByCategory", questionsCountByCategoryJson);

		return new JsonResponse() {
			@NotNull @Override protected Object getJson() { return responseJson; }
		};

	}


}
