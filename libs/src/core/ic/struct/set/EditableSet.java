package ic.struct.set;


import ic.event.Event;
import ic.interfaces.changeable.Changeable;
import ic.interfaces.action.Action1;
import ic.interfaces.emptiable.Emptiable;
import ic.interfaces.adder.SetAdder;
import ic.interfaces.remover.Remover1;
import ic.throwables.*;
import ic.struct.collection.Collection;
import org.jetbrains.annotations.NotNull;


public interface EditableSet<Item> extends

	CountableSet<Item>,
	SetAdder<Item>,
	Remover1<Item>,
	Emptiable,
	Changeable

{


	// Subclasses:

	abstract class BaseEditableSet<Item> implements EditableSet<Item> {


		// SetAdder<Item> implementation:

		protected abstract void implementAdd(Item item) throws AlreadyExists;

		final @Override public synchronized void addOrThrow(Item item) throws AlreadyExists {
			implementAdd(item);
			changeEvent.run();
		}


		// Remover1<Item> implementation:

		protected abstract void implementRemove(Item item) throws NotExists;

		final @Override public void removeOrThrow(Item item) throws NotExists {
			implementRemove(item);
			changeEvent.run();
		}


		// Changeable implementation:

		protected final Event.Trigger changeEvent = new Event.Trigger();

		@Override public Event.Trigger getChangeEvent() { return changeEvent; }


	}


	class FromJavaSet<Item> extends BaseEditableSet<Item> {

		private final java.util.Set<Item> javaSet;

		@Override public void implementForEach(Action1<Item> action) throws Break {
			for (Item item : javaSet) action.invoke(item);
		}

		@Override public synchronized boolean contains(Item item) {
			return javaSet.contains(item);
		}

		@Override public synchronized void implementRemove(Item item) throws NotExists {
			if (!javaSet.remove(item)) throw new NotExists();
		}

		@Override public synchronized void implementAdd(Item item) throws AlreadyExists {
			if (!javaSet.add(item)) throw new AlreadyExists();
		}

		@Override public synchronized void empty() {
			javaSet.clear();
		}

		public FromJavaSet(java.util.Set<Item> javaSet) {
			this.javaSet = javaSet;
		}

	}


	class Default<Item> extends FromJavaSet<Item> {

		public Default() {
			super(new java.util.HashSet<Item>());
		}

		public Default(@NotNull Collection<Item> items) {
			this();
			items.forEach(item -> addIfNotExists(item));
		}

		public Default(@NotNull Iterable<Item> items) {
			this();
			for (Item item : items) {
				addIfNotExists(item);
			}
		}

		public @SafeVarargs Default(@NotNull Item... items) {
			this();
			for (Item item : items) {
				addIfNotExists(item);
			}
		}

		@SuppressWarnings("unchecked") public Default(Item item) { this((Item[]) new Object[] { item }); }

	}


}
