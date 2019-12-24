package ic.struct.list;


import ic.annotations.CalledManyTimes;
import ic.annotations.ToOverride;


public abstract class ConvertArray<Item, SourceItem> implements Array<Item> {


	protected abstract Array<SourceItem> getSourceArray();


	@CalledManyTimes
	@ToOverride protected abstract Item convert(SourceItem sourceItem);

	@CalledManyTimes
	@ToOverride protected abstract SourceItem convertBack(Item sourceItem);


	@Override public int getCount() {
		return getSourceArray().getCount();
	}

	@Override public Item implementGet(int index) {
		return convert(getSourceArray().get(index));
	}

	@Override public void implementSet(int index, Item value) throws IndexOutOfBounds.Runtime {
		getSourceArray().set(index, convertBack(value));
	}


	public static abstract class FromSourceArray<Item, SourceItem> extends ConvertArray<Item, SourceItem> {

		private final Array<SourceItem> sourceArray;

		@Override public Array<SourceItem> getSourceArray() {
			return sourceArray;
		}

		public FromSourceArray(Array<SourceItem> sourceArray) {
			this.sourceArray = sourceArray;
		}

	}


}
