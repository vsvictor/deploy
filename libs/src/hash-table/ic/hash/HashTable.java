package ic.hash;


import ic.interfaces.action.Action1;
import org.jetbrains.annotations.NotNull;
import ic.interfaces.condition.Condition1;
import ic.interfaces.emptiable.Emptiable;
import ic.interfaces.getter.Getter;
import ic.throwables.Break;
import ic.throwables.NotExists;
import ic.struct.variable.Variable;
import ic.throwables.AlreadyExists;
import ic.struct.collection.Collection;
import ic.struct.list.Array;
import ic.struct.list.EditableList;

import static ic.util.Unsigned.unsignedMod;


public class HashTable<Item> implements Collection<Item>, Emptiable {


	private final IntHasher<Item> hasher;


	private static final int INITIAL_BUCKETS_COUNT = 16;

	private static final int MAX_BUCKET_SIZE = 64;


	private class Bucket extends EditableList.Default<Item> {}


	private Array<Bucket> buckets;

	@Override public void empty() {
		buckets = new Array.Default<Bucket>(INITIAL_BUCKETS_COUNT);
	}

	{ empty(); }


	private void doubleBucketsCount() {
		final int newBucketsCount = buckets.getCount() * 2;
		final Array<Bucket> newBuckets = new Array.Default<Bucket>(newBucketsCount);
		for (Bucket bucket : buckets) {
			if (bucket == null) continue;
			for (Item item : bucket) {
				final Bucket newBucket; {
					final int newBucketIndex = unsignedMod(hasher.get(item), newBucketsCount);
					Bucket newBucketValue = newBuckets.get(newBucketIndex);
					if (newBucketValue == null) {
						newBucketValue = new Bucket();
						newBuckets.set(newBucketIndex, newBucketValue);
					}
					newBucket = newBucketValue;
				}
				newBucket.add(item);
			}
		}
		buckets = newBuckets;
	}


	public synchronized Item get(int hash, Condition1<Item> searcher) throws NotExists {
		final Bucket bucket = buckets.get(unsignedMod(hash, buckets.getCount()));
		if (bucket == null) throw NotExists.NOT_EXISTS;
		return bucket.findOrThrow(searcher);
	}

	public synchronized boolean contains(int hash, Condition1<Item> searcher) {
		try {
			get(hash, searcher);
			return true;
		} catch (NotExists notExists) {
			return false;
		}
	}

	public synchronized void add(int hash, Condition1<Item> searcher, Getter<Item> getter) throws AlreadyExists {
		final Bucket bucket; {
			final int bucketIndex = unsignedMod(hash, buckets.getCount());
			Bucket bucketValue = buckets.get(bucketIndex);
			if (bucketValue == null) {
				bucketValue = new Bucket();
				buckets.set(bucketIndex, bucketValue);
			}
			bucket = bucketValue;
		}
		try {
			bucket.findOrThrow(searcher);
			throw AlreadyExists.ALREADY_EXISTS;
		} catch (NotExists notExists) {}
		if (bucket.getCount() >= MAX_BUCKET_SIZE) {
			doubleBucketsCount();
			add(hash, searcher, getter);
			return;
		}
		bucket.add(getter.get());
	}

	public synchronized void remove(int hash, Condition1<Item> searcher) throws NotExists {
		final Bucket bucket = buckets.get(unsignedMod(hash, buckets.getCount()));
		if (bucket == null) throw NotExists.NOT_EXISTS;
		bucket.remove(searcher);
	}

	public synchronized void removeAll(Condition1<Item> searcher) {
		for (Bucket bucket : buckets) {
			if (bucket == null) continue;
			bucket.removeAll(searcher);
		}
	}

	public synchronized void set(int hash, Condition1<Item> searcher, Item item) {
		final Bucket bucket; {
			final int bucketIndex = unsignedMod(hash, buckets.getCount());
			Bucket bucketValue = buckets.get(bucketIndex);
			if (bucketValue == null) {
				bucketValue = new Bucket();
				buckets.set(bucketIndex, bucketValue);
			}
			bucket = bucketValue;
		}
		try {
			bucket.set(
				bucket.findIndexOrThrow(searcher),
				item
			);
		} catch (NotExists notExists) {
			if (bucket.getCount() >= MAX_BUCKET_SIZE) {
				doubleBucketsCount();
				try {
					add(hash, searcher, new Variable.Constant<Item>(item));
				} catch (AlreadyExists alreadyExists) { throw new Error("HashTable inconsistency!"); }
				return;
			}
			bucket.add(item);
		}
	}


	@Override public void implementForEach(Action1<Item> action) throws Break {
		for (Bucket bucket : buckets) {
			if (bucket == null) continue;
			for (Item item : bucket) {
				action.run(item);
			}
		}
	}


	public HashTable(@NotNull IntHasher<Item> hasher) {
		this.hasher = hasher;
	}


}
