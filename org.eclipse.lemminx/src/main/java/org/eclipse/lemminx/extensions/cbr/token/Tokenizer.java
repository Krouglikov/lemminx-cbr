package org.eclipse.lemminx.extensions.cbr.token;

import java.util.LinkedList;
import java.util.List;

public final class Tokenizer {

    private final static String WHITESPACES = " \t";

    private Tokenizer() {
    }

    public static List<Token> tokenize(String line, int pos, String lineBreak) {
        int maxPos = line.length() - 1;
        List<Token> result = new LinkedList<>();
        do {
            int nextWhitespace = findWhitespace(line, pos);
            if (nextWhitespace == pos) {
                // если сразу в начале поиска наткнулись на пробел
                int lastWhitespace = pos + whitespaceLength(line, pos);
                boolean lineEnd = lastWhitespace >= maxPos;
                result.add(new Whitespaces(line.substring(pos, lastWhitespace), lineEnd));
                pos = lastWhitespace;
            } else if (nextWhitespace == -1) {
                //если пробела нет до самого конца строки
                result.addAll(breakWord(line.substring(pos), true, lineBreak));
                pos = maxPos + 1;
            } else {
                result.addAll(breakWord(line.substring(pos, nextWhitespace), false, lineBreak));
                pos = nextWhitespace;
            }
        } while (pos <= maxPos);
        return result;
    }

    public static int whitespacePrefixLength(String s) {
        return whitespaceLength(s, 0);
    }

    private static List<Token> breakWord(String word, boolean isLast, String lineBreak) {
        List<Token> result = new LinkedList<>();
        boolean done = word.isEmpty();
        while (!done) {
            int breakPosition = word.indexOf(lineBreak);
            if (breakPosition == -1) {
                // если внутри слова нет разрвыа строки -- это одно слово
                result.add(new Word(word, isLast));
                done = true;
            } else {
                // а если есть, то впишем слово, разрыв, и продолжим искать после разрыва
                if (breakPosition != 0) {
                    // правда слово есть только если разрыв не идет первым в строке
                    result.add(new Word(word.substring(0, breakPosition), false));
                }
                word = word.substring(breakPosition + lineBreak.length());
                if (word.isEmpty()) {
                    result.add(new LineBreak(isLast));
                    done = true;
                } else {
                    result.add(new LineBreak(false));
                }
            }
        }
        return result;
    }


    /**
     * Следующий пробельный символ в строке начиная от заданной позиции
     *
     * @param s   строка
     * @param pos начальная позиция
     * @return позиция найденного пробельного симвала или -1 если до конца строки такого не найдено
     */
    static int findWhitespace(String s, int pos) {
        int i = pos;
        int len = s.length();
        boolean done = false;
        while (!done && i < len) {
            if (isWhitespace(s.charAt(i))) {
                done = true;
            } else {
                i++;
            }
        }
        return done ? i : -1;
    }

    /**
     * Число пробельных символов начиная с данной позиции
     */
    static int whitespaceLength(String s, int pos) {
        int l = 0;
        int len = s.length();
        boolean done = false;
        while (!done && pos < len) {
            if (isWhitespace(s.charAt(pos))) {
                l++;
                pos++;
            } else {
                done = true;
            }
        }
        return l;
    }

    static boolean isWhitespace(char c) {
        return WHITESPACES.indexOf(c) != -1;
    }


}
