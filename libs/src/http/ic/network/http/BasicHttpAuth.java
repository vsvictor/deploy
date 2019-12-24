package ic.network.http;


// core imports:

import ic.text.Charset;
import ic.text.Text;
import ic.throwables.UnableToParse;

import static ic.util.Base64.base64StringToBytes;


public class BasicHttpAuth extends HttpAuth {


	public final String username;
	public final String password;


	/**
	 * Parses BasicHttpAuth from raw HTTP header
	 * @param base64Credentials value from headers
	 */
	public BasicHttpAuth(Text base64Credentials) throws UnableToParse {

		final String decoded = Charset.DEFAULT_HTTP.bytesToString(base64StringToBytes(base64Credentials.toString()));

		final String[] split = decoded.split(":");

		if (split.length != 2) throw new UnableToParse(decoded);

		this.username = split[0];
		this.password = split[1];

	}


}
