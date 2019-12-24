package ic.struct.list;


import ic.annotations.NotZero;
import ic.annotations.Positive;


public abstract class DivideByPortionsList<Item> implements List<List<Item>> {

	private final int portionSize;

	protected DivideByPortionsList(@NotZero @Positive int portionSize) {
		{ // Checking arguments:
			assert portionSize > 0;
		}
		this.portionSize = portionSize;
	}

	protected abstract List<Item> getSourceList();

	@Override public int getLength() {
		final int sourceListSize = getSourceList().getLength();
		int result = sourceListSize / portionSize;
		if (sourceListSize % portionSize != 0) result++;
		return result;
	}

	@Override public List<Item> implementGet(final int portionIndex) {
		return new List<Item>() {
			@Override public int getLength() {
				final int sourceListSize = getSourceList().getCount();
				final int remainder = sourceListSize % portionSize;

				int portionsCount = sourceListSize / portionSize;
				if (remainder == 0) {
					return portionSize;
				} else {
					portionsCount++;

					if (portionIndex < portionsCount - 1) {
						return portionSize;
					} else {
						return remainder;
					}
				}

			}
			@Override public Item implementGet(int index) {
				return getSourceList().get(portionIndex * portionSize + index);
			}
		};
	}

	public static class FromSourceList<Item> extends DivideByPortionsList<Item> {

		private final List<Item> sourceList;

		public FromSourceList(List<Item> sourceList, int portionSize) {
			super(portionSize);
			this.sourceList = sourceList;
		}

		@Override
		protected List<Item> getSourceList() {
			return sourceList;
		}
	}

}
