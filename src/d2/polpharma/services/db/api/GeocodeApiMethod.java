package d2.polpharma.services.db.api;


import ic.geo.Location;
import ic.network.SocketAddress;
import ic.network.http.HttpException;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.JsonResponse;
import ic.storage.Storage;
import ic.text.Charset;
import ic.text.Language;
import ic.throwables.AccessDenied;
import ic.throwables.Empty;
import ic.throwables.IOException;
import ic.throwables.UnableToParse;
import org.json.JSONObject;

import static ic.geo.Place.findPlacesByAddress;


public abstract class GeocodeApiMethod extends ProtectedApiMethod {

	@Override protected String getName() { return "geocode"; }

	protected abstract Storage getStorage();

	@Override protected HttpResponse implementProtectedApi(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {

		final JSONObject requestJson = new JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body));

		final String address = requestJson.getString("address");

		final Location location; try {

			location = getStorage().createIfNull(address, () -> {

				try {
					return findPlacesByAddress(address, Language.UKRAINIAN, "AIzaSyD1S25d5AwtpMaDojDUSHP_2idbRTOSbiI").getAny().location;
				} catch (IOException ioException) 	{ throw new IOException.Runtime(ioException);
				} catch (HttpException e) 			{ throw new HttpException.Runtime(e);
				} catch (AccessDenied accessDenied) { throw new AccessDenied.Runtime(accessDenied); 	}

			});

		} catch (Empty.Runtime empty) {

			return new JsonResponse() {
				@Override protected JSONObject getJson() {
					final JSONObject responseJson = new JSONObject();
					responseJson.put("status", "NOT_FOUND");
					return responseJson;
				}
			};

		}

		return new JsonResponse() {
			@Override protected JSONObject getJson() {
				final JSONObject responseJson = new JSONObject();
				responseJson.put("status", "SUCCESS");
				{ final JSONObject locationJson = new JSONObject();
					locationJson.put("latitude", location.latitude);
					locationJson.put("longitude", location.longitude);
					responseJson.put("location", locationJson);
				}
				return responseJson;
			}
		};

	}

}
