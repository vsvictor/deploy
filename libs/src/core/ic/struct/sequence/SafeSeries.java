package ic.struct.sequence;


import ic.interfaces.action.Safe2Action1;
import ic.interfaces.getter.Safe2Getter;
import ic.throwables.Break;
import ic.throwables.End;
import org.jetbrains.annotations.NotNull;


public interface SafeSeries<Item, Thrown extends Throwable> extends Safe2Getter<Item, End, Thrown> {


	default void skip() throws Thrown {
		try {
			while (true) get();
		} catch (End end) {}
	}

	default void skip(int amount) throws End, Thrown {
		for (int i = 0; i < amount; i++) get();
	}


	default <ActionThrowable extends Throwable> void iterate(@NotNull Safe2Action1<Item, Break, ActionThrowable> action) throws Thrown, ActionThrowable {
		try {
			while (true) action.run(get());
		} catch (End | Break end) {}
	}


}
