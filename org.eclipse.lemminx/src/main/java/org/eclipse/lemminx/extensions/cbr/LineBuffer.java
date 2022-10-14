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

    public LineBuffer(int maxLength) {
        this.maxLength = maxLength;
        this.full = maxLength <= 0;
    }

    public boolean isFull() {
        return full;
    }

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

    @Override
    public void accept(Token token) {
        switch (token.type()) {
            case WHITESPACE:
                break; //TODO preserve whitespace
            case LINE_BREAK:
                full = true;
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
            } else {
                full = true;
                lastAccepted = false;
            }
        } else {
            putToken(token);
        }
    }

    private int predictedBufferLength(Token word) {
        int bufferLength = buffer.length();
        if (bufferLength == 0) {
            return word.length();
        } else {
            return bufferLength + 1 + word.length() + 1;
        }
    }

    private void putToken(Token token) {
        // после крайнего слова надо добавить пробел
        if (buffer.length() != 0) {
            buffer.append(" "); // NB! replace all whitespace symbols with " "
        }
        buffer.append(token.content());
    }

}
