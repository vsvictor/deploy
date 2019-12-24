package ic.struct.order;



public interface Order1<Item> {


	OrderRelation compare(Item b);


	class FromItemAndOrder<Item> implements Order1<Item> {

		private final Item a;
		private final Order<Item> order;

		@Override public OrderRelation compare(Item b) {
			return order.compare(a, b);
		}

		public FromItemAndOrder(Item a, Order<Item> order) {
			this.a = a;
			this.order = order;
		}

	}


}
