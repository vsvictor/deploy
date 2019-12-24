package ic.struct.list



fun <Item> List (vararg items: Any) : List<Item> = List.Default(*items)

fun <Item> List (iterable: Iterable<Item>) : List<Item> = List.Default(iterable)


fun <Item> EditableList () : EditableList<Item> = EditableList.Default()

fun <Item> EditableList (iterable: Iterable<Item>) : EditableList<Item> = EditableList.Default(iterable)
