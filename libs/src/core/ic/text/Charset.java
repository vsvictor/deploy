package ic.text;


import org.jetbrains.annotations.NotNull;
import ic.annotations.Overload;
import ic.stream.ByteSequence;
import ic.throwables.IOException;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;


public class Charset {


	private final String javaName;
	public final String htmlName;


	public byte[] stringToByteArray(@NotNull String string) {
		try {
			return string.getBytes(javaName);
		} catch (UnsupportedEncodingException e) { throw new Error(e); }
	}

	public ByteSequence stringToByteSequence(@NotNull String string) {
		return new ByteSequence.FromByteArray(stringToByteArray(string));
	}

	public byte[] textToByteArray(@NotNull Text text) {
		return stringToByteArray(text.getStringValue());
	}

	public ByteSequence textToByteSequence(@NotNull Text text) {
		return new ByteSequence.FromByteArray(textToByteArray(text));
	}

	public String bytesToString(@NotNull byte[] bytes) {
		try {
			return new String(bytes, javaName);
		} catch (UnsupportedEncodingException e) { throw new Error(e); }
	}

	@Overload public String bytesToString(@NotNull ByteSequence byteSequence) {
		return bytesToString(byteSequence.toByteArray());
	}

	@Overload public String bytesToString(@NotNull InputStream inputStream) {
		try {
			return bytesToString(inputStream.readAllBytes());
		} catch (java.io.IOException e) { throw new IOException.Runtime(e); }
	}

	public Text bytesToText(@NotNull ByteSequence byteSequence) {
		return new Text.FromString(bytesToString(byteSequence));
	}


	public String encodeUrl(String string) {
		try {
			return URLEncoder.encode(string, javaName);
		} catch (UnsupportedEncodingException e) { throw new Error(e); }
	}

	public String decodeUrl(String string) {
		try {
			return URLDecoder.decode(string, javaName);
		} catch (UnsupportedEncodingException e) { throw new Error(e); }
	}


	private Charset(String javaName, String htmlName) {
		this.javaName = javaName;
		this.htmlName = htmlName;
	}


	public static final Charset ISO_8859_1 		= new Charset(	"ISO-8859-1", 	"iso-8859-1"	);
	public static final Charset UTF_8 			= new Charset(	"UTF-8", 		"utf-8"			);
	public static final Charset UTF_16 			= new Charset(	"UTF-16",		"utf-16"		);
	public static final Charset WINDOWS_1251 	= new Charset(	"windows-1251",	"windows-1251"	);

	public static final Charset DEFAULT_UNIX 	= UTF_8;
	public static final Charset DEFAULT_HTTP 	= UTF_8;


}
