package d2.polpharma.services.db.api;


import d2.polpharma.services.db.api.admin.AdminRoute;
import d2.polpharma.services.db.api.ophtalm.OphtalmApiRoute;
import d2.polpharma.services.db.api.pharm.PharmApiRoute;
import ic.network.http.*;
import ic.storage.Directory;
import ic.storage.Storage;
import ic.storage.StreamStorage;
import ic.struct.list.List;
import ic.struct.set.CountableSet;
import ic.throwables.NotSupported;
import org.jetbrains.annotations.NotNull;

import static d2.polpharma.services.db.PolpharmaRedisDataBase.POLPHARMA_REDIS_DATABASE;
import java.util.Objects;
import static ic.Assets.resources;
import static ic.ServiceAppKt.getTier;


public class HttpServer extends HttpsServer {


	/*@Override protected boolean toLogTraffic() {
		return true;
	}*/

	@Override protected String getCertEmail() { return "Polpharma.Bot@gmail.com"; }

	@Override protected CountableSet<String> getDomainNames() throws NotSupported {
		final String tier = getTier();
		if (Objects.equals(tier, "prod")) return new CountableSet.Default<>(
			"bd-polpharma.com.ua",
			"ophtik.com.ua"
		); else
		if (Objects.equals(tier, "stage")) return new CountableSet.Default<>(
			"stage.bd-polpharma.com.ua",
			"test-ssl-alex.d2.digital"
		);
		else throw new NotSupported();
	}

	@Override protected String getDomainName(HttpRequest request) throws NotSupported {
		final String tier = getTier();
		if (Objects.equals(tier, "prod")) return "bd-polpharma.com.ua"; else
		if (Objects.equals(tier, "stage")) return "stage.bd-polpharma.com.ua";
		else throw new NotSupported();
	}

	@Override protected HttpMode getHttpMode() { return HttpMode.DUPLICATE; }

	@Override protected boolean toAllowCors() { return true; }

	@Override protected HttpRoute initRoute() { return new FolderHttpRoute.Final(

		new FolderHttpRoute.Static() {

			@NotNull @Override protected String getName() {
				return "api";
			}

			@NotNull @Override protected List<HttpRoute> initChildren() {
				return new List.Default<>(

					new OphtalmApiRoute(),
					new PharmApiRoute()

				);
			}

		},

		new AdminRoute(),

		new FolderHttpRoute.Static() {
			@NotNull @Override protected String getName() { return "ophtalm"; }
			@NotNull @Override protected List<HttpRoute> initChildren() { return new List.Default<HttpRoute>(

				new StorageHttpRoute() { @Override protected StreamStorage getStorage(String head) {
					return Directory.createIfNotExists("/public/ophtalm/build");
				} }

			); }
		},

		new GeocodeApiMethod() {
			final Storage storage = POLPHARMA_REDIS_DATABASE.createFolderIfNotExists("geocode-cache"); @Override protected Storage getStorage() { return storage; }
		},

		new StorageHttpRoute() {
			@Override protected StreamStorage getStorage(String head) { return resources; }
		},

		new Migrate()

	); }


}
