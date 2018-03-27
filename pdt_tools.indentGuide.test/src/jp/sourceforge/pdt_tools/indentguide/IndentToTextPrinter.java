package jp.sourceforge.pdt_tools.indentguide;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IndentToTextPrinter {

	public static String print(String text, int... indentIndex) {
		List<Integer> boxed = Arrays.stream(indentIndex)
				.boxed()
				.collect(Collectors.toList());
		return print(text, boxed);
	}

	public static String print(String line, List<Integer> indentIndex) {
		StringBuilder sb = new StringBuilder();
		final AtomicInteger stringIndex = new AtomicInteger();
		indentIndex.stream().sorted().forEach(t -> {
			while (stringIndex.get() < t) {
				sb.append(line.charAt(stringIndex.get()));
				stringIndex.incrementAndGet();
			}
			sb.append("|");
		});
		sb.append(line.substring(stringIndex.get()));

		return sb.toString();
	}
}
