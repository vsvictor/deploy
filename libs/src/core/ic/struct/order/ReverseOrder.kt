package ic.struct.order


class ReverseOrder<Item> (

	private val sourceOrder : Order<Item>

) : Order<Item> {

	override fun compare (a : Item, b : Item) : OrderRelation {

		return sourceOrder.compare(b, a)

	}

}