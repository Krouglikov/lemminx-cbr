package org.eclipse.lemminx.extensions.cbr.token;

import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.eclipse.lemminx.extensions.cbr.token.Tokenizer.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class TokenizerTest {


    @Test
    public void emptyStringToEmptyTokenList() {
        String line = "";
        List<Token> expected = new LinkedList<>();
        assertEquals(expected, Tokenizer.tokenize(line, 0, "\n"));
    }

    @Test
    public void wordsAreTokenized() {
        String line = "word\r\nword\r\n \tword\r\nword";
        List<Token> expected = Stream.<Token>builder()
                .add(new Word("word", false))
                .add(new LineBreak(false))
                .add(new Word("word", false))
                .add(new LineBreak(false))
                .add(new Whitespaces(" \t", false))
                .add(new Word("word", false))
                .add(new LineBreak(false))
                .add(new Word("word", true))
                .build().collect(Collectors.toList());
        assertIterableEquals(expected, Tokenizer.tokenize(line, 0, "\r\n"));
    }

    @Test
    public void leadingWhitespacesTokenized() {
        String line = " \t \tword\r\nword\r\n \tword\r\nword";
        List<Token> expected = Stream.<Token>builder()
                .add(new Whitespaces(" \t \t", false))
                .add(new Word("word", false))
                .add(new LineBreak(false))
                .add(new Word("word", false))
                .add(new LineBreak(false))
                .add(new Whitespaces(" \t", false))
                .add(new Word("word", false))
                .add(new LineBreak(false))
                .add(new Word("word", true))
                .build().collect(Collectors.toList());
        assertIterableEquals(expected, Tokenizer.tokenize(line, 0, "\r\n"));
    }

    @Test
    public void trailingWhitespacesTokenized() {
        String line = "word\r\nword\r\n \tword\r\nword\t ";
        List<Token> expected = Stream.<Token>builder()
                .add(new Word("word", false))
                .add(new LineBreak(false))
                .add(new Word("word", false))
                .add(new LineBreak(false))
                .add(new Whitespaces(" \t", false))
                .add(new Word("word", false))
                .add(new LineBreak(false))
                .add(new Word("word", false))
                .add(new Whitespaces("\t ", true))
                .build().collect(Collectors.toList());
        assertIterableEquals(expected, Tokenizer.tokenize(line, 0, "\r\n"));
    }

    @Test
    public void trailingWhitespacesTokenized2() {
        String line = "Lorem ipsum dolor sit amet, ";
        List<Token> expected = Stream.<Token>builder()
                .add(new Word("Lorem", false))
                .add(new Whitespaces(" ", false))
                .add(new Word("ipsum", false))
                .add(new Whitespaces(" ", false))
                .add(new Word("dolor", false))
                .add(new Whitespaces(" ", false))
                .add(new Word("sit", false))
                .add(new Whitespaces(" ", false))
                .add(new Word("amet,", false))
                .add(new Whitespaces(" ", true))
                .build().collect(Collectors.toList());
        assertIterableEquals(expected, Tokenizer.tokenize(line, 0, "\r\n"));
    }


    @Test
    public void leadingLineBreakTokenized() {
        String line = "\r\nword\r\nword\r\n \tword\r\nword";
        List<Token> expected = Stream.<Token>builder()
                .add(new LineBreak(false))
                .add(new Word("word", false))
                .add(new LineBreak(false))
                .add(new Word("word", false))
                .add(new LineBreak(false))
                .add(new Whitespaces(" \t", false))
                .add(new Word("word", false))
                .add(new LineBreak(false))
                .add(new Word("word", true))
                .build().collect(Collectors.toList());
        assertIterableEquals(expected, Tokenizer.tokenize(line, 0, "\r\n"));
    }

    @Test
    public void testWhitespaceLength() {
        assertEquals(0, whitespaceLength("", 0));
        assertEquals(0, whitespaceLength("string", 2));
        assertEquals(1, whitespaceLength("st ring", 2));
        assertEquals(1, whitespaceLength("st\tring", 2));
        assertEquals(2, whitespaceLength("st \tring", 2));
        assertEquals(2, whitespaceLength("st\t ring", 2));
    }

    @Test
    public void testWhitespacePrefixLength() {
        assertEquals(0, whitespacePrefixLength(""));
        assertEquals(0, whitespacePrefixLength("string"));
        assertEquals(1, whitespacePrefixLength(" "));
        assertEquals(1, whitespacePrefixLength("\t"));
        assertEquals(2, whitespacePrefixLength(" \tstring"));
        assertEquals(2, whitespacePrefixLength("\t string"));
    }

    @Test
    public void testFindWhitespace() {
        assertEquals(-1, findWhitespace("", 0));
        assertEquals(-1, findWhitespace("str ing", 4));
        assertEquals(3, findWhitespace("str ing", 3));
        assertEquals(3, findWhitespace("str ing", 2));
        assertEquals(3, findWhitespace("str\ting", 2));
        assertEquals(3, findWhitespace("str ing", 1));
        assertEquals(3, findWhitespace("str\ting", 1));
    }

}