package ic.struct.list;


import ic.annotations.Valid;


public class SingleItemList<Item> implements List<Item> {

	private final Item item;
	
	@Override public int getLength() {
		return 1;
	}

	@Override public Item implementGet(@Valid int index) {
		return item;
	}

	public SingleItemList(Item item) {
		this.item = item;
	}
	
}
