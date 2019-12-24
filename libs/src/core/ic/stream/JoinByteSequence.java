package ic.stream;


import org.jetbrains.annotations.Nullable;
import ic.struct.sequence.Sequence;
import ic.struct.sequence.Series;
import ic.throwables.End;


public class JoinByteSequence extends ByteSequence.BaseByteSequence {


	private final Sequence<ByteSequence> byteSequences;


	@Override public ByteInput getIterator() {

		return new ByteInput.BaseByteInput() {

			private final Series<ByteSequence> byteSequenceIterator = byteSequences.getIterator();

			private @Nullable volatile ByteInput itemIterator;

			@Override public synchronized byte getByte() throws End {
				while (true) {
					if (itemIterator == null) itemIterator = byteSequenceIterator.get().getIterator();
					try {
						return itemIterator.getByte();
					} catch (End endOfItem) {
						itemIterator = byteSequenceIterator.get().getIterator();
					}
				}
			}

			@Override public Byte get() throws End {
				while (true) {
					if (itemIterator == null) itemIterator = byteSequenceIterator.get().getIterator();
					try {
						return itemIterator.get();
					} catch (End endOfItem) {
						itemIterator = byteSequenceIterator.get().getIterator();
					}
				}
			}

			@Override public void close() {

			}

		};

	}


	public JoinByteSequence(Sequence<ByteSequence> byteSequences) {
		this.byteSequences = byteSequences;
	}

	public JoinByteSequence(ByteSequence... parts) {
		this.byteSequences = new Sequence.Default<>(parts);
	}


}
