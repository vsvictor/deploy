package ic.network.http;


import java.io.PrintStream;

import ic.annotations.CalledOnce;
import ic.annotations.Default;
import ic.mimetypes.MimeType;
import ic.network.IncompleteRequest;
import ic.network.SocketAddress;
import ic.network.SocketRRServer;
import ic.stream.*;
import ic.struct.collection.Collection;
import ic.struct.map.CountableMap;
import ic.struct.map.EditableMap;
import ic.text.*;
import ic.throwables.*;
import org.jetbrains.annotations.NotNull;

import static java.lang.Integer.parseInt;


/**
 * Starts HTTP server.
 */
public abstract class HttpServer extends SocketRRServer {


	@Override protected int getPort() {
		return 80;
	}


	protected abstract @CalledOnce HttpRoute initRoute();

	protected final HttpRoute httpRoute = initRoute();


	@Default protected boolean toAllowCors() { return false; }


	private static ByteSequence compileResponse(HttpResponse response, boolean allowCors, CountableMap<String, String> additionalHeaders) {

		final ByteBuffer byteBuffer = new ByteBuffer();

		final TextBuffer textBuffer = new TextBuffer(); {

			textBuffer.write("HTTP/1.1 " + response.getStatusMessage() + "\r\n");

			textBuffer.write(
				"Content-Type: " + response.getContentType().name + "; charset=" + response.getCharset().htmlName + "\r\n"
			);

			if (allowCors) {
				textBuffer.write(
					"Access-Control-Allow-Origin: *\r\n"
				);
			}

			additionalHeaders.getKeys().forEach(header -> textBuffer.write(
				header + ": " + additionalHeaders.get(header) + "\r\n"
			));

			{ final CountableMap<String, String> headers = response.getHeaders();
				headers.getKeys().forEach(header -> textBuffer.write(
					header + ": " + headers.get(header) + "\r\n"
				));
			}

			textBuffer.write("\r\n");

			byteBuffer.write(Charset.ISO_8859_1.textToByteArray(textBuffer));

		}

		byteBuffer.write(response.getBody());

		return byteBuffer;

	}


	private static class NotSupportedRequest extends RuntimeException {
		NotSupportedRequest(Throwable cause) { super(cause); }
	}

	private static class InvalidRequest extends RuntimeException {
		InvalidRequest(Throwable cause) { super(cause); }
	}


	@Default protected CountableMap<String, String> getAdditionalResponseHeaders() { return new CountableMap.Default<>(); }

	@Override protected ByteSequence handleThrowable(ByteSequence request, Throwable throwable) {

		final ByteBuffer byteBuffer = new ByteBuffer();
		throwable.printStackTrace(new PrintStream(byteBuffer.getOutputStream()));

		if (throwable instanceof NotSupportedRequest) {
			final HttpResponse response = new HttpResponse() {
				@Override public int getStatus() 			{ return STATUS_NOT_FOUND; 		}
				@Override public MimeType getContentType() 	{ return MimeType.TEXT; 		}
				@Override public ByteSequence getBody() 	{ return byteBuffer; 			}
			};
			return compileResponse(response, toAllowCors(), getAdditionalResponseHeaders());
		}

		if (throwable instanceof InvalidRequest) {
			final HttpResponse response = new HttpResponse() {
				@Override public int getStatus() 			{ return STATUS_BAD_REQUEST;	}
				@Override public MimeType getContentType() 	{ return MimeType.TEXT; 		}
				@Override public ByteSequence getBody() 	{ return byteBuffer; 			}
			};
			return compileResponse(response, toAllowCors(), getAdditionalResponseHeaders());
		}

		final HttpResponse response = new HttpResponse() {
			@Override public int getStatus() 			{ return STATUS_SERVICE_UNAVAILABLE; 	}
			@Override public MimeType getContentType() 	{ return MimeType.TEXT; 				}
			@Override public ByteSequence getBody() 	{ return byteBuffer; 					}
		};

		return compileResponse(response, toAllowCors(), getAdditionalResponseHeaders());

	}


