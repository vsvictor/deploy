package d2.modules.talk.api;


import ic.network.http.FolderHttpRoute;
import ic.network.http.HttpRoute;
import ic.storage.CacheStorage;
import ic.storage.Storage;
import ic.struct.collection.Collection;
import ic.struct.list.List;


public abstract class TalkApiRoute extends FolderHttpRoute.Static {

	@Override protected abstract String getName();

	protected abstract CacheStorage getStorage();
	protected abstract int getUsersCount();
	protected abstract Collection<String> getUserIds();

	protected abstract void onSetSelectedTalks(String userId, String talkEventId);

	@Override protected List<HttpRoute> initChildren() { return new List.Default<>(

		new GetTalkEventsApiMethod() {
			@Override protected Storage getStorage() { return TalkApiRoute.this.getStorage(); }
		},

		new GetTalksApiMethod() {
			@Override protected Storage getStorage() { return TalkApiRoute.this.getStorage(); }
		},

		new GetSpeakersApiMethod() {
			@Override protected Storage getStorage() { return TalkApiRoute.this.getStorage(); }
		},

		new GetTalksByTypesApiMethod() {
			@Override protected CacheStorage getStorage() { return TalkApiRoute.this.getStorage(); }
		},

		new SetSelectedTalksApiMethod() {
			@Override protected CacheStorage getStorage() { return TalkApiRoute.this.getStorage(); }
			@Override protected void onCalled(String userId, String talkEventId) { onSetSelectedTalks(userId, talkEventId); }
		},

		new NotifyBuyTicketApiMethod() {
			@Override protected CacheStorage getStorage() { return TalkApiRoute.this.getStorage(); }
		},

		new GetTicketsStatisticsApiMethod() {
			@Override protected CacheStorage getStorage() 	{ return TalkApiRoute.this.getStorage(); 	}
			@Override protected int getUsersCount() 		{ return TalkApiRoute.this.getUsersCount(); }
		},

		new GetSelectedTalksApiMethod() {
			@Override protected CacheStorage getStorage() { return TalkApiRoute.this.getStorage(); }
		},

		new GetUsersWithOnlyOneDaySelected() {
			@Override protected CacheStorage getStorage() { return TalkApiRoute.this.getStorage(); }
			@Override protected Collection<String> getUserIds() { return TalkApiRoute.this.getUserIds(); }
		}

	); }

}
