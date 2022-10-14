package org.eclipse.lemminx.extensions.cbr.token;

public interface Token {
    TokenType type();

    int length();

    String content();

    boolean isLastInLine();

    default boolean isEmpty() {
        return length() == 0;
    }

}
