package ic.struct.collection


import ic.interfaces.action.Action1


class JoinCollection<Item>(private val parts: Collection<Collection<Item>>) : Collection<Item> {


	override fun implementForEach(action: Action1<Item>) {
		parts.forEach { part -> part.forEach(action) }
	}

	@SafeVarargs
	constructor(vararg parts: Collection<Item>) : this(Collection.FromArray<Collection<Item>>(parts))

	constructor(part1: Collection<Item>, part2: Collection<Item>) : this(*arrayOf(part1, part2))


}
