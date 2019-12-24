package ic.color;


import org.jetbrains.annotations.NotNull;

public abstract class Blend {


	public abstract Color apply(Color destination, Color source);


	private Blend() {}


	public static final Blend ADD = new Blend() { @Override public Color apply(@NotNull final Color background, @NotNull final Color foreground) {

		final float foregroundAlpha = foreground.getAlpha();

		return new Color() {

			@Override public float getRed() 	{ return background.getRed() 	+ foreground.getRed() 	* foregroundAlpha; }
			@Override public float getGreen() 	{ return background.getGreen() 	+ foreground.getGreen() * foregroundAlpha; }
			@Override public float getBlue() 	{ return background.getBlue() 	+ foreground.getBlue() 	* foregroundAlpha; }

			@Override public float getAlpha() { return 1 - (1 - background.getAlpha()) * (1 - foregroundAlpha); }
		};

	} };


	public static final Blend MULTIPLY = new Blend() { @Override public Color apply(@NotNull final Color background, @NotNull final Color foreground) {

		return new Color() {

			@Override public float getRed() 	{ return background.getRed() 	* foreground.getRed(); 		}
			@Override public float getGreen() 	{ return background.getGreen() 	* foreground.getGreen(); 	}
			@Override public float getBlue() 	{ return background.getBlue() 	* foreground.getBlue();		}

			@Override public float getAlpha() { return background.getAlpha() * foreground.getAlpha(); }

		};

	} };


	public static final Blend MIX = new Blend() { @Override public Color apply(@NotNull final Color background, @NotNull final Color foreground) {

		final float foregroundAlpha = foreground.getAlpha();

		return new Color() {

			@Override public float getRed() 	{ return background.getRed() 	* (1 - foregroundAlpha) + foreground.getRed() 	* foregroundAlpha; }
			@Override public float getGreen() 	{ return background.getGreen() 	* (1 - foregroundAlpha) + foreground.getGreen() * foregroundAlpha; }
			@Override public float getBlue() 	{ return background.getBlue()	* (1 - foregroundAlpha) + foreground.getBlue()	* foregroundAlpha; }

			@Override public float getAlpha() { return 1 - (1 - background.getAlpha()) * (1 - foregroundAlpha); }

		};

	} };


}
