package ic.serial.json;


import ic.serial.Serializable;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;


public interface JsonSerializable extends Serializable {


	String KEY_CLASS = "class";

	void serialize(@NotNull JSONObject json) throws JSONException;


}
