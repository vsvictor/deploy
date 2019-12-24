package ic.text;


import org.jetbrains.annotations.Nullable;

import ic.struct.sequence.Sequence;
import ic.struct.sequence.Series;

import ic.throwables.End;


public class JoinText extends Text.BaseText {


	private final @Nullable Character divider;
	private final Sequence<Text> textSequence;


	@Override public CharInput getIterator() {

		return new CharInput.BaseCharInput() {

			private final Series<Text> textSequenceIterator = textSequence.getIterator();

			private @Nullable volatile CharInput itemIterator;

			@Override public synchronized char getChar() throws End {
				while (true) {
					if (itemIterator == null) itemIterator = textSequenceIterator.get().getIterator();
					try {
						return itemIterator.getChar();
					} catch (End endOfItem) {
						itemIterator = textSequenceIterator.get().getIterator();
						if (divider != null) return divider;
					}
				}
			}

			@Override public Character get() throws End {
				while (true) {
					if (itemIterator == null) itemIterator = textSequenceIterator.get().getIterator();
					try {
						return itemIterator.get();
					} catch (End endOfItem) {
						itemIterator = textSequenceIterator.get().getIterator();
						if (divider != null) return divider;
					}
				}
			}
		};

	}


	private JoinText(Sequence<Text> textSequence, @Nullable Character divider) {
		this.textSequence = textSequence;
		this.divider = divider;
	}

	public JoinText(Sequence<Text> textSequence, char divider) {
		this(textSequence, (Character) divider);
	}

	public JoinText(Sequence<Text> textSequence) {
		this(textSequence, null);
	}

	public JoinText(Text... parts) {
		this(new Sequence.Default<Text>(parts));
	}


}
