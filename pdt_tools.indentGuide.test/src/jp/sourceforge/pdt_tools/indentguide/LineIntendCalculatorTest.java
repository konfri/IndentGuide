package jp.sourceforge.pdt_tools.indentguide;

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.junit.Test;

public class LineIntendCalculatorTest {

	// TODO add junit5

	@Test
	public void tabsAndSpacesMixed() throws IOException {
		// given
		MockTextImpl text = MockTextImpl.builder("files/tabs_and_spaces_mixed_input")
				.build();
		IndentSettings is = IndentSettings.builder().build();

		// when
		List<Integer> indents = calculateIndents(text, is);

		// then
		assertThat(IndentToTextPrinter.print(text.getLines(), indents))
				.isEqualTo(FileReader.read("files/tabs_and_spaces_mixed_expected"));
	}

	@Test
	public void drawBlockComment() throws IOException {
		// given
		MockTextImpl text = MockTextImpl.builder("files/draw_block_comment_input")
				.build();
		IndentSettings is = IndentSettings.builder()
				.skipBlockComment(false)
				.build();
		// when
		List<Integer> indents = calculateIndents(text, is);

		// then
		assertThat(IndentToTextPrinter.print(text.getLines(), indents))
				.isEqualTo(FileReader.read("files/draw_block_comment_expected"));
	}

	@Test
	public void skipBlockComment() throws IOException {
		// given
		MockTextImpl text = MockTextImpl.builder("files/skip_block_comment_input")
				.build();
		IndentSettings is = IndentSettings.builder()
				.skipBlockComment(true)
				.build();

		// when
		List<Integer> indents = calculateIndents(text, is);

		// then
		assertThat(IndentToTextPrinter.print(text.getLines(), indents))
				.isEqualTo(FileReader.read("files/skip_block_comment_expected"));
	}

	@Test
	public void skipFirstIndent() throws IOException {
		// given
		MockTextImpl text = MockTextImpl.builder("files/skip_first_indent_input")
				.build();
		IndentSettings is = IndentSettings.builder()
				.drawLeftEnd(false)
				.build();
		// when
		List<Integer> indents = calculateIndents(text, is);

		// then
		assertThat(IndentToTextPrinter.print(text.getLines(), indents))
				.isEqualTo(FileReader.read("files/skip_first_indent_expected"));
	}

	@Test
	public void skipWhiteLine() throws IOException {
		// given
		MockTextImpl text = MockTextImpl.builder("files/skip_white_line_input")
				.build();
		IndentSettings is = IndentSettings.builder()
				.drawBlankLine(false)
				.build();
		// when
		List<Integer> indents = calculateIndents(text, is);

		// then
		assertThat(IndentToTextPrinter.print(text.getLines(), indents))
				.isEqualTo(FileReader.read("files/skip_white_line_expected"));
	}

	@Test
	public void drawWhiteLine() throws IOException {
		// given
		MockTextImpl text = MockTextImpl.builder("files/draw_white_line_input")
				.build();
		IndentSettings is = IndentSettings.builder()
				.drawBlankLine(true)
				.build();
		// when
		List<Integer> indents = calculateIndents(text, is);

		// then
		assertThat(IndentToTextPrinter.print(text.getLines(), indents))
				.isEqualTo(FileReader.read("files/draw_white_line_expected"));
	}

	@Test
	public void firstLineWhiteSpace() throws IOException {
		// given
		MockTextImpl text = MockTextImpl.builder("files/first_line_white_space_input")
				.build();
		IndentSettings is = IndentSettings.builder()
				.drawBlankLine(true)
				.build();
		// when
		List<Integer> indents = calculateIndents(text, is);

		// then
		assertThat(IndentToTextPrinter.print(text.getLines(), indents))
				.isEqualTo(FileReader.read("files/first_line_white_space_expected"));
	}

	private List<Integer> calculateIndents(MockTextImpl text, IndentSettings is) {
		return text.getZippedLinesWithIndex()
				.entrySet()
				.stream()
				.collect(toMap(Entry::getKey, e -> LineIndentCalculator.calculateLineIndents(text, e.getKey(), is)))
				.values()
				.stream()
				.flatMap(i -> i.stream())
				.collect(Collectors.toList());
	}
}
