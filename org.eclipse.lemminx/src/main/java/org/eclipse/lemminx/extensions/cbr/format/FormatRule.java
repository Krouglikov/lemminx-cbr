package org.eclipse.lemminx.extensions.cbr.format;

import org.eclipse.lemminx.dom.DOMNode;

/**
 * Правило форматирования.
 * Анализирует узел, и определяет его формат.
 */
public interface FormatRule {

    int kind();

    int sequence();

    /**
     * Применимо ли правило к узлу
     */
    boolean applicable(DOMNode node);

    /**
     * Применение правила определяет формат узла
     */
    ContextBoundFormat apply(DOMNode node);  // Format apply(Format f);

}
