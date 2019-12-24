package d2.modules.scenarios.admin;


import d2.modules.scenarios.model.Subject;
import ic.network.SocketAddress;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.JsonResponse;
import ic.storage.BindingStorage;
import ic.struct.collection.Collection;
import ic.throwables.UnableToParse;
import org.json.JSONObject;

import static d2.modules.scenarios.model.Subject.getSubjectsMapper;
import static ic.json.JsonArrays.toJsonArray;


@Deprecated
public abstract class GetSubjectsApiMethod extends ProtectedAdminApiMethod {

	@Override protected String getName() { return "get_subjects"; }

	protected abstract BindingStorage getStorage();

	@Override protected HttpResponse implementProtectedApi(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final Collection<Subject> subjects = getSubjectsMapper(getStorage()).getItems();

		final JSONObject responseJson = new JSONObject(); {
			responseJson.put("status", "SUCCESS");
			responseJson.put("subjects", toJsonArray(subjects, subject -> {
				final JSONObject subjectJson = new JSONObject();
				subjectJson.put("id", getSubjectsMapper(getStorage()).getId(subject));
				subjectJson.put("title", subject.getTitle());
				subjectJson.put("roles", toJsonArray(subject.getRoles(), role -> role));
				return subjectJson;
			}));
		}

		return new JsonResponse() {
			@Override protected JSONObject getJson() { return responseJson; }
		};

	}

}