package ic.struct.order;


import static ic.struct.order.OrderRelation.*;


public interface Order<Item> {


	OrderRelation compare(Item a, Item b);


	Order<Integer> INT_ORDER = (aObject, bObject) -> {
		assert aObject != null;
		assert bObject != null;
		final int a = aObject;
		final int b = bObject;
		if (a < b) return A_LESS_THAN_B;
		if (a > b) return A_GREATER_THAN_B;
		if (a == b) return A_EQUALS_B;
		else throw new Error("a: " + a + ", b: " + b);
	};

	Order<Long> LONG_ORDER = (aObject, bObject) -> {
		assert aObject != null;
		assert bObject != null;
		final long a = aObject;
		final long b = bObject;
		if (a < b) return A_LESS_THAN_B;
		if (a > b) return A_GREATER_THAN_B;
		if (a == b) return A_EQUALS_B;
		else throw new Error("a: " + a + ", b: " + b);
	};

	Order<Float> FLOAT_ORDER = (aObject, bObject) -> {
		assert aObject != null;
		assert bObject != null;
		final float a = aObject;
		final float b = bObject;
		if (a < b) return A_LESS_THAN_B;
		if (a > b) return A_GREATER_THAN_B;
		if (a == b) return A_EQUALS_B;
		else throw new Error("a: " + a + ", b: " + b);
	};

	Order<String> ALPHABETIC_STRING_ORDER = (a, b) -> {
		assert a != null;
		assert b != null;
		final int comparison = a.compareTo(b);
		if (comparison < 0) return A_LESS_THAN_B;
		if (comparison > 0) return A_GREATER_THAN_B;
		if (a.equals(b))	return A_EQUALS_B;
		else throw new Error("a: " + a + ", b: " + b);
	};


}
