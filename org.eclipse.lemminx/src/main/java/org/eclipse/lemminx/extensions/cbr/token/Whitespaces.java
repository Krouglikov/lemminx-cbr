package org.eclipse.lemminx.extensions.cbr.token;

import java.util.Objects;

public class Whitespaces implements Token {

    final String content;
    final boolean isLastInLine;

    public Whitespaces(String content, boolean isLastInLine) {
        if (content == null) {
            throw new IllegalArgumentException("word must not be null");
        }
        this.content = content;
        this.isLastInLine = isLastInLine;
    }

    public Whitespaces(String content) {
        this(content, false);
    }

    @Override
    public TokenType type() {
        return TokenType.WHITESPACE;
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
        Whitespaces that = (Whitespaces) o;
        return isLastInLine == that.isLastInLine && content.equals(that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, isLastInLine);
    }
}
