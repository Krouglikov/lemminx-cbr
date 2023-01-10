package org.eclipse.lemminx.extensions.cbr;

import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertLinesMatch;

class LineSeekerTest {

    @Test
    public void testSeekLines() {
        String data = "1234567890 1234567890 1234567890 1234567890 1234567890";
        List<String> expected = Stream.<String>builder()
                .add("1234567890 1234567890 ")
                .add("1234567890 1234567890 ")
                .add("1234567890")
                .build().collect(Collectors.toList());
        List<String> lines = new LinkedList<>();
        LineSeeker.setup()
                .maxLineLength(30)
                .onNewLine(lines::add)
                .run(data);
        assertLinesMatch(expected, lines);
    }

}