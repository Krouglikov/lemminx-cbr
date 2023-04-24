package org.eclipse.lemminx.extensions.cbr.format.rules;

/**
 * Последовательность этапов форматирования узла
 */
public enum FormattingOrder {

    BEFORE_HEAD,
    HEAD,
    AFTER_HEAD,
    BEFORE_CHILDREN,
    CHILDREN,
    AFTER_CHILDREN,
    BEFORE_TAIL,
    TAIL,
    AFTER_TAIL,
    UNDEFINED

}
