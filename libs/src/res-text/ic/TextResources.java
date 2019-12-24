package ic;



import ic.annotations.Degenerate;
import ic.annotations.Hide;
import ic.storage.StreamStorage;
import ic.struct.collection.Collection;
import ic.text.Charset;
import ic.text.ReplaceText;
import ic.text.Text;
import ic.throwables.NotExists;
import kotlin.Pair;

import static ic.Assets.resources;


public @Degenerate class TextResources { @Hide private TextResources() {}


	private static final StreamStorage textResources = resources.getFolder("text");


	public static Text getResText(String path) {
		try {
			return Charset.DEFAULT_UNIX.bytesToText(textResources.recursiveReadOrThrow(path + ".txt"));
		} catch (NotExists notExists) { throw new NotExists.Runtime("resText: " + path); }
	}

	public static Text getResText(String path, Collection<Pair<String, String>> replaceMap) {
		return new ReplaceText(getResText(path), replaceMap);
	}

	public static String getResString(String path) {
		return getResText(path).getStringValue();
	}


}
