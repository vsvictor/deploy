package ic.struct.list


import ic.annotations.Valid


class ConvertList<Item, SourceItem>(

	private val sourceList: List<SourceItem>,

	private val itemConverter: (SourceItem) -> Item

) : List<Item> {

	override val length get() = sourceList.count

	override fun implementGet (@Valid index: Int): Item {
		return itemConverter(sourceList.get(index))
	}

}
