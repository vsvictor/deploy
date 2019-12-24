package ic.url;


import ic.annotations.Degenerate;
import ic.annotations.Hide;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
import ic.struct.list.List;
import ic.text.CharInput;
import ic.text.JoinText;
import ic.text.Text;


public @Degenerate class Url { @Hide private Url() {}


	public static Pair<String, Pair<String, String>> separateAuth(@NotNull String rawUrl) {

		final Text rawUrlText = new Text.FromString(rawUrl);

		final String url;
		final String urlUserName;
		final String urlPassword;

		if (rawUrlText.startsWith("https://")) {
			if (rawUrlText.contains('@')) {
				final CharInput urlIterator = rawUrlText.getIterator();
				urlIterator.read("https://".length());
				final Text credentials = urlIterator.readTill('@');
				if (credentials.contains(':')) {
					final CharInput credentialsIterator = credentials.getIterator();
					urlUserName = credentialsIterator.readTill(':').toString();
					urlPassword = credentialsIterator.read().toString();
				} else {
					urlUserName = credentials.toString();
					urlPassword = null;
				}
				final Text restUrl = urlIterator.read();
				url = new JoinText(new List.Default<>(
					new Text.FromString("https://"),
					restUrl
				)).getStringValue();
			} else {
				url = rawUrl;
				urlUserName = null;
				urlPassword = null;
			}
		} else {
			if (rawUrlText.contains('@')) {
				final CharInput urlIterator = rawUrlText.getIterator();
				final Text credentials = urlIterator.readTill('@');
				if (credentials.contains(':')) {
					final CharInput credentialsIterator = credentials.getIterator();
					urlUserName = credentialsIterator.readTill(':').toString();
					urlPassword = credentialsIterator.read().toString();
				} else {
					urlUserName = credentials.toString();
					urlPassword = null;
				}
				final Text restUrl = urlIterator.read();
				url = restUrl.toString();
			} else {
				url = rawUrl;
				urlUserName = null;
				urlPassword = null;
			}
		}

		return new Pair<>(url, new Pair<>(urlUserName, urlPassword));

	}


}
