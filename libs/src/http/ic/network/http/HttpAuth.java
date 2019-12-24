package ic.network.http;


// util imports:

import org.jetbrains.annotations.NotNull;
import ic.throwables.NotExists;
import ic.throwables.UnableToParse;


// core imports:

import ic.text.CharInput;
import ic.text.Text;


/**
 * Represents various HTTP authorization standards
 */
public abstract class HttpAuth {


	/**
	 * Parses HttpAuth from raw HTTP header
	 * @param header HTTP header "Authorization" value
	 */
	public static @NotNull <Type extends HttpAuth> Type parse(@NotNull String header) throws UnableToParse {

		final CharInput iterator = new Text.FromString(header).getIterator();

		try {

			final String type = iterator.safeReadTill(' ').toString();

			if (type.equals("Basic")) {
				@SuppressWarnings("unchecked")
				final Type httpAuth = (Type) new BasicHttpAuth(iterator.read());
				return httpAuth;
			} else

			if (type.equals("Bearer")) {
				@SuppressWarnings("unchecked")
				final Type bearerAuth = (Type) new BearerHttpAuth(iterator.read());
				return bearerAuth;
			}

			else throw new UnableToParse("Unknown type " + type);

		} catch (NotExists notFound) { throw new UnableToParse("Can't parse Authentication \"" + header + "\""); }

	}


}
