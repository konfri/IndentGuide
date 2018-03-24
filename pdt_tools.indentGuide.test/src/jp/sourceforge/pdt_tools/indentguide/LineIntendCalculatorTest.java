package jp.sourceforge.pdt_tools.indentguide;

import static java.util.stream.Collectors.toMap;
import static jp.sourceforge.pdt_tools.indentguide.LineIndentCalculator.calculateLineIndents;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.junit.Test;

public class LineIntendCalculatorTest {

    // TODO add junit5
    // TODO test 3 spaces for one tab
    // FIXME there is on array out of bound when one line contains three spaces
    // TODO add all other files to the test (parametized)

    @Test
    public void tests() throws IOException {
        // given
        MockTextImpl text = MockTextImpl.builder("files/tabs_and_spaces_mixed_input")
                .setTabToSpaceLenght(4)
                .build();
        IndentSettings is = IndentSettings.builder()
                .drawBlankLine(true)
                .drawLeftEnd(true)
                .skipBlockComment(false)
                .build();

        // when
        List<Integer> indents = calculateIndents(text, is);

        // then
        assertThat(IndentToTextPrinter.print(text.getLines(), indents))
                .isEqualTo(FileReader.read("files/tabs_and_spaces_mixed_expected"));
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
