package lazyteam.adminka;


import ic.interfaces.getter.Safe2Getter1;
import ic.json.JsonArrays;
import ic.network.SocketAddress;
import ic.network.http.*;
import ic.storage.StreamStorage;
import ic.stream.ByteSequence;
import ic.struct.list.List;
import ic.throwables.InconsistentData;
import ic.throwables.NotExists;
import ic.throwables.Skip;
import ic.throwables.UnableToParse;
import lazyteam.http.PasswordProtectedHttpEndpoint;
import org.json.JSONException;
import org.json.JSONObject;
import static ic.Assets.resources;


public @Deprecated abstract class ImagesAdminkaRoute extends FolderHttpRoute.Static {

	protected abstract StreamStorage initImagesStorage();

	protected abstract String getPassword();

	private StreamStorage imagesStorage = initImagesStorage();

	@Override protected List<HttpRoute> initChildren() { return new List.Default<HttpRoute>(

		new HttpEndpoint() {
			@Override protected String getName() { return "adminka"; }
			@Override protected HttpResponse implementEndpoint(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {
				final StreamStorage htmlStorage = resources.getNotNull("html");
				final ByteSequence file; try {
					file = htmlStorage.getInput("adminka-images.html").read();
				} catch (NotExists notFound) { throw new InconsistentData(notFound); }
				return new HttpResponse() {
					@Override public ByteSequence getBody() { return file; }
				};
			}
		},

		new StorageHttpRoute() {
			@Override protected StreamStorage getStorage(String head) { return resources; }
		},

		new FolderHttpRoute.Static() {
			@Override protected String getName() { return "api"; }
			@Override protected List<HttpRoute> initChildren() { return new List.Default<HttpRoute>(
				new PasswordProtectedHttpEndpoint() {
					@Override protected String getName() { return "get-image-names"; }
					@Override protected String getPassword() { return ImagesAdminkaRoute.this.getPassword(); }
					@Override protected HttpResponse implementSafeEndpoint(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {
						return new JsonResponse() { @Override protected JSONObject getJson() {
							final JSONObject responseJson = new JSONObject();
							responseJson.put("imageNames", JsonArrays.toJsonArray(imagesStorage.getKeys(), (Safe2Getter1<Object, String, JSONException, Skip>) imageName -> imageName));
							return responseJson;
						} };
					}
				},
				new PasswordProtectedHttpEndpoint() {
					@Override protected String getName() { return "upload-image"; }
					@Override protected String getPassword() { return ImagesAdminkaRoute.this.getPassword(); }
					@Override protected HttpResponse implementSafeEndpoint(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {
						imagesStorage.write(
							request.urlParams.get("name"),
							request.body
						);
						return new JsonResponse() { @Override protected JSONObject getJson() {
							final JSONObject responseJson = new JSONObject();
							responseJson.put("status", "SUCCESS");
							return responseJson;
						} };
					}
				},
				new PasswordProtectedHttpEndpoint() {
					@Override protected String getName() { return "delete-image"; }
					@Override protected String getPassword() { return ImagesAdminkaRoute.this.getPassword(); }
					@Override protected HttpResponse implementSafeEndpoint(SocketAddress socketAddress, HttpRequest request) throws UnableToParse {
						imagesStorage.set(request.urlParams.get("name"), null);
						return new JsonResponse() { @Override protected JSONObject getJson() {
							final JSONObject responseJson = new JSONObject();
							responseJson.put("status", "SUCCESS");
							return responseJson;
						} };
					}
				}
			); }
		},

		new StorageHttpRoute() {
			@Override protected StreamStorage getStorage(String head) { return imagesStorage; }
		}

	); }

}
