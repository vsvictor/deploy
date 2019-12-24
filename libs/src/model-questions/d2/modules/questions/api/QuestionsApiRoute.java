package d2.modules.questions.api;


import ic.network.http.BasicHttpAuth;
import ic.network.http.FolderHttpRoute;
import ic.network.http.HttpRoute;
import ic.storage.BindingStorage;
import ic.struct.list.List;
import ic.throwables.AccessDenied;
import ic.throwables.NotExists;
import ic.throwables.WrongValue;
import org.jetbrains.annotations.Nullable;


public abstract class QuestionsApiRoute extends FolderHttpRoute.Static {


	protected abstract void checkServerKey(String serverKey) throws WrongValue, AccessDenied;
	protected abstract void checkUserAuth(BasicHttpAuth auth) throws NotExists, WrongValue, AccessDenied;

	protected abstract BindingStorage getStorage();

	protected abstract @Nullable String getAnswererIdByCategory(@Nullable String category);

	protected abstract String userToString(String userId) throws NotExists;


	@Override protected List<HttpRoute> initChildren() { return new List.Default<>(

		new AskApiEndpoint() {
			@Override protected void checkServerKey(String serverKey) throws WrongValue, AccessDenied { QuestionsApiRoute.this.checkServerKey(serverKey); }
			@Override protected void checkUserAuth(BasicHttpAuth auth) throws NotExists, WrongValue, AccessDenied { QuestionsApiRoute.this.checkUserAuth(auth); }
			@Override protected BindingStorage getStorage() 					{ return QuestionsApiRoute.this.getStorage(); 						}
			@Override protected String getAnswererIdByCategory(String category) { return QuestionsApiRoute.this.getAnswererIdByCategory(category); 	}
		},

		new AnswerApiEndpoint() {
			@Override protected void checkServerKey(String serverKey) throws WrongValue, AccessDenied { QuestionsApiRoute.this.checkServerKey(serverKey); }
			@Override protected void checkUserAuth(BasicHttpAuth auth) throws NotExists, WrongValue, AccessDenied { QuestionsApiRoute.this.checkUserAuth(auth); }
			@Override protected BindingStorage getStorage() { return QuestionsApiRoute.this.getStorage(); }
		},

		new GetQuestionsApiEndpoint() {
			@Override protected void checkServerKey(String serverKey) throws WrongValue, AccessDenied { QuestionsApiRoute.this.checkServerKey(serverKey); }
			@Override protected void checkUserAuth(BasicHttpAuth auth) throws NotExists, WrongValue, AccessDenied { QuestionsApiRoute.this.checkUserAuth(auth); }
			@Override protected BindingStorage getStorage() { return QuestionsApiRoute.this.getStorage(); }
			@Override protected String userToString(String userId) throws NotExists { return QuestionsApiRoute.this.userToString(userId); }
		},

		new ListQuestionsApiEndpoint() {
			@Override protected void checkServerKey(String serverKey) throws WrongValue, AccessDenied { QuestionsApiRoute.this.checkServerKey(serverKey); }
			@Override protected void checkUserAuth(BasicHttpAuth auth) throws NotExists, WrongValue, AccessDenied { QuestionsApiRoute.this.checkUserAuth(auth); }
			@Override protected BindingStorage getStorage() { return QuestionsApiRoute.this.getStorage(); }
			@Override protected String userToString(String userId) throws NotExists { return QuestionsApiRoute.this.userToString(userId); }
		},

		new CountQuestionsApiEndpoint() {
			@Override protected void checkServerKey(String serverKey) throws WrongValue, AccessDenied { QuestionsApiRoute.this.checkServerKey(serverKey); }
			@Override protected void checkUserAuth(BasicHttpAuth auth) throws NotExists, WrongValue, AccessDenied { QuestionsApiRoute.this.checkUserAuth(auth); }
			@Override protected BindingStorage getStorage() { return QuestionsApiRoute.this.getStorage(); }
			@Override protected String userToString(String userId) throws NotExists { return QuestionsApiRoute.this.userToString(userId); }
		},

		new GetQuestionApiEndpoint() {
			@Override protected void checkServerKey(String serverKey) throws WrongValue, AccessDenied { QuestionsApiRoute.this.checkServerKey(serverKey); }
			@Override protected void checkUserAuth(BasicHttpAuth auth) throws NotExists, WrongValue, AccessDenied { QuestionsApiRoute.this.checkUserAuth(auth); }
			@Override protected BindingStorage getStorage() { return QuestionsApiRoute.this.getStorage(); }
			@Override protected String userToString(String userId) throws NotExists { return QuestionsApiRoute.this.userToString(userId); }
		},

		new GetQuestionsCountByCategoryEndpoint() {
			@Override protected void checkServerKey(String serverKey) throws WrongValue, AccessDenied { QuestionsApiRoute.this.checkServerKey(serverKey); }
			@Override protected void checkUserAuth(BasicHttpAuth auth) throws NotExists, WrongValue, AccessDenied { QuestionsApiRoute.this.checkUserAuth(auth); }
			@Override protected BindingStorage getStorage() { return QuestionsApiRoute.this.getStorage(); }
		},

		new DeleteQuestionApiEndpoint() {
			@Override protected void checkServerKey(String serverKey) throws WrongValue, AccessDenied { QuestionsApiRoute.this.checkServerKey(serverKey); }
			@Override protected void checkUserAuth(BasicHttpAuth auth) throws NotExists, WrongValue, AccessDenied { QuestionsApiRoute.this.checkUserAuth(auth); }
			@Override protected BindingStorage getStorage() { return QuestionsApiRoute.this.getStorage(); }
		}

	); }


}
