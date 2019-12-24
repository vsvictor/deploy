package d2.modules.scenarios.api;


import d2.modules.scenarios.model.Subject;
import ic.annotations.Same;
import ic.id.Mapper;
import ic.interfaces.getter.Safe2Getter1;
import ic.json.JsonArrays;
import ic.network.SocketAddress;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.JsonResponse;
import ic.network.http.ProtectedHttpEndpoint;
import ic.storage.BindingStorage;
import ic.struct.collection.Collection;
import ic.text.Charset;
import ic.throwables.NotExists;
import ic.throwables.UnableToParse;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import static d2.modules.scenarios.model.History.isSubjectPassed;
import static d2.modules.scenarios.model.Subject.getSubjectsMapper;
import static ic.json.JsonArrays.toJsonArray;
import static ic.throwables.Skip.SKIP;


public abstract class GetSubjectsApiMethod extends ProtectedHttpEndpoint {

	@Same
	protected abstract BindingStorage getStorage();

	protected abstract String getUserRoleById(String id) throws NotExists;

	@Override protected String getName() { return "get_subjects"; }

	@Override protected HttpResponse implementSafeEndpoint(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final BindingStorage storage = getStorage();

		final @Nullable String userId; {
			if (request.method.equals("GET")) {
				userId = null;
			} else {
				final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));
				userId = requestJson.getString("userId");
			}
		}

		final @Nullable String role; {
			if (userId == null) {
				role = null;
			} else {
				try {
					role = getUserRoleById(userId);
				} catch (NotExists notExists) {
					return new JsonResponse() {
						@Override protected JSONObject getJson() {
							final JSONObject responseJson = new JSONObject();
							responseJson.put("status", "USER_NOT_FOUND");
							return responseJson;
						}
					};
				}
			}
		}

		final Mapper<Subject, String> subjectsMapper = getSubjectsMapper(storage);

		final Collection<String> subjectIds = subjectsMapper.getIds();

		final JSONObject responseJson = new JSONObject();

		responseJson.put("status", "SUCCESS");

		responseJson.put("subjects", JsonArrays.toJsonArray(subjectIds, subjectId -> {
			final Subject subject = subjectsMapper.getItem(subjectId);
			if (role != null) {
				if (!subject.getRoles().contains(role)) throw SKIP;
			}
			final JSONObject subjectJson = new JSONObject();
			subjectJson.put("id", 		subjectId);
			subjectJson.put("title", 	subject.getTitle());
			if (userId == null) {
				subjectJson.put("roles", toJsonArray(subject.getRoles(), subjectRole -> subjectRole));
			} else {
				subjectJson.put("passed", isSubjectPassed(storage, userId, subject));
			}
			return subjectJson;
		}));

		return new JsonResponse() { @Override protected JSONObject getJson() { return responseJson; } };

	}

}
