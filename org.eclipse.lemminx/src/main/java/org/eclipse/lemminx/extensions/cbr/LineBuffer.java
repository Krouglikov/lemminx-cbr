package org.eclipse.lemminx.extensions.cbr;

import org.eclipse.lemminx.extensions.cbr.token.Token;

import java.util.function.Consumer;

public class LineBuffer implements Consumer<Token> {

    private final int maxLength;

    private final StringBuilder buffer = new StringBuilder();

    private boolean full;

    private boolean lastAccepted = true;

    /**
     * Настройка - допустимо ли поместить в буфер слишком длинную строку
     */
    private boolean acceptLongLines = true;

    /**
     * Настройка - игнорировать ли пробел, пытающийся попасть в самое начало буфера
     */
    private boolean ignoreLeadingWhitespace = false;

    public LineBuffer(int maxLength) {
        this.maxLength = maxLength;
        this.full = maxLength <= 0;
    }

    public boolean isFull() {
        return full;
    }

    /**
     * Поместился ли последний токен в буфер
     */
    public boolean isLastAccepted() {
        return lastAccepted;
    }

    public boolean isEmpty() {
        return buffer.length() == 0;
    }

    public String getContents() {
        return buffer.toString();
    }

    public LineBuffer rejectLongLine() {
        acceptLongLines = false;
        return this;
    }

    public LineBuffer ignoreLeadingSpace() {
        ignoreLeadingWhitespace = true;
        return this;
    }

    @Override
    public void accept(Token token) {
        switch (token.type()) {
            case LINE_BREAK:
                full = true;
                break;
            case WHITESPACE:
                acceptWhitespace(token);
                break;
            case WORD:
                acceptWord(token);
                break;
        }
    }

    private void acceptWord(Token token) {
        if (predictedBufferLength(token) >= maxLength) {
            //если в буфере ничего не было, а строка длинная, то она и будет следующей
            if (buffer.length() == 0 && acceptLongLines) {
                putToken(token);
                full = true;
                lastAccepted = true;
            } else {
                full = true;
                lastAccepted = false;
            }
        } else {
            putToken(token);
            lastAccepted = true;
        }
    }

    private void acceptWhitespace(Token token) {
        if (predictedBufferLength(token) >= maxLength) {
            //если в буфере ничего не было, а пробелов слишком много, проигнорим их
            if (buffer.length() == 0) {
                lastAccepted = true;
            } else {
                full = true;
                lastAccepted = true;
            }
        } else {
            if (buffer.length() == 0 && ignoreLeadingWhitespace) {
                // если буфер пуст, то игнорим лидирующие пробелы
                lastAccepted = true;
            } else {
                putToken(token);
                lastAccepted = true;
            }
        }
    }

    private int predictedBufferLength(Token word) {
        int bufferLength = buffer.length();
        if (bufferLength == 0) {
            return word.length();
        } else {
            return bufferLength + word.length();
        }
    }

    private void putToken(Token token) {
        buffer.append(token.content());
    }

}
