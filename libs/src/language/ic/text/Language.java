package ic.text;


import org.jetbrains.annotations.NotNull;
import ic.struct.set.CountableSet;


public class Language {


	public final String code;

	private Language(@NotNull String code) {
		assert code != null;
		this.code = code;
	}


	@Override public int hashCode() {
		return code.hashCode();
	}

	@Override public boolean equals(Object obj) {
		try {
			return code.equals(((Language) obj).code);
		} catch (ClassCastException notALanguage) { return false; }
	}

	public static final Language ENGLISH 	= new Language("en");
	public static final Language UKRAINIAN 	= new Language("uk");
	public static final Language RUSSIAN 	= new Language("ru");


	public static final CountableSet<Language> LANGUAGES = new CountableSet.Default<>(
		ENGLISH,
		UKRAINIAN,
		RUSSIAN
	);


	public static Language byCode(@NotNull String code) {
		assert code != null;
		return LANGUAGES.find(language -> language.code.equals(code));
	}


}
