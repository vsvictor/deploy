package ic.network.http;


import ic.annotations.Default;
import ic.annotations.ToOverride;
import ic.network.SocketAddress;
import ic.stream.ByteSequence;
import ic.struct.map.CountableMap;
import ic.struct.set.CountableSet;
import ic.throwables.NotSupported;
import ic.throwables.UnableToParse;
import ic.throwables.WrongValue;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;

import static ic.cmd.UserConsole.USER_CONSOLE;
import static ic.network.ssl.CertKt.generateCertJks;


public abstract class HttpsServer extends HttpServer {


	protected abstract String getCertEmail() throws NotSupported;
	protected abstract CountableSet<String> getDomainNames() throws NotSupported;

	protected abstract String getDomainName(@Nullable HttpRequest request) throws NotSupported;


	@ToOverride
	@Override protected int getPort() { return 443; }

	@ToOverride
	protected int getHttpPort() { return 80; }

	@ToOverride
	protected int getFallbackHttpPort() { return 8192; }


	private volatile boolean fallback;


	@ToOverride
	protected ByteSequence getCertificateKeyStore() throws NotSupported {
		return generateCertJks(
			getCertEmail(),
			getDomainNames(),
			getCertificateKeyStorePassword(),
			getCertificateKeyPassword()
		);
	}

	@ToOverride
	protected String getCertificateKeyStorePassword() { return "ilovejava"; }

	@ToOverride
	protected String getCertificateKeyPassword() { return "ilovejava"; }


	@Override protected synchronized ServerSocket createServerSocket(int port) throws IOException {

		try {

			KeyStore keyStore = KeyStore.getInstance("JKS");
			keyStore.load(
				getCertificateKeyStore().getIterator().toInputStream(),
				getCertificateKeyStorePassword().toCharArray()
			);

			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("PKIX");
			keyManagerFactory.init(keyStore, getCertificateKeyPassword().toCharArray());
			KeyManager[] km = keyManagerFactory.getKeyManagers();

			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("PKIX");
			trustManagerFactory.init(keyStore);
			TrustManager[] tm = trustManagerFactory.getTrustManagers();

			SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
			sslContext.init(km, tm, null);
			SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
			return sslServerSocketFactory.createServerSocket(port);

		} catch (CertificateException | NoSuchAlgorithmException | UnrecoverableKeyException | KeyStoreException | KeyManagementException e) {

			throw new RuntimeException(e);

		} catch (NotSupported notSupported) {

			USER_CONSOLE.writeLine("SSL certificate not supported on this machine. Fallback to HTTP");

			fallback = true;
			return super.createServerSocket(getFallbackHttpPort());

		}

	}

	@Override protected void onSocketAccept(Socket socket) throws IOException {

		if (socket instanceof SSLSocket) { final SSLSocket sslSocket = (SSLSocket) socket;
			sslSocket.setEnabledCipherSuites(sslSocket.getSupportedCipherSuites());
			sslSocket.startHandshake();
		}

	}


	private HttpServer httpServer;

	public static class HttpMode {
		private HttpMode() {}
		public static final HttpMode NONE = new HttpMode();
		public static final HttpMode REDIRECT = new HttpMode();
		public static final HttpMode DUPLICATE = new HttpMode();
	}

	@Default protected HttpMode getHttpMode() { return HttpMode.REDIRECT; }

	@Override public synchronized void implementStart() {
		super.implementStart();
		if (getHttpMode() != HttpMode.NONE && !fallback) {
			httpServer = new HttpServer() {
				@Override protected int getPort() { return getHttpPort(); }
				@Override protected boolean toAllowCors() { return HttpsServer.this.toAllowCors(); }
				@Override protected HttpRoute initRoute() { return new HttpRoute() {
					@Override public HttpResponse implementRoute(SocketAddress socketAddress, final HttpRequest request, boolean root) throws NotSupported, UnableToParse {
						final HttpMode httpMode = getHttpMode();
						if (httpMode == HttpMode.REDIRECT) {
							final String domainName = getDomainName(request);
							return new HttpResponse() {
								@Override public int getStatus() { return STATUS_MOVED_PERMANENTLY; }
								@Override public CountableMap<String, String> getHeaders() { return new CountableMap.Default<>(
									"Location", "https://" + domainName + ":" + HttpsServer.this.getPort() + "/" + request.path
								); }
							};
						} else
						if (httpMode == HttpMode.DUPLICATE) {
							return HttpsServer.this.httpRoute.implementRoute(socketAddress, request, root);
						}
						else throw new WrongValue.Runtime(httpMode.toString());
					}
				}; }
			};
			httpServer.start();
		}
	}

	@Override public synchronized void implementStop() {
		super.implementStop();
		if (httpServer != null) {
			httpServer.stop();
		}
	}


}
