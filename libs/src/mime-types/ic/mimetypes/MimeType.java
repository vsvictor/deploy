package ic.mimetypes;


import ic.struct.collection.Collection;
import org.jetbrains.annotations.NotNull;


public class MimeType {


	public final String extension;

	public final String name;

	private MimeType(String extension, String name) {
		this.extension = extension;
		this.name = name;
	}


	public static final MimeType TEXT 	= new MimeType("txt", 	"text/plain");
	public static final MimeType HTML 	= new MimeType("html", 	"text/html");
	public static final MimeType CSS	= new MimeType("css", 	"text/css");
	public static final MimeType JS		= new MimeType("js", 	"text/javascript");
	public static final MimeType JSON 	= new MimeType("json", 	"application/json");
	public static final MimeType CSV 	= new MimeType("csv", 	"text/csv");
	public static final MimeType PNG 	= new MimeType("png", 	"image/png");
	public static final MimeType JPG 	= new MimeType("jpg", 	"image/jpg");
	public static final MimeType JPEG 	= new MimeType("jpeg", 	"image/jpeg");
	public static final MimeType SVG 	= new MimeType("svg", 	"image/svg+xml");
	public static final MimeType ICO 	= new MimeType("ico", 	"image/image/x-icon");
	public static final MimeType TTF 	= new MimeType("ttf", 	"font/ttf");
	public static final MimeType WOFF 	= new MimeType("woff", 	"font/woff");
	public static final MimeType WOFF2 	= new MimeType("woff2", "font/woff2");
	public static final MimeType MP3 	= new MimeType("mp3", 	"audio/mpeg");
	public static final MimeType OGG 	= new MimeType("ogg", 	"audio/ogg");
	public static final MimeType OGA 	= new MimeType("oga", 	"audio/ogg");
	public static final MimeType XML 	= new MimeType("xml", 	"text/xml");
	public static final MimeType PDF 	= new MimeType("pdf", 	"application/pdf");
	public static final MimeType SH 	= new MimeType("sh", 	"text/x-shellscript");


	public static final Collection<MimeType> MIME_TYPES = new Collection.Default<>(
		TEXT,
		HTML,
		CSS,
		JS,
		JSON,
		CSV,
		PNG,
		JPG,
		JPEG,
		SVG,
		ICO,
		TTF,
		WOFF,
		WOFF2,
		MP3,
		OGG,
		OGA,
		XML,
		PDF,
		SH
	);

	public static @NotNull MimeType byName(@NotNull String name) {
		return MIME_TYPES.find(mimeType -> mimeType.name.equals(name));
	}


}
