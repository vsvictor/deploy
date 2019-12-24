package ic.cmd;


import ic.annotations.Degenerate;
import ic.annotations.Hide;
import ic.interfaces.action.BetweenAction2;
import org.jetbrains.annotations.NotNull;
import ic.interfaces.action.SafeBetweenAction2;
import ic.interfaces.countable.Countable;
import ic.struct.collection.ConvertCollection;
import ic.struct.list.ConvertList;
import ic.struct.list.IndexOutOfBounds;
import ic.struct.list.List;
import ic.struct.list.IntRange;

import static ic.LoopKt.loop;
import static ic.struct.collection.Collections.maxInt;


public @Degenerate class TableFormatter { @Hide private TableFormatter() {}


	public static void writeTable(@NotNull final Console console, @NotNull final List<List<String>> rows, final int tabs) {

		assert console 	!= null;
		assert rows 	!= null;

		final List<Integer> columnMaxLengths = new ConvertList<>(
			new IntRange(
				maxInt(
					new ConvertCollection<>(
						rows,
						Countable::getCount
					)
				)
			),
			columnIndex -> maxInt(
				new ConvertCollection<>(
					rows,
					row -> {
						try {
							final String value = row.safeGet(columnIndex);
							if (value == null) return 0;
							return value.length();
						} catch (IndexOutOfBounds indexOutOfBounds) {
							return 0;
						}
					}
				)
			)
		);

		rows.forEach(row -> {

			loop(
				() -> console.write("    "),
				tabs
			);

			row.forEach(new BetweenAction2<>() {

				@Override protected void implementRun(String string, Integer columnIndex) {

					if (string == null) string = "";

					console.write(string);

					loop(
						() -> console.put(' '),
						columnMaxLengths.get(columnIndex) - string.length()
					);

				}

				@Override protected void doBetween() {

					console.put(' ');

				}

			});

			console.writeLine();

		});

	}


}
