package ic.interfaces.condition;


import org.jetbrains.annotations.NotNull;
import ic.struct.order.Order;

import static ic.struct.order.OrderRelation.A_LESS_THAN_B;


public class LessThanCondition1<Item> implements Condition1<Item> {


	private final Order<Item> order;

	private final Item b;


	@Override public boolean is(Item a) {
		return order.compare(a, b) == A_LESS_THAN_B;
	}


	public LessThanCondition1(@NotNull Order<Item> order, @NotNull Item b) {

		this.order = order;
		this.b = b;

	}


}
