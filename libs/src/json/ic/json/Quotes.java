package ic.json;


import ic.text.Text;
import org.json.JSONObject;


public class Quotes {

	public static String quote(String string) {
		return JSONObject.quote(string);
	}

	public static Text quote(Text string) {
		return new Text.FromString(JSONObject.quote(string.toString()));
	}

}
