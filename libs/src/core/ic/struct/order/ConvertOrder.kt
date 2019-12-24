package ic.struct.order


class ConvertOrder<Item, SourceItem> (

	private val sourceOrder : Order<SourceItem>,

	private val itemConverter : (Item) -> SourceItem

) : Order<Item> {

	override fun compare (a: Item, b: Item) : OrderRelation {

		return sourceOrder.compare(itemConverter(a), itemConverter(b))

	}

}