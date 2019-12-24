package ic.struct.order;


import org.jetbrains.annotations.NotNull;
import ic.struct.collection.Collection;
import ic.struct.list.List;
import ic.struct.set.CountableSet;


public interface OrderedCountableSet<Item> extends OrderedSet<Item>, CountableSet<Item>, List<Item> {


	class Default<Item> extends OrderedEditableSet.Default<Item> {

		public Default(@NotNull Order<Item> order) 							{ super(order); 		}
		public Default(@NotNull Order<Item> order, Collection<Item> items) 	{ super(order, items); 	}
		public Default(@NotNull Order<Item> order, Iterable<Item> items) 	{ super(order, items); 	}

	}


}
