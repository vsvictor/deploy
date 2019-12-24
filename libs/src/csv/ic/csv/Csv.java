package ic.csv;


import com.opencsv.CSVReader;
import ic.annotations.Degenerate;
import ic.annotations.Hide;
import ic.interfaces.action.BetweenAction1;
import ic.struct.list.ConvertList;
import ic.struct.list.List;
import ic.text.Text;
import ic.text.TextBuffer;
import ic.throwables.IOException;
import org.jetbrains.annotations.NotNull;

import static ic.json.Quotes.quote;


public @Degenerate class Csv { @Hide private Csv() {}


	public static List<List<String>> parseCsv(@NotNull Text text, char comma) {

		final CSVReader csvReader = new CSVReader(
			text.getIterator().toReader(),
			comma
		);

		final java.util.List<String[]> rows; try {
			rows = csvReader.readAll();
		} catch (java.io.IOException e) { throw new IOException.Runtime(e); }

		return new ConvertList<>(
			new List.FromJavaList<>(rows),
			List.FromArray::new
		);

	}

	public static List<List<String>> parseCsv(Text text) {
		return parseCsv(text, ',');
	}


	public static Text formatCsv(@NotNull List<List<String>> data, final char comma) {

		final TextBuffer textBuffer = new TextBuffer();

		data.forEach(row -> {

			row.forEach(new BetweenAction1<>() {

				@Override protected void implementRun(String data1) {
					textBuffer.write(quote(data1));
				}

				@Override protected void doBetween() {
					textBuffer.put(comma);
				}

			});

			textBuffer.writeLine();

		});

		return textBuffer;

	}

	public static Text formatCsv(@NotNull List<List<String>> data) {
		return formatCsv(data, ',');
	}


}
