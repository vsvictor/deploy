package ic.interfaces.condition;


import ic.annotations.Narrowing;


public interface Condition1<Arg> extends SafeCondition1<Arg, RuntimeException> {

	@Narrowing @Override boolean is(Arg arg);

	class False<Arg> implements Condition1<Arg> {
		@Override public boolean is(Arg arg) { return false; }
	}

	class True<Arg> implements Condition1<Arg> {
		@Override public boolean is(Arg arg) { return true; }
	}

}
