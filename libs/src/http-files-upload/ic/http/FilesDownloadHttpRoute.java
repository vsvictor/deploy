package ic.http;


import ic.annotations.Redirect;
import ic.network.http.*;
import ic.storage.StreamStorage;
import ic.struct.list.List;


abstract class FilesDownloadHttpRoute extends FolderHttpRoute.Static {

	@Override protected String getName() { return "download"; }

	protected abstract StreamStorage getStorage();

	@Override protected List<HttpRoute> initChildren() { return new List.Default<>(

		new StorageHttpRoute() {
			@Redirect @Override protected StreamStorage getStorage(String head) { return FilesDownloadHttpRoute.this.getStorage(); }
		}

	); }

}
