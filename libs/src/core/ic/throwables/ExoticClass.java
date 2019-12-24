package ic.throwables;


@SuppressWarnings("serial")


public class ExoticClass extends RuntimeException {

	public final Class exoticClass;

	public ExoticClass(Class exoticClass) {
		super(exoticClass.getName());
		this.exoticClass = exoticClass;
	}

}
