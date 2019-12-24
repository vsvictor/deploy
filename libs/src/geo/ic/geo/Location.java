package ic.geo;


import ic.annotations.Necessary;
import ic.interfaces.getter.Getter1;
import ic.interfaces.setter.Setter1;
import ic.serial.stringmap.StringMapSerializable;
import ic.throwables.UnableToParse;
import org.jetbrains.annotations.NotNull;

import static java.lang.Double.parseDouble;


public class Location implements StringMapSerializable {


	public final double latitude, longitude;


	// StringMapSerializable implementation:

	@Override public Class getClassToDeclare() { return Location.class; }

	@Override public void serialize(Setter1<String, String> output) {
		output.set("latitude", Double.toString(latitude));
		output.set("longitude", Double.toString(longitude));
	}

	@Necessary public Location(Getter1<String, String> input) throws UnableToParse {
		try {
			this.latitude = parseDouble(input.getNotNull("latitude"));
			this.longitude = parseDouble(input.getNotNull("longitude"));
		} catch (NumberFormatException numberFormatException) { throw new UnableToParse(numberFormatException); }
	}


	// Constructor:

	public Location(double latitude, double longitude) {

		this.latitude = latitude;
		this.longitude = longitude;

	}


	// Static:

	public static final double EARTH_RADIUS = 6371000;

	public static float getDistance(@NotNull Location a, @NotNull Location b) {

		double dLat = Math.toRadians(b.latitude - a.latitude);
		double dLng = Math.toRadians(b.longitude - a.longitude);

		double square = (
			Math.sin(dLat/2) * Math.sin(dLat/2) +
			Math.cos(Math.toRadians(a.latitude)) * Math.cos(Math.toRadians(b.latitude)) *
			Math.sin(dLng/2) * Math.sin(dLng/2)
		);

		return (float) (
			EARTH_RADIUS * 2 * Math.atan2(Math.sqrt(square), Math.sqrt(1 - square))
		);

	}


}
