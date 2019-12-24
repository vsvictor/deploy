package ic.struct.sequence;


public interface SafeSequence<Item, Thrown extends Throwable> {

	/**
	 * This method
	 * @return new iterator every time. Each iterator get independent and have its internal data of "where it get currently".
	 */
	SafeSeries<Item, Thrown> getIterator();

	abstract class BaseSafeSequence<Item, Thrown extends Throwable> implements SafeSequence<Item, Thrown> {

	}

}
