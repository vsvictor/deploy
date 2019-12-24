package ic.struct.order;


import ic.event.Event;
import ic.interfaces.action.Action1;
import org.jetbrains.annotations.NotNull;
import ic.annotations.Redirect;
import ic.struct.collection.Collection;
import ic.throwables.*;
import ic.struct.set.EditableSet;


public interface OrderedEditableSet<Item> extends OrderedCountableSet<Item>, EditableSet<Item> {


	abstract class BaseOrderedEditableSet<Item> implements OrderedEditableSet<Item> {

		// EditableSet<Item> implementation:

		protected abstract void implementAdd(Item item) throws AlreadyExists;
		protected abstract void implementRemove(Item item) throws NotExists;

		private final BaseEditableSet<Item> baseEditableSet = new BaseEditableSet<Item>() {
			@Redirect @Override public void empty() 											{ BaseOrderedEditableSet.this.empty(); 					}
			@Redirect @Override public void implementAdd(Item item) throws AlreadyExists 		{ BaseOrderedEditableSet.this.implementAdd(item); 		}
			@Redirect @Override public void implementRemove(Item item) throws NotExists 		{ BaseOrderedEditableSet.this.implementRemove(item); 	}
			@Redirect @Override public void implementForEach(Action1<Item> action) throws Break { BaseOrderedEditableSet.this.implementForEach(action); }
			@Redirect @Override public boolean contains(Item item) 								{ return BaseOrderedEditableSet.this.contains(item); 	}
		};

		@Redirect @Override public final void addOrThrow(Item item) throws AlreadyExists 	{ baseEditableSet.addOrThrow(item); 	}
		@Redirect @Override public final void add(Item item) 								{ baseEditableSet.add(item); 			}
		@Redirect @Override public final void addIfNotExists(Item item) 					{ baseEditableSet.addIfNotExists(item); }
		@Redirect @Override public final void addAll(Iterable<Item> items) 					{ baseEditableSet.addAll(items); 		}
		@Redirect @Override public final void removeOrThrow(Item item) throws NotExists 	{ baseEditableSet.removeOrThrow(item); 	}
		@Redirect @Override public final void remove(Item item) throws NotExists.Runtime 	{ baseEditableSet.remove(item); 		}
		@Redirect @Override public final void removeIfExists(Item item) 					{ baseEditableSet.removeIfExists(item); }

		@Override public Event getChangeEvent() { return baseEditableSet.getChangeEvent(); }

	}


	class Default<Item> extends BaseOrderedEditableSet<Item> {


		private final @NotNull Order<Item> order;

		private final OrderedList<Item> orderedList = new OrderedList<Item>();


		// List implementation:

		@Redirect @Override public Item implementGet(int index) { return orderedList.get(index); }

		@Override public int getLength() {
			return orderedList.getLength();
		}

		// EditableSet<Item> implementation:

		@Override public boolean contains(@NotNull Item item) {
			return orderedList.contains(new Order1.FromItemAndOrder<Item>(item, order));
		}

		@Override public void implementAdd(final Item item) throws AlreadyExists {
			assert item != null;
			orderedList.add(
				new Order1.FromItemAndOrder<Item>(item, order),
				() -> item
			);
		}

		@Override public void implementRemove(Item item) throws NotExists {
			orderedList.remove(
				new Order1.FromItemAndOrder<Item>(item, order)
			);
		}

		@Redirect @Override public void implementForEach(Action1<Item> action) throws Break { orderedList.forEach(action); }

		@Override public void empty() {
			orderedList.empty();
		}


		// Constructors:

		public Default(@NotNull Order<Item> order) {
			this.order = order;
		}

		public Default(@NotNull Order<Item> order, @NotNull Collection<Item> items) {
			this(order);
			items.forEach(this::addIfNotExists);
		}

		public Default(@NotNull Order<Item> order, @NotNull Iterable<Item> items) {
			this(order);
			for (Item item : items) {
				addIfNotExists(item);
			}
		}

	}


}
