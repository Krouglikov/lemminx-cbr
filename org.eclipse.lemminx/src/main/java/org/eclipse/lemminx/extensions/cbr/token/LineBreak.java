package org.eclipse.lemminx.extensions.cbr.token;

import java.util.Objects;

public class LineBreak implements Token {

    private final boolean isLastInLine;

    public LineBreak(boolean isLastInLine) {
        this.isLastInLine = isLastInLine;
    }

    @Override
    public TokenType type() {
        return TokenType.LINE_BREAK;
    }

    @Override
    public int length() {
        return 0;
    }

    @Override
    public String content() {
        return null;
    }

    @Override
    public boolean isLastInLine() {
        return isLastInLine;
    }

    @Override
    public String toString() {
        return "<break>";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineBreak lineBreak = (LineBreak) o;
        return isLastInLine == lineBreak.isLastInLine;
    }

    @Override
    public int hashCode() {
        return Objects.hash(isLastInLine);
    }
}
