package ic.struct.order;


public abstract class OrderRelation {

	public abstract OrderRelation getOpposite();

	private OrderRelation() {}

	public static final OrderRelation A_LESS_THAN_B = new OrderRelation() {
		@Override public OrderRelation getOpposite() { return A_GREATER_THAN_B; }
	};
	public static final OrderRelation A_GREATER_THAN_B	= new OrderRelation() {
		@Override public OrderRelation getOpposite() { return A_LESS_THAN_B; }
	};
	public static final OrderRelation A_EQUALS_B = new OrderRelation() {
		@Override public OrderRelation getOpposite() { return A_EQUALS_B; }
	};

}
