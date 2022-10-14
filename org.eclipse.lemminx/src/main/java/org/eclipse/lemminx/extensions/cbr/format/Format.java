package org.eclipse.lemminx.extensions.cbr.format;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.utils.XMLBuilder;

import java.util.function.BiConsumer;
import java.util.stream.Stream;

/**
 * Формат. Процедура, выводящая заданный узел при помощи заданного исполнительного механизма.
 * Процедура может быть частичной (то есть формировать не весь вывод содержимого узла)
 */
public interface Format extends BiConsumer<DOMNode, XMLBuilder> {

    Format withContext(Object context);

    Priority priority();

    Stream<Class<? extends Format>> overrides();

    enum Priority {

        /**
         * Основной формат
         */
        BASE,

        /**
         * Формат на замену основному
         */
        OVERRIDE

    }

}
