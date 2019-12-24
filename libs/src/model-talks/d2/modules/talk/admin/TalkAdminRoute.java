package d2.modules.talk.admin;


import ic.annotations.Redirect;
import ic.network.SocketAddress;
import ic.network.http.*;
import ic.storage.CacheStorage;
import ic.storage.Storage;
import ic.storage.StreamStorage;
import ic.stream.ByteSequence;
import ic.struct.collection.Collection;
import ic.struct.list.List;
import ic.throwables.*;
import org.jetbrains.annotations.NotNull;

import static ic.Assets.resources;


public abstract class TalkAdminRoute extends FolderHttpRoute.Static {

	@Override protected abstract String getName();

	protected abstract String getPassword();

	protected abstract CacheStorage getStorage();

	protected abstract Collection<String> getUserIds();

	protected abstract String getUserName(String userId);
	protected abstract String getUserPhone(String userId);

	protected abstract void checkUserAuth(@NotNull BasicHttpAuth auth) throws NotExists, WrongValue, AccessDenied;

	@Override protected List<HttpRoute> initChildren() { return new List.Default<HttpRoute>(

		new FolderHttpRoute.Static() {
			@Override protected String getName() { return "api"; }
			@Override protected List<HttpRoute> initChildren() { return new List.Default<HttpRoute>(

				new GetTalkEventsApiMethod() {
					@Redirect @Override protected Storage getStorage() { return TalkAdminRoute.this.getStorage(); }
					@Redirect @Override protected String getPassword() { return TalkAdminRoute.this.getPassword(); }
				},
				new AddTalkEventApiMethod() {
					@Redirect @Override protected Storage getStorage() { return TalkAdminRoute.this.getStorage(); }
					@Redirect @Override protected String getPassword() { return TalkAdminRoute.this.getPassword(); }
				},
				new GetTalkEventApiMethod() {
					@Redirect @Override protected Storage getStorage() { return TalkAdminRoute.this.getStorage(); }
					@Redirect @Override protected String getPassword() { return TalkAdminRoute.this.getPassword(); }
				},
				new SetTalkEventTitleApiMethod() {
					@Redirect @Override protected Storage getStorage() { return TalkAdminRoute.this.getStorage(); }
					@Redirect @Override protected String getPassword() { return TalkAdminRoute.this.getPassword(); }
				},
				new SetTalkEventDescriptionApiMethod() {
					@Redirect @Override protected Storage getStorage() { return TalkAdminRoute.this.getStorage(); }
					@Redirect @Override protected String getPassword() { return TalkAdminRoute.this.getPassword(); }
				},
				new SetTalkEventImageUrlApiMethod() {
					@Redirect @Override protected Storage getStorage() { return TalkAdminRoute.this.getStorage(); }
					@Redirect @Override protected String getPassword() { return TalkAdminRoute.this.getPassword(); }
				},
				new SetTalkEventStartDateApiMethod() {
					@Redirect @Override protected Storage getStorage() { return TalkAdminRoute.this.getStorage(); }
					@Redirect @Override protected String getPassword() { return TalkAdminRoute.this.getPassword(); }
				},
				new SetTalkEventEndDateApiMethod() {
					@Redirect @Override protected Storage getStorage() { return TalkAdminRoute.this.getStorage(); }
					@Redirect @Override protected String getPassword() { return TalkAdminRoute.this.getPassword(); }
				},

				new GetStagesApiMethod() {
					@Redirect @Override protected Storage getStorage() { return TalkAdminRoute.this.getStorage(); }
					@Redirect @Override protected String getPassword() { return TalkAdminRoute.this.getPassword(); }
				},
				new AddStageApiMethod() {
					@Redirect @Override protected Storage getStorage() { return TalkAdminRoute.this.getStorage(); }
					@Redirect @Override protected String getPassword() { return TalkAdminRoute.this.getPassword(); }
				},
				new AddStageToTalkEventApiMethod() {
					@Redirect @Override protected Storage getStorage() { return TalkAdminRoute.this.getStorage(); }
					@Redirect @Override protected String getPassword() { return TalkAdminRoute.this.getPassword(); }
				},
				new GetStageApiMethod() {
					@Redirect @Override protected Storage getStorage() { return TalkAdminRoute.this.getStorage(); }
					@Redirect @Override protected String getPassword() { return TalkAdminRoute.this.getPassword(); }
				},
				new SetStageNameApiMethod() {
					@Redirect @Override protected Storage getStorage() { return TalkAdminRoute.this.getStorage(); }
					@Redirect @Override protected String getPassword() { return TalkAdminRoute.this.getPassword(); }
				},
				new SetStageCoverImageUrlApiMethod() {
					@Redirect @Override protected Storage getStorage() { return TalkAdminRoute.this.getStorage(); }
					@Redirect @Override protected String getPassword() { return TalkAdminRoute.this.getPassword(); }
				},
				new SetStagePlanImageUrlApiMethod() {
					@Redirect @Override protected Storage getStorage() { return TalkAdminRoute.this.getStorage(); }
					@Redirect @Override protected String getPassword() { return TalkAdminRoute.this.getPassword(); }
				},

				new GetSpeakersApiMethod() {
					@Redirect @Override protected Storage getStorage() { return TalkAdminRoute.this.getStorage(); }
					@Redirect @Override protected String getPassword() { return TalkAdminRoute.this.getPassword(); }
				},
				new AddSpeakerApiMethod() {
					@Redirect @Override protected Storage getStorage() { return TalkAdminRoute.this.getStorage(); }
					@Redirect @Override protected String getPassword() { return TalkAdminRoute.this.getPassword(); }
				},
				new AddSpeakerToTalkEventApiMethod() {
					@Redirect @Override protected Storage getStorage() { return TalkAdminRoute.this.getStorage(); }
					@Redirect @Override protected String getPassword() { return TalkAdminRoute.this.getPassword(); }
				},
				new GetSpeakerApiMethod() {
					@Redirect @Override protected Storage getStorage() { return TalkAdminRoute.this.getStorage(); }
					@Redirect @Override protected String getPassword() { return TalkAdminRoute.this.getPassword(); }
				},
				new SetSpeakerNameApiMethod() {
					@Redirect @Override protected Storage getStorage() { return TalkAdminRoute.this.getStorage(); }
					@Redirect @Override protected String getPassword() { return TalkAdminRoute.this.getPassword(); }
				},
				new SetSpeakerSurnameApiMethod() {
					@Redirect @Override protected Storage getStorage() { return TalkAdminRoute.this.getStorage(); }
					@Redirect @Override protected String getPassword() { return TalkAdminRoute.this.getPassword(); }
				},
				new SetSpeakerShortDescriptionApiMethod() {
					@Redirect @Override protected Storage getStorage() { return TalkAdminRoute.this.getStorage(); }
					@Redirect @Override protected String getPassword() { return TalkAdminRoute.this.getPassword(); }
				},
				new SetSpeakerDescriptionApiMethod() {
					@Redirect @Override protected Storage getStorage() { return TalkAdminRoute.this.getStorage(); }
					@Redirect @Override protected String getPassword() { return TalkAdminRoute.this.getPassword(); }
				},
				new SetSpeakerImageUrlApiMethod() {
					@Redirect @Override protected Storage getStorage() { return TalkAdminRoute.this.getStorage(); }
					@Redirect @Override protected String getPassword() { return TalkAdminRoute.this.getPassword(); }
				},
				new SetSpeakerShowInListApiMethod() {
					@Redirect @Override protected Storage getStorage() { return TalkAdminRoute.this.getStorage(); }
					@Redirect @Override protected String getPassword() { return TalkAdminRoute.this.getPassword(); }
				},

				new GetTalksApiMethod() {
					@Redirect @Override protected Storage getStorage() { return TalkAdminRoute.this.getStorage(); }
					@Redirect @Override protected String getPassword() { return TalkAdminRoute.this.getPassword(); }
				},
				new AddTalkApiMethod() {
					@Redirect @Override protected Storage getStorage() { return TalkAdminRoute.this.getStorage(); }
					@Redirect @Override protected String getPassword() { return TalkAdminRoute.this.getPassword(); }
				},
				new AddTalkToTalkEventApiMethod() {
					@Redirect @Override protected Storage getStorage() { return TalkAdminRoute.this.getStorage(); }
					@Redirect @Override protected String getPassword() { return TalkAdminRoute.this.getPassword(); }
				},
				new GetTalkApiMethod() {
					@Redirect @Override protected Storage getStorage() { return TalkAdminRoute.this.getStorage(); }
					@Redirect @Override protected String getPassword() { return TalkAdminRoute.this.getPassword(); }
				},
				new SetTalkNameApiMethod() {
					@Redirect @Override protected Storage getStorage() { return TalkAdminRoute.this.getStorage(); }
					@Redirect @Override protected String getPassword() { return TalkAdminRoute.this.getPassword(); }
				},
				new SetTalkShortDescriptionApiMethod() {
					@Redirect @Override protected Storage getStorage() { return TalkAdminRoute.this.getStorage(); }
					@Redirect @Override protected String getPassword() { return TalkAdminRoute.this.getPassword(); }
				},
				new SetTalkDescriptionApiMethod() {
					@Redirect @Override protected Storage getStorage() { return TalkAdminRoute.this.getStorage(); }
					@Redirect @Override protected String getPassword() { return TalkAdminRoute.this.getPassword(); }
				},
				new SetTalkStartDateApiMethod() {
					@Redirect @Override protected Storage getStorage() { return TalkAdminRoute.this.getStorage(); }
					@Redirect @Override protected String getPassword() { return TalkAdminRoute.this.getPassword(); }
				},
				new SetTalkEndDateApiMethod() {
					@Redirect @Override protected Storage getStorage() { return TalkAdminRoute.this.getStorage(); }
					@Redirect @Override protected String getPassword() { return TalkAdminRoute.this.getPassword(); }
				},
				new SetTalkImageUrlApiMethod() {
					@Redirect @Override protected Storage getStorage() { return TalkAdminRoute.this.getStorage(); }
					@Redirect @Override protected String getPassword() { return TalkAdminRoute.this.getPassword(); }
				},
				new SetTalkPlacesCountApiMethod() {
					@Redirect @Override protected Storage getStorage() { return TalkAdminRoute.this.getStorage(); }
					@Redirect @Override protected String getPassword() { return TalkAdminRoute.this.getPassword(); }
				},
				new AddSpeakerToTalkApiMethod() {
					@Redirect @Override protected Storage getStorage() { return TalkAdminRoute.this.getStorage(); }
					@Redirect @Override protected String getPassword() { return TalkAdminRoute.this.getPassword(); }
				},
				new RemoveSpeakerFromTalkApiMethod() {
					@Redirect @Override protected Storage getStorage() { return TalkAdminRoute.this.getStorage(); }
					@Redirect @Override protected String getPassword() { return TalkAdminRoute.this.getPassword(); }
				},
				new SetTalkStageApiMethod() {
					@Redirect @Override protected Storage getStorage() { return TalkAdminRoute.this.getStorage(); }
					@Redirect @Override protected String getPassword() { return TalkAdminRoute.this.getPassword(); }
				},

				new UploadTalksCsvApiMethod() {
					@Override protected void checkUserAuth(@NotNull BasicHttpAuth auth) throws NotExists, WrongValue, AccessDenied { TalkAdminRoute.this.checkUserAuth(auth); }
					@Redirect @Override protected Storage getStorage() { return TalkAdminRoute.this.getStorage(); }
				},

				new ClearTalksApiMethod() {
					@Redirect @Override protected Storage getStorage() { return TalkAdminRoute.this.getStorage(); }
					@Redirect @Override protected String getPassword() { return TalkAdminRoute.this.getPassword(); }
				},
				new ClearSpeakersApiMethod() {
					@Redirect @Override protected Storage getStorage() { return TalkAdminRoute.this.getStorage(); }
					@Redirect @Override protected String getPassword() { return TalkAdminRoute.this.getPassword(); }
				},

				new UsersCsvApiMethod() {
					@Redirect @Override protected CacheStorage getStorage() 			{ return TalkAdminRoute.this.getStorage(); 			}
					@Redirect @Override protected Collection<String> getUserIds() 		{ return TalkAdminRoute.this.getUserIds(); 			}
					@Redirect @Override protected String getUserName(String userId) 	{ return TalkAdminRoute.this.getUserName(userId); 	}
					@Redirect @Override protected String getUserPhone(String userId) 	{ return TalkAdminRoute.this.getUserPhone(userId); 	}
				}

			); }
		},

		new StorageHttpRoute() {
			@Override protected StreamStorage getStorage(String head) {
				final StreamStorage htmlFolder = resources.get("html");
				return htmlFolder;
			}
		},

		new StorageHttpRoute() {
			@Override protected StreamStorage getStorage(String head) { return resources; }
		},

		new HttpRoute() {
			@Override public HttpResponse implementRoute(SocketAddress socketAddress, HttpRequest request, boolean root) throws NotSupported, UnableToParse {
				final StreamStorage htmlFolder = resources.getNotNull("html");
				final ByteSequence file;
				try {
					file = htmlFolder.getInput("talk-admin.html").read();
				} catch (NotExists notFound) {
					throw new NotExists.Error(notFound);
				}
				return new HttpResponse() {
					@Override public ByteSequence getBody() {
						return file;
					}
				};
			}
		}

	); }

}
