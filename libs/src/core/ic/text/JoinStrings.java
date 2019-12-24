package ic.text;


import ic.struct.sequence.ConvertSequence;
import ic.struct.sequence.Sequence;


public class JoinStrings extends JoinText {


	public JoinStrings(Sequence<String> sequence, char divider) {

		super(new ConvertSequence<>(
			sequence,
			FromString::new
		), divider);

	}

	public JoinStrings(String[] strings, char divider) {
		this(new Sequence.Default<>(strings), divider);
	}

	public JoinStrings(Iterable<String> strings, char divider) {
		this(new Sequence.Default<>(strings), divider);
	}

	public JoinStrings(Sequence<String> sequence) {
		super(new ConvertSequence<>(
			sequence,
			FromString::new
		));
	}


}
