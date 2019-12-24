package d2.polpharma.services.images;


import ic.http.FilesUploadHttpRoute;
import ic.network.http.*;
import ic.storage.StreamStorage;
import ic.struct.list.List;
import ic.struct.set.CountableSet;
import lazyteam.adminka.ImagesAdminkaRoute;
import org.jetbrains.annotations.NotNull;

import static ic.Assets.resources;
import static ic.DataStorageKt.getDataStorage;


public class ImagesHttpsServer extends HttpsServer {

	@Override protected String getCertEmail() { return "Polpharma.Bot@gmail.com"; }

	@Override protected CountableSet<String> getDomainNames() { return new CountableSet.Default<>(
		"images.bd-polpharma.com.ua",
		"images.market-insights.com.ua"
	); }

	@Override protected String getDomainName(HttpRequest request) { return "images.market-insights.com.ua"; }

	@Override protected boolean toAllowCors() { return true; }

	@Override protected HttpRoute initRoute() { return new FolderHttpRoute.Static() {

		@Override protected String getName() { return ""; }

		@Override protected List<HttpRoute> initChildren() { return new List.Default<>(

			new TestResultImageGenerator(),

			new DailyReportImageGenerator(),
			new WeeklyReportImageGenerator(),
			new MonthlyReportImageGenerator(),

			new AchievementsImageGenerator(),

			new StorageHttpRoute() {
				@Override protected StreamStorage getStorage(String head) { return resources; }
			},

			new FolderHttpRoute.Static() {

				@Override protected String getName() { return "education"; }

				@Override protected List<HttpRoute> initChildren() {
					return new List.Default<>(
						new StorageHttpRoute() {
							@Override protected StreamStorage getStorage(String head) {
								return resources.get("education");
							}
						}
					);
				}
			},

			new ImagesAdminkaRoute() {
				@Override protected String getName() { return "upload"; }
				@Override protected StreamStorage initImagesStorage() {
					return getDataStorage().createFolderIfNotExists("upload");
				}
				@Override protected String getPassword() { return "1e7082351284c8af"; }
			},

			new FilesUploadHttpRoute() {
				@NotNull @Override protected String getName() { return "files"; }
				final StreamStorage storage = getDataStorage().createFolderIfNotExists("files");
				@Override protected StreamStorage getStorage() { return storage; }
			}

		); }

	}; }

}
