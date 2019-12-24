package ic.interfaces.remover;


import ic.throwables.NotExists;


public interface SafeRemover<Thrown extends Throwable> {


	void remove() throws NotExists.Runtime, Thrown;

	void removeOrThrow() throws NotExists, Thrown;

	void removeIfExists() throws Thrown;


	abstract class BaseSafeRemover<Thrown extends Throwable> implements SafeRemover<Thrown> {

		@Override public void remove() throws NotExists.Runtime, Thrown {
			try {
				removeOrThrow();
			} catch (NotExists notExists) { throw new NotExists.Runtime(notExists); }
		}

		@Override public void removeIfExists() throws Thrown {
			try {
				removeOrThrow();
			} catch (NotExists notExists) {}
		}

	}


}
