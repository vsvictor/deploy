package d2.modules.talk.admin;


import d2.modules.talk.model.Selection;
import d2.modules.talk.model.Talk;
import d2.modules.talk.model.TalkEvent;
import ic.id.Mapper;
import ic.mimetypes.MimeType;
import ic.network.SocketAddress;
import ic.network.http.HttpEndpoint;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.storage.CacheStorage;
import ic.stream.ByteSequence;
import ic.struct.collection.Collection;
import ic.struct.collection.ConvertCollection;
import ic.struct.collection.FilterCollection;
import ic.struct.list.ConvertList;
import ic.struct.list.JoinList;
import ic.struct.list.List;
import ic.struct.set.CountableSet;
import ic.text.Charset;
import ic.text.Text;
import ic.throwables.InvalidValue;
import ic.throwables.UnableToParse;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static d2.modules.talk.model.Talk.getTalksMapper;
import static d2.modules.talk.model.TalkEvent.getTalkEventsMapper;
import static ic.csv.Csv.formatCsv;
import static java.util.Objects.equals;


public abstract class UsersCsvApiMethod extends HttpEndpoint {


	@Override protected String getName() {
		return "users.csv";
	}


	protected abstract CacheStorage getStorage();

	protected abstract Collection<String> getUserIds();

	protected abstract String getUserName(String userId);
	protected abstract String getUserPhone(String userId);


	@Override protected HttpResponse implementEndpoint(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final CacheStorage storage = getStorage();

		final String talkEventId = request.urlParams.getNotNull("talk_event_id");

		final @Nullable String talkType = request.urlParams.get("talk_type");

		final Charset charset; {
			final String charsetString = request.urlParams.get("charset");
			if (charsetString == null) 					charset = Charset.DEFAULT_HTTP; else
			if (charsetString.equals("windows-1251")) 	charset = Charset.WINDOWS_1251;
			else throw new InvalidValue.Runtime("charset: " + charsetString);
		}

		final TalkEvent talkEvent = getTalkEventsMapper(storage).getItem(talkEventId);

		final Mapper<Talk, String> talksMapper = getTalksMapper(storage);

		final List<String> talkIds = new List.Default<>(
			new FilterCollection<>(
				talkEvent.getTalkIds(),
				talkId -> {
					if (talkType == null) return true;
					return Objects.equals(
						talksMapper.getItem(talkId).getType(),
						talkType
					);
				}
			)
		);

		final Text csv = formatCsv(
			new JoinList<>(
				new List.Default<List<String>>(
					new JoinList<String>(
						new List.Default<>(
							"user.name",
							"user.phone"
						),
						new ConvertList<>(
							talkIds,
							talkId -> talksMapper.getItem(talkId).getName()
						)
					)
				),
				new List.Default<>(
					new ConvertCollection<>(
						getUserIds(),
						userId -> {
							final String userName;
							{
								String value = getUserName(userId);
								if (value == null) value = "";
								userName = value;
							}
							final String userPhone;
							{
								String value = getUserPhone(userId);
								if (value == null) value = "";
								userPhone = value;
							}
							final CountableSet<String> selectedTalkIds = Selection.getSelectedTalkIds(storage, userId, talkEventId);
							return new JoinList<>(
								new List.Default<>(
									userName,
									userPhone
								),
								new ConvertList<>(
									talkIds,
									talkId -> Boolean.toString(selectedTalkIds.contains(talkId))
								)
							);
						}
					)
				)
			)
		);

		return new HttpResponse() {
			@Override public Charset getCharset() 		{ return charset; 		}
			@Override public MimeType getContentType() 	{ return MimeType.CSV; 	}
			@Override public ByteSequence getBody() {
				return charset.textToByteSequence(csv);
			}
		};

	}


}
