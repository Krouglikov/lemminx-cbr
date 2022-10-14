package org.eclipse.lemminx.extensions.cbr;

import org.eclipse.lemminx.extensions.cbr.token.Token;
import org.eclipse.lemminx.extensions.cbr.token.TokenType;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.eclipse.lemminx.extensions.cbr.token.Tokenizer.tokenize;

/**
 * Компонент разбиения строки на подстроки не более заданной длины
 */
public class LineSeeker {

    //region Fields

    private final String lineBreak;

    private final int maxLineLength;

    private final int firstLineOffset;

    private final int otherLinesOffset;

    private final boolean preserveLeadingBreak;

    private final boolean forceTrailingBreak;

    private final Consumer<String> onNewLine;

    private final Runnable onEnd;

    //endregion

    LineSeeker(Settings settings) {
        this.lineBreak = settings.lineBreak;
        this.maxLineLength = settings.maxLineLength;
        this.firstLineOffset = settings.firstLineOffset;
        this.otherLinesOffset = settings.otherLinesOffset;
        this.preserveLeadingBreak = settings.preserveLeadingBreak;
        this.forceTrailingBreak = settings.forceTrailingBreak;
        this.onNewLine = settings.onNewLine;
        this.onEnd = settings.onEnd;
    }

    public static Settings setup() {
        return new Settings();
    }

    //region Implementation

    void run(String line) {
        List<Token> tokens = tokenize(line, 0, lineBreak);
        boolean leadingBreak = tokens.get(0).type() == TokenType.LINE_BREAK;
        if (leadingBreak && !preserveLeadingBreak) {
            tokens.remove(0);
            leadingBreak = false;
        }
        boolean trailingBreak = tokens.size() != 0 && tokens.get(tokens.size() - 1).type() == TokenType.LINE_BREAK;
        Supplier<LineBuffer> firstLineBuffer = () -> new LineBuffer(this.maxLineLength - firstLineOffset).rejectLongLine();
        Supplier<LineBuffer> restLineBuffers = () -> new LineBuffer(this.maxLineLength - otherLinesOffset);
        Supplier<LineBuffer> newBuffer = new DifferingFirstValueGenerator<>(firstLineBuffer, restLineBuffers);

        LineBuffer buffer = newBuffer.get();
        // если почему-то первая строка сразу не лезет, то пойдем сразу к следующей
        if (buffer.isFull() && !leadingBreak) {
            raiseLineCompleteEvent(buffer.getContents());
            buffer = newBuffer.get();
        }

        for (Token token : tokens) {
            buffer = acceptToken(newBuffer, buffer, token);
        }

        // после обработки последнего токена буфер выбрасываем в последнюю строку
        if (!buffer.isEmpty()) {
            raiseLineCompleteEvent(buffer.getContents());
        }
        // если последним элементом строки был перевод строки, нужно его не потерять
        // или нужно добавить если требуется обеспечить финальный
        if (trailingBreak || forceTrailingBreak) {
            raiseLineCompleteEvent("");
        }
        if (onEnd != null) {
            onEnd.run();
        }
    }

    private LineBuffer acceptToken(Supplier<LineBuffer> newBuffer, LineBuffer buffer, Token token) {
        buffer.accept(token);
        boolean accepted = buffer.isLastAccepted();
        // если строка заполнена, переходим к следующей
        if (buffer.isFull()) {
            raiseLineCompleteEvent(buffer.getContents());
            buffer = newBuffer.get();
        }
        // если же последнее слово не влезло, то сразу пихаем его в новый буфер
        if (!accepted) {
            buffer.accept(token);
        }
        //и сразу контролируем, не закончился ли новый буфер
        if (buffer.isFull()) {
            raiseLineCompleteEvent(buffer.getContents());
            buffer = newBuffer.get();
        }
        return buffer;
    }

    private void raiseLineCompleteEvent(String contents) {
        if (onNewLine != null) {
            onNewLine.accept(contents);
        }
    }

    //endregion

    public static class Settings {
        private String lineBreak = "\n";
        private int maxLineLength = 100;
        private int firstLineOffset = 0;
        private int otherLinesOffset = 0;
        private boolean preserveLeadingBreak = false;
        private boolean forceTrailingBreak = false;
        private Consumer<String> onNewLine;
        private Runnable onEnd;

        public Settings lineBreak(String lineBreak) {
            this.lineBreak = lineBreak;
            return this;
        }

        public Settings maxLineLength(int maxLineLength) {
            this.maxLineLength = maxLineLength;
            return this;
        }

        public Settings firstLineOffset(int firstLineOffset) {
            this.firstLineOffset = firstLineOffset;
            return this;
        }

        public Settings otherLinesOffset(int otherLinesOffset) {
            this.otherLinesOffset = otherLinesOffset;
            return this;
        }

        public Settings preserveLeadingBreak() {
            preserveLeadingBreak = true;
            return this;
        }

        public Settings forceTrailingBreak() {
            forceTrailingBreak = true;
            return this;
        }

        public Settings onNewLine(Consumer<String> onNewLine) {
            this.onNewLine = onNewLine;
            return this;
        }

        public Settings onEnd(Runnable onEnd) {
            this.onEnd = onEnd;
            return this;
        }

        public void run(String line) {
            done().run(line);
        }

        public LineSeeker done() {
            return new LineSeeker(this);
        }
    }

}
