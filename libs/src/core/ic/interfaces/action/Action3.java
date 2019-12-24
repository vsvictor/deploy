package ic.interfaces.action;


import ic.annotations.Repeat;


public interface Action3<Arg1, Arg2, Arg3> extends SafeAction3<Arg1, Arg2, Arg3, RuntimeException> {

	@Repeat @Override void run(Arg1 arg1, Arg2 arg2, Arg3 arg3);

}
