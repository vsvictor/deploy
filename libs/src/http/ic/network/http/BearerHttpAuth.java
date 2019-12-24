package ic.network.http;


import ic.text.Text;


public class BearerHttpAuth extends HttpAuth {

	public final String bearer;

	public BearerHttpAuth(Text credentials) {
		this.bearer = credentials.toString();
	}

}
