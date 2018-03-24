package jp.sourceforge.pdt_tools.indentguide;

import static java.util.stream.Collectors.toMap;
import static jp.sourceforge.pdt_tools.indentguide.LineIndentCalculator.calculateLineIndents;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.junit.Ignore;
import org.junit.Test;

public class LineIntendCalculatorTest {

	// TODO add junit5
	// FIXME there is on array out of bound when one line contains three spaces
	// TODO add all other files to the test (parametized)

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
	
	@Ignore
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
	
	private List<Integer> calculateIndents(MockTextImpl text, IndentSettings is) {
		return text.getZippedLinesWithIndex()
				.entrySet()
				.stream()
				.collect(toMap(Entry::getKey, e -> calculateLineIndents(text, e.getKey(), is)))
				.values()
				.stream()
				.flatMap(i -> StreamSupport.stream(i.spliterator(), false))
				.collect(Collectors.toList());
	}
}
