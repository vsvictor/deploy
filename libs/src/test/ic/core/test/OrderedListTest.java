package ic.core.test;


import ic.struct.order.Order1;
import ic.struct.order.OrderedList;
import ic.test.Test;
import ic.text.CharOutput;
import ic.throwables.AlreadyExists;

import static ic.struct.order.Order.INT_ORDER;


public class OrderedListTest extends Test { @Override public void run(CharOutput charOutput) {

	final OrderedList<Integer> orderedList = new OrderedList<>();

	assert orderedList.getCount() == 0;

	try {
		orderedList.add(new Order1.FromItemAndOrder<>(0, INT_ORDER), () -> 0);
	} catch (AlreadyExists alreadyExists) { assert false; }

	assert orderedList.getCount() == 1;

	assert orderedList.get(0) == 0;

	try {
		orderedList.add(new Order1.FromItemAndOrder<>(1, INT_ORDER), () -> 1);
	} catch (AlreadyExists alreadyExists) { assert false; }

	try {
		orderedList.add(new Order1.FromItemAndOrder<>(-1, INT_ORDER), () -> -1);
	} catch (AlreadyExists alreadyExists) { assert false; }

	assert orderedList.getCount() == 3;

	assert orderedList.get(0) == -1;
	assert orderedList.get(1) == 0;
	assert orderedList.get(2) == 1;

} }
