package ic.color;


import kotlin.UInt;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"PointlessArithmeticExpression", "StaticInitializerReferencesSubClass"})


public abstract class Color {

	public abstract float getRed();

	public abstract float getGreen();

	public abstract float getBlue();

	public abstract float getAlpha();

	private static int floatToByte(float f) {
		int b = (int) (f * 255);
		if (b < 0) 		b = 0; 		else
		if (b > 255) 	b = 255;
		return b;
	}

	public int toArgb() {
		return (
			(floatToByte(getAlpha()) 	<< (8 * 3)) |
			(floatToByte(getRed()) 		<< (8 * 2)) |
			(floatToByte(getGreen()) 	<< (8 * 1)) |
			(floatToByte(getBlue()) 	<< (8 * 0))
		);
	}

	public int toRgba() {
		return (
			(floatToByte(getRed()) 		<< (8 * 3)) |
			(floatToByte(getGreen()) 	<< (8 * 2)) |
			(floatToByte(getBlue()) 	<< (8 * 1)) |
			(floatToByte(getAlpha()) 	<< (8 * 0))
		);
	}

	private static float byteToFloat(int b) {
		return b / 255f;
	}

	public static @NotNull Color fromArgb(int argb) {
		return new Constant(
			byteToFloat((argb >>> (8 * 2)) & 0x000000FF),
			byteToFloat((argb >>> (8 * 1)) & 0x000000FF),
			byteToFloat((argb >>> (8 * 0)) & 0x000000FF),
			byteToFloat((argb >>> (8 * 3)) & 0x000000FF)
		);
	}

	public static @NotNull Color fromRgba(int rgba) {
		return new Constant(
			byteToFloat((rgba >>> (8 * 3)) & 0x000000FF),
			byteToFloat((rgba >>> (8 * 2)) & 0x000000FF),
			byteToFloat((rgba >>> (8 * 1)) & 0x000000FF),
			byteToFloat((rgba >>> (8 * 0)) & 0x000000FF)
		);
	}

	public String toCss() {
		return "rgba(" + floatToByte(getRed()) + "," + floatToByte(getGreen()) + "," + floatToByte(getBlue()) + "," + getAlpha() + ")";
	}

	public static class Constant extends Color {

		private final float red; 	@Override public float getRed() 	{ return red; 	}
		private final float green;	@Override public float getGreen()	{ return green; }
		private final float blue; 	@Override public float getBlue() 	{ return blue; 	}
		private final float alpha;	@Override public float getAlpha()	{ return alpha; }

		public Constant(float red, float green, float blue, float alpha) {
			this.red = red;
			this.green = green;
			this.blue = blue;
			this.alpha = alpha;
		}

		public Constant(float red, float green, float blue) {
			this(red, green, blue, 1f);
		}

	}

	public static final Color TRANSPARENT = new Constant(0f, 0f, 0f, 0f);

	public static final Color BLACK 		= new Constant(0f, 0f, 0f);
	public static final Color WHITE 		= new Constant(1f, 1f, 1f);
	public static final Color GRAY 			= new Constant(.5f, .5f, .5f);
	public static final Color DARK_GRAY 	= new Constant(.25f, .25f, .25f);
	public static final Color LIGHT_GRAY 	= new Constant(.75f, .75f, .75f);
	public static final Color LIGHTER_GRAY 	= new Constant(.875f, .875f, .875f);
	public static final Color LIGHTEST_GRAY = new Constant(.9375f, .9375f, .9375f);

	public static final Color RED 		= new Constant(1f, 0f, 0f);
	public static final Color GREEN 	= new Constant(0f, 1f, 0f);
	public static final Color BLUE 		= new Constant(0f, 0f, 1f);

	public static final Color YELLOW 	= new Constant(1f, 1f, 0f);
	public static final Color MAGENTA 	= new Constant(1f, 0f, 1f);
	public static final Color CYAN 		= new Constant(0f, 1f, 1f);

	public static final Color DARK_MAGENTA 	= new Constant(.5f, 0f, .5f);
	public static final Color DARK_CYAN 	= new Constant(0f, .5f, .5f);

}
