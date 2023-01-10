package org.eclipse.lemminx.extensions.cbr.token;

public interface Token {
    TokenType type();

    int length();

    String content();

    default boolean isEmpty() {
        return length() == 0;
    }

}
