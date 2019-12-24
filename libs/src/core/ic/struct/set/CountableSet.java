package ic.struct.set;


import org.jetbrains.annotations.NotNull;
import ic.interfaces.action.Action1;
import ic.struct.collection.Collection;
import ic.throwables.Break;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.NoSuchElementException;


public interface CountableSet<Item> extends Set<Item>, Collection<Item> {


	class FromJavaSet<Item> implements CountableSet<Item> {

		private final java.util.Set<Item> javaSet;

		@Override public void implementForEach(Action1<Item> action) throws Break {
			for (Item item : javaSet) action.run(item);
		}

		@Override public boolean contains(Item item) {
			return javaSet.contains(item);
		}

		public FromJavaSet(java.util.Set<Item> javaSet) {
			this.javaSet = javaSet;
		}

	}


	class Default<Item> extends FromJavaSet<Item> {

		private static <Item> java.util.Set<Item> createJavaSet(Item[] array) {
			final java.util.Set<Item> javaSet = new HashSet<Item>();
			for (Item item : array) javaSet.add(item);
			return javaSet;
		}

		public @SafeVarargs Default(Item... items) {
			super(createJavaSet(items));
		}

		private static <Item> java.util.Set<Item> createJavaSet(Iterable<Item> iterable) {
			final java.util.Set<Item> javaSet = new HashSet<>();
			for (Item item : iterable) javaSet.add(item);
			return javaSet;
		}

		public Default(Iterable<Item> iterable) {
			super(createJavaSet(iterable));
		}

		private static <Item> java.util.Set<Item> createJavaSet(@NotNull Enumeration<Item> enumeration) {
			final java.util.Set<Item> javaSet = new HashSet<>();
			try {
				while (true) {
					javaSet.add(enumeration.nextElement());
				}
			} catch (NoSuchElementException end) {}
			return javaSet;
		}

		public Default(Enumeration<Item> enumeration) {
			super(createJavaSet(enumeration));
		}

		private static <Item> java.util.Set<Item> createJavaSet(Collection<Item> items) {
			final java.util.Set<Item> javaSet = new HashSet<>();
			items.forEach(javaSet::add);
			return javaSet;
		}

		public Default(Collection<Item> items) {
			super(createJavaSet(items));
		}

	}


}
