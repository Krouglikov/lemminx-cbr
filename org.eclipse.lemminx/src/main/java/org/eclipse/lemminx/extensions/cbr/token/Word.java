package org.eclipse.lemminx.extensions.cbr.token;

import java.util.Objects;

public class Word implements Token {
    final String content;
    final boolean isLastInLine;

    public Word(String content, boolean isLastInLine) {
        if (content == null) {
            throw new IllegalArgumentException("word must not be null");
        }
        this.content = content;
        this.isLastInLine = isLastInLine;
    }

    @Override
    public TokenType type() {
        return TokenType.WORD;
    }

    @Override
    public int length() {
        return content.length();
    }

    @Override
    public String content() {
        return content;
    }

    @Override
    public String toString() {
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Word word = (Word) o;
        return isLastInLine == word.isLastInLine && content.equals(word.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, isLastInLine);
    }
}
