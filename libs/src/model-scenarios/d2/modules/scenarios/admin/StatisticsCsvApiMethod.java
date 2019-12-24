package d2.modules.scenarios.admin;


import d2.modules.scenarios.model.Block;
import d2.modules.scenarios.model.History;
import d2.modules.scenarios.model.Scenario;

import static d2.modules.scenarios.model.Scenario.getScenariosMapper;
import static ic.csv.Csv.formatCsv;

import ic.interfaces.action.SafeAction1;
import ic.mimetypes.MimeType;
import ic.network.SocketAddress;
import ic.network.http.HttpEndpoint;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.JsonResponse;
import ic.storage.BindingStorage;
import ic.stream.ByteSequence;
import ic.struct.collection.Collection;
import ic.struct.list.ConvertList;
import ic.struct.list.JoinList;
import ic.struct.list.List;
import ic.struct.map.CountableMap;
import ic.struct.map.EditableMap;
import static ic.struct.variable.Variable.Constant;

import ic.text.Charset;
import ic.text.Text;
import ic.throwables.*;
import org.json.JSONObject;

import static d2.modules.scenarios.model.History.getHistory;


public abstract @Deprecated class StatisticsCsvApiMethod extends HttpEndpoint {

	@Override protected String getName() {
		return "statistics.csv";
	}

	protected abstract BindingStorage getStorage();

	protected abstract Collection<String> getUserIds();

	@Override protected HttpResponse implementEndpoint(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final BindingStorage storage = getStorage();

		final String subjectId 	= request.urlParams.getNotNull("subject_id");
		final String scenarioId = request.urlParams.getNotNull("scenario_id");

		final Charset charset; {
			final String charsetString = request.urlParams.get("charset");
			if (charsetString == null) 					charset = Charset.DEFAULT_HTTP; else
			if (charsetString.equals("windows-1251")) 	charset = Charset.WINDOWS_1251;
			else throw new InvalidValue.Runtime("charset: " + charsetString);
		}

		final EditableMap<EditableMap<Integer, Integer>, String> counterByBlockIdAndWay = new EditableMap.Default<EditableMap<Integer, Integer>, String>(); {

			getUserIds().forEach(userId -> {

				final List<History.Item> historyItems = getHistory(storage, userId, subjectId, scenarioId).getItems();

				historyItems.forEach(new SafeAction1<History.Item, Break>() {
					History.Item previousItem;
					@Override public void run(History.Item item) {
						assert item != null;
						if (previousItem != null) {
							final EditableMap<Integer, Integer> counterByWay = counterByBlockIdAndWay.createIfNull(
								previousItem.blockId,
								EditableMap.Default::new
							);
							counterByWay.set(
								item.way,
								counterByWay.get(item.way, new Constant<Integer>(0)) + 1
							);
						}
						previousItem = item;
					}
				});

			});

		}

		final List<Block> blocks; {
			List<Block> blocksValue;
			try {
				final Scenario scenario = getScenariosMapper(storage, subjectId).getItemOrThrow(scenarioId);
				blocksValue = scenario.getBlocks();
			} catch (Empty empty) {
				return new JsonResponse() {
					@Override protected JSONObject getJson() {
						final JSONObject responseJson = new JSONObject();
						responseJson.put("status", "EMPTY_SCENARIO");
						return responseJson;
					}
				};
			} catch (NotExists notExists) {
				blocksValue = new ConvertList<Block, String>(
					new List.Default<>(counterByBlockIdAndWay.getKeys()),
					blockId -> new Block(blockId, "TEXT", "", "NONE", new List.Default<>())
				);
			}
			blocks = blocksValue;
		}

		final Text csv = formatCsv(
			new JoinList<List<String>>(
				new List.Default<List<String>>(
					new List.Default<String>(
						"block.id", "block.content", "block.ways[0]", "block.ways[1]", "block.ways[2]", "block.ways[3]"
					)
				),
				new ConvertList<>(
					blocks,
					block -> {
						final CountableMap<Integer, Integer> counterByWay = counterByBlockIdAndWay.get(
							block.id,
							EditableMap.Default::new
						);
						return new List.Default<>(
							block.id,
							block.content,
							Integer.toString(counterByWay.get(0, new Constant<>(0))),
							Integer.toString(counterByWay.get(1, new Constant<>(0))),
							Integer.toString(counterByWay.get(2, new Constant<>(0))),
							Integer.toString(counterByWay.get(3, new Constant<>(0)))
						);
					}
				)
			)
		);

		return new HttpResponse() {
			@Override public MimeType getContentType() 	{ return MimeType.CSV; 						}
			@Override public Charset getCharset() 		{ return charset;	 						}
			@Override public ByteSequence getBody() 	{ return charset.textToByteSequence(csv); 	}
		};

	}

}
