package ic.text;


import ic.struct.collection.Collection;
import kotlin.Pair;


public class ReplaceText extends Text.FromString {

	public static String replaceString(String sourceString, Collection<Pair<String, String>> replaceMap) {

		for (Pair<String, String> replacement : replaceMap) {
			sourceString = sourceString.replace(replacement.getFirst(), replacement.getSecond());
		}

		return sourceString;

	}

	public ReplaceText(Text sourceText, Collection<Pair<String, String>> replaceMap) {
		super(replaceString(sourceText.toString(), replaceMap));
	}

}
