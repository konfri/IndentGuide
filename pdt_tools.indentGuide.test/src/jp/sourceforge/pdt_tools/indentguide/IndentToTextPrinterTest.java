package jp.sourceforge.pdt_tools.indentguide;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.util.Lists;
import org.junit.Test;

public class IndentToTextPrinterTest {
    @Test
    public void array() {
        assertThat(IndentToTextPrinter.print("        a", 0, 4)).isEqualTo("|    |    a"); // spaces
        assertThat(IndentToTextPrinter.print("		a", 0, 1)).isEqualTo("|	|	a"); // tabs
    }

    @Test
    public void list() {
        assertThat(IndentToTextPrinter.print("        a", Lists.newArrayList(0, 4))).isEqualTo("|    |    a"); // spaces
        assertThat(IndentToTextPrinter.print("		a", Lists.newArrayList(0, 1))).isEqualTo("|	|	a"); // tabs
    }

    @Test
    public void arrayWithSeveralLines() {
        String nl = System.getProperty("line.separator");
        assertThat(IndentToTextPrinter.print("        aaaa" + nl + "        b", 0, 4, 13, 17))
                .isEqualTo("|    |    aaaa" + nl + "|    |    b"); // spaces
        assertThat(IndentToTextPrinter.print("		aaaa\n		bbbb", 0, 1, 7, 8))
                .isEqualTo("|	|	aaaa" + nl + "|	|	bbbb"); // tabs
    }

    @Test
    public void arraySorted() {
        assertThat(IndentToTextPrinter.print("        a", 4, 0)).isEqualTo("|    |    a"); // spaces
        assertThat(IndentToTextPrinter.print("		a", 1, 0)).isEqualTo("|	|	a"); // tabs
    }
}
