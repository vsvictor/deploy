package ic.interfaces.remover;


import ic.annotations.Narrowing;
import ic.annotations.Repeat;
import ic.throwables.NotExists;


public interface Remover extends SafeRemover<RuntimeException> {


	@Narrowing @Repeat @Override void remove() throws NotExists.Runtime;

	@Narrowing @Repeat @Override void removeOrThrow() throws NotExists;

	@Narrowing @Repeat @Override void removeIfExists();


	abstract class BaseRemover extends BaseSafeRemover<RuntimeException> implements Remover {

		@Narrowing @Repeat @Override public void remove() throws NotExists.Runtime { super.remove(); }

		@Narrowing @Repeat @Override public void removeIfExists() { super.removeIfExists(); }

	}


}