	@Override protected final @NotNull ByteSequence getResponse(
		SocketAddress socketAddress, @NotNull ByteInput input
	) throws IncompleteRequest, IOException.Runtime {

		final String method;
		final String pathWithParams;
		final int contentLength;
		final CountableMap<String, String> headers; try {

			method = Charset.DEFAULT_HTTP.bytesToString(
				input.safeReadTill(
					Charset.DEFAULT_HTTP.stringToByteArray(
						" /"
					)
				)
			);

			pathWithParams = Charset.DEFAULT_UNIX.bytesToString(
				input.safeReadTill(
					Charset.DEFAULT_HTTP.stringToByteArray(
						" "
					)
				)
			);

			{
				final EditableMap<String, String> editableHeaders = new EditableMap.Default<>();
				new Collection.Default<>(
					new SplitText(
						new RemoveChar(
							Charset.DEFAULT_HTTP.bytesToText(
								input.readTill(
									Charset.DEFAULT_HTTP.stringToByteArray(
										"\n\r\n"
									)
								)
							).getIterator(),
							'\r'
						).read(),
						'\n'
					)
				).forEach(headerRow -> {
					final CharInput headerRowIterator = headerRow.getIterator();
					final String headerKey = headerRowIterator.readTill(": ").toString();
					final String headerValue = headerRowIterator.read().toString();
					editableHeaders.set(headerKey, headerValue);
				});
				if (editableHeaders.get("Authorization") == null) {
					editableHeaders.set("Authorization", editableHeaders.get("Auth"));
				}
				headers = editableHeaders;
			}

			{ String contentLengthValue = headers.get("Content-Length");
				if (contentLengthValue == null) {
					contentLengthValue = headers.get("content-length");
				}
				if (contentLengthValue == null) {
					contentLength = 0;
				} else {
					contentLength = parseInt(contentLengthValue);
				}
			}

		} catch (NotExists notFound) { throw new IncompleteRequest(notFound); }

		final ByteSequence body; try {
			body = input.read(contentLength);
		} catch (End end) {
			throw new IncompleteRequest(end);
		}

		final HttpResponse response; {
			if (method.equals("OPTIONS")) {
				response = new HttpResponse() {
					@Override public int getStatus() { return STATUS_NO_CONTENT; }
					@Override public CountableMap<String, String> getHeaders() { return new CountableMap.Default<>(
						"Access-Control-Allow-Methods", "OPTIONS, GET, HEAD, POST, PUT, DELETE",
						"Access-Control-Allow-Headers", "Accept, Accept-CH, Accept-Charset, Accept-Datetime, Accept-Encoding, Accept-Ext, Accept-Features, Accept-Language, Accept-Params, Accept-Ranges, Access-Control-Allow-Credentials, Access-Control-Allow-Headers, Access-Control-Allow-Methods, Access-Control-Allow-Origin, Access-Control-Expose-Headers, Access-Control-Max-Age, Access-Control-Request-Headers, Access-Control-Request-Method, Age, Allow, Alternates, Authentication-Info, Authorization, Auth, C-Ext, C-Man, C-Opt, C-PEP, C-PEP-Info, CONNECT, Cache-Control, Compliance, Connection, Content-Base, Content-Disposition, Content-Encoding, Content-ID, Content-Language, Content-Length, Content-Location, Content-MD5, Content-Range, Content-Script-Type, Content-Security-Policy, Content-Style-Type, Content-Transfer-Encoding, Content-Type, Content-Version, Cookie, Cost, DAV, DELETE, DNT, DPR, Date, Default-Style, Delta-Base, Depth, Derived-From, Destination, Differential-ID, Digest, ETag, Expect, Expires, Ext, From, GET, GetProfile, HEAD, HTTP-date, Host, IM, If, If-Match, If-Modified-Since, If-None-Match, If-Range, If-Unmodified-Since, Keep-Alive, Label, Last-Event-ID, Last-Modified, Link, Location, Lock-Token, MIME-Version, Man, Max-Forwards, Media-Range, Message-ID, Meter, Negotiate, Non-Compliance, OPTION, OPTIONS, OWS, Opt, Optional, Ordering-Type, Origin, Overwrite, P3P, PEP, PICS-Label, POST, PUT, Pep-Info, Permanent, Position, Pragma, ProfileObject, Protocol, Protocol-Query, Protocol-Request, Proxy-Authenticate, Proxy-Authentication-Info, Proxy-Authorization, Proxy-Features, Proxy-Instruction, Public, RWS, Range, Referer, Refresh, Resolution-Hint, Resolver-Location, Retry-After, Safe, Sec-Websocket-Extensions, Sec-Websocket-Key, Sec-Websocket-Origin, Sec-Websocket-Protocol, Sec-Websocket-Version, Security-Scheme, Server, Set-Cookie, Set-Cookie2, SetProfile, SoapAction, Status, Status-URI, Strict-Transport-Security, SubOK, Subst, Surrogate-Capability, Surrogate-Control, TCN, TE, TRACE, Timeout, Title, Trailer, Transfer-Encoding, UA-Color, UA-Media, UA-Pixels, UA-Resolution, UA-Windowpixels, URI, Upgrade, User-Agent, Variant-Vary, Vary, Version, Via, Viewport-Width, WWW-Authenticate, Want-Digest, Warning, Width, X-Content-Duration, X-Content-Security-Policy, X-Content-Type-Options, X-CustomHeader, X-DNSPrefetch-Control, X-Forwarded-For, X-Forwarded-Port, X-Forwarded-Proto, X-Frame-Options, X-Modified, X-OTHER, X-PING, X-PINGOTHER, X-Powered-By, X-Requested-With"
					); }
				};
			} else {
				try {
					response = httpRoute.implementRoute(
						socketAddress,
						new HttpRequest(
							pathWithParams,
							pathWithParams,
							method,
							headers,
							body
						),
						true
					);
				} catch (NotSupported notSupported) 	{ throw new NotSupportedRequest(notSupported);
				} catch (UnableToParse unableToParse) 	{ throw new InvalidRequest(unableToParse); }
			}
		}

		return compileResponse(response, toAllowCors(), getAdditionalResponseHeaders());

	}


}
