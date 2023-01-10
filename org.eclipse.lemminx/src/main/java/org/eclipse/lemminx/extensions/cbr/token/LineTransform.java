package org.eclipse.lemminx.extensions.cbr.token;

import java.util.LinkedList;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class LineTransform {

    /**
     * Удаление переводов строки.
     */
    public static UnaryOperator<List<Token>> removeLineBreaks() {
        return tokens -> removeLineBreaks(false, false, tokens);
    }

    /**
     * Удаление переводов строки, быть может за исключением начального и конечнеого.
     *
     * @param keepLeading  сохранять ли перенос строки непосредственно в ее начале
     * @param keepTrailing сохранять ли перенос строки непосредственно в ее конце
     */
    public static UnaryOperator<List<Token>> removeLineBreaks(boolean keepLeading, boolean keepTrailing) {
        return tokens -> removeLineBreaks(keepLeading, keepTrailing, tokens);
    }

    /**
     * Замена групп пробелов на одиночные
     */
    public static UnaryOperator<List<Token>> collapseWhitespaces() {
        return LineTransform::collapseWhitespaces;
    }

    /**
     * Удаление первого токена если он пробельный
     */
    public static UnaryOperator<List<Token>> removeLeadingWhitespace() {
        return LineTransform::removeLeadingWhitespace;
    }

    /**
     * Удаление последнего токена если он пробельный
     */
    public static UnaryOperator<List<Token>> removeTrailingWhitespace() {
        return LineTransform::removeTrailingWhitespace;
    }

    /**
     * Удаление форматирующего префикса ([пробелы]-разрыв строки-[пробелы])
     */
    public static UnaryOperator<List<Token>> removeLeadingFormatting() {
        return LineTransform::removeLeadingFormatting;
    }

    /**
     * Удаление форматирующего постфикса ([пробелы]-разрыв строки-[пробелы])
     */
    public static UnaryOperator<List<Token>> removeTrailingFormatting() {
        return LineTransform::removeTrailingFormatting;
    }

    //region Private methods

    private static List<Token> removeLineBreaks(boolean keepLeading, boolean keepTrailing, List<Token> tokens) {
        List<Token> result;
        if (tokens == null || tokens.isEmpty()) {
            result = tokens;
        } else {
            result = new LinkedList<>();
            int size = tokens.size();
            for (int i = 0; i < size; i++) {
                Token token = tokens.get(i);
                if (token.type() == TokenType.LINE_BREAK) {
                    if (i == 0 && keepLeading) {
                        result.add(token);
                    } else if (i == size - 1 && keepTrailing) {
                        result.add(token);
                    }
                    //otherwise drop token
                } else {
                    result.add(token);
                }
            }
        }
        return result;
    }

    private static List<Token> collapseWhitespaces(List<Token> tokens) {
        List<Token> result;
        if (tokens == null || tokens.isEmpty()) {
            result = tokens;
        } else {
            result = new LinkedList<>();
            int size = tokens.size();
            for (int i = 0; i < size; i++) {
                Token token = tokens.get(i);
                if (token.type() == TokenType.WHITESPACE) {
                    int resultSize = result.size();
                    if (resultSize > 1 && result.get(resultSize - 1).type() == TokenType.WHITESPACE) {
                        ; // skip duplicate whitespace
                    } else {
                        result.add(new Whitespaces(" "));
                    }
                } else {
                    result.add(token);
                }
            }
        }
        return result;
    }

    private static List<Token> removeLeadingWhitespace(List<Token> tokens) {
        List<Token> result;
        if (tokens == null || tokens.isEmpty()) {
            result = tokens;
        } else if (tokens.get(0).type() != TokenType.WHITESPACE) {
            result = tokens;
        } else {
            result = tokens.stream().skip(1).collect(Collectors.toList());
        }
        return result;
    }

    private static List<Token> removeTrailingWhitespace(List<Token> tokens) {
        List<Token> result;
        if (tokens == null || tokens.isEmpty()) {
            result = tokens;
        } else if (tokens.get(tokens.size() - 1).type() != TokenType.WHITESPACE) {
            result = tokens;
        } else {
            result = tokens.stream().limit(tokens.size() - 1).collect(Collectors.toList());
        }
        return result;
    }

    private static List<Token> removeLeadingFormatting(List<Token> tokens) {
        List<Token> result;
        if (tokens == null || tokens.isEmpty()) {
            result = tokens;
        } else if (startsWithSBS(tokens)) {
            result = tokens.stream().skip(3).collect(Collectors.toList());
        } else if (startsWithSBBS(tokens)) {
            result = tokens.stream().skip(2).collect(Collectors.toList());
        } else if (startsWithLineBreak(tokens)) {
            result = tokens.stream().skip(1).collect(Collectors.toList());
        } else {
            result = tokens;
        }
        return result;
    }

    private static List<Token> removeTrailingFormatting(List<Token> tokens) {
        List<Token> result;
        if (tokens == null || tokens.isEmpty()) {
            result = tokens;
        } else if (endsWithSBS(tokens)) {
            result = tokens.stream().limit(tokens.size() - 3).collect(Collectors.toList());
        } else if (endsWithSBBS(tokens)) {
            result = tokens.stream().limit(tokens.size() - 2).collect(Collectors.toList());
        } else if (endsWithLineBreak(tokens)) {
            result = tokens.stream().limit(tokens.size() - 1).collect(Collectors.toList());
        } else {
            result = tokens;
        }
        return result;
    }

    /**
     * Строка токенов начинается комбинацией пробел-разрыв строки-пробел
     */
    private static boolean startsWithSBS(List<Token> tokens) {
        if (tokens == null || tokens.size() < 3) {
            return false;
        } else {
            Token token1 = tokens.get(0);
            Token token2 = tokens.get(1);
            Token token3 = tokens.get(2);
            return token1.type() == TokenType.WHITESPACE
                    && token2.type() == TokenType.LINE_BREAK
                    && token3.type() == TokenType.WHITESPACE;
        }
    }

    /**
     * Строка токенов начинается комбинацией пробел-разрыв строки или разрыв строки-пробел
     */
    private static boolean startsWithSBBS(List<Token> tokens) {
        if (tokens == null || tokens.size() < 2) {
            return false;
        } else {
            Token token1 = tokens.get(0);
            Token token2 = tokens.get(1);
            boolean isSB = token1.type() == TokenType.WHITESPACE
                    && token2.type() == TokenType.LINE_BREAK;
            boolean isBS = token1.type() == TokenType.LINE_BREAK
                    && token2.type() == TokenType.WHITESPACE;
            return isSB || isBS;
        }
    }

    /**
     * Строка токенов начинается разрывом строки
     */
    private static boolean startsWithLineBreak(List<Token> tokens) {
        if (tokens == null || tokens.size() < 1) {
            return false;
        } else {
            Token token1 = tokens.get(0);
            return token1.type() == TokenType.LINE_BREAK;
        }
    }

    /**
     * Строка токенов начинается комбинацией пробел-разрыв строки-пробел
     */
    private static boolean endsWithSBS(List<Token> tokens) {
        if (tokens == null || tokens.size() < 3) {
            return false;
        } else {
            int size = tokens.size();
            Token token1 = tokens.get(size - 3);
            Token token2 = tokens.get(size - 2);
            Token token3 = tokens.get(size - 1);
            return token1.type() == TokenType.WHITESPACE
                    && token2.type() == TokenType.LINE_BREAK
                    && token3.type() == TokenType.WHITESPACE;
        }
    }

    /**
     * Строка токенов начинается комбинацией пробел-разрыв строки или разрыв строки-пробел
     */
    private static boolean endsWithSBBS(List<Token> tokens) {
        if (tokens == null || tokens.size() < 2) {
            return false;
        } else {
            int size = tokens.size();
            Token token1 = tokens.get(size - 2);
            Token token2 = tokens.get(size - 1);
            boolean isSB = token1.type() == TokenType.WHITESPACE
                    && token2.type() == TokenType.LINE_BREAK;
            boolean isBS = token1.type() == TokenType.LINE_BREAK
                    && token2.type() == TokenType.WHITESPACE;
            return isSB || isBS;
        }
    }

    /**
     * Строка токенов начинается разрывом строки
     */
    private static boolean endsWithLineBreak(List<Token> tokens) {
        if (tokens == null || tokens.size() < 1) {
            return false;
        } else {
            Token token1 = tokens.get(tokens.size() - 1);
            return token1.type() == TokenType.LINE_BREAK;
        }
    }

    //endregion

}
