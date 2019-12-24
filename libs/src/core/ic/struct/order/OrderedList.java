package ic.struct.order;


import ic.interfaces.emptiable.Emptiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ic.annotations.Valid;

import ic.interfaces.getter.Getter;
import ic.throwables.AlreadyExists;
import ic.throwables.NotExists;


import ic.struct.list.EditableList;
import ic.struct.list.List;

import static ic.struct.order.OrderRelation.A_GREATER_THAN_B;
import static ic.struct.order.OrderRelation.A_LESS_THAN_B;
import static ic.throwables.NotExists.NOT_EXISTS;


public class OrderedList<Item> implements List<Item>, Emptiable {


	private final EditableList<Item> editableList;

	@Override public Item implementGet(int index) {
		return editableList.get(index);
	}

	@Override public int getLength() {
		return editableList.getLength();
	}


	private class FoundIndex {
		final int index;
		final @Nullable Item item;
		FoundIndex(int index, @Nullable Item item) {
			this.index = index;
			this.item = item;
		}
	}


	private @NotNull FoundIndex implementFindIndex(Order1<Item> searcher) {

		int intervalStart = 0;
		int intervalEnd = editableList.getCount();

		while (true) {

			final int intervalLength = intervalEnd - intervalStart;

			final int middleIndex; {

				if (intervalLength < 0) throw new Error("intervalLength == " + intervalLength); else

				if (intervalLength == 0) return new FoundIndex(intervalStart, null);

				else {
					middleIndex = intervalStart + intervalLength / 2;
				}

			}

			final Item middleItem = editableList.get(middleIndex);

			{ final OrderRelation targetToMiddleItem = searcher.compare(middleItem);
				if (targetToMiddleItem == A_LESS_THAN_B) {
					intervalEnd = middleIndex;
				} else
				if (targetToMiddleItem == A_GREATER_THAN_B) {
					intervalStart = middleIndex + 1;
				}
				else return new FoundIndex(middleIndex, middleItem);
			}

		}

	}

	public int findIndex(Order1<Item> searcher) throws NotExists {
		final FoundIndex foundIndex = implementFindIndex(searcher);
		if (foundIndex.item == null) throw NOT_EXISTS;
		return foundIndex.index;
	}


	public Item get(Order1<Item> searcher) throws NotExists {
		final FoundIndex foundIndex = implementFindIndex(searcher);
		if (foundIndex.item == null) throw new NotExists();
		return foundIndex.item;
	}


	public boolean contains(Order1<Item> searcher) {
		return implementFindIndex(searcher).item != null;
	}

	public void add(Order1<Item> searcher, Getter<Item> itemGetter) throws AlreadyExists {
		final FoundIndex foundIndex = implementFindIndex(searcher);
		if (foundIndex.item != null) throw new AlreadyExists();
		else editableList.insert(foundIndex.index, itemGetter.get());
	}

	public void remove(Order1<Item> searcher) throws NotExists {
		final FoundIndex foundIndex = implementFindIndex(searcher);
		if (foundIndex.item == null) throw new NotExists();
		else editableList.remove(foundIndex.index);
	}

	@Override public void empty() {
		editableList.empty();
	}

	public OrderedList(@Valid EditableList<Item> implementation) {
		this.editableList = implementation;
	}

	public OrderedList() {
		this(new EditableList.Default<Item>());
	}




}
