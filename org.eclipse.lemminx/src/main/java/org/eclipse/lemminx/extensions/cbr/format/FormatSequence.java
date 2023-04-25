package org.eclipse.lemminx.extensions.cbr.format;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.library.FormattingOrder;
import org.eclipse.lemminx.extensions.cbr.format.library.base.*;
import org.eclipse.lemminx.extensions.cbr.format.library.dita.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.eclipse.lemminx.extensions.cbr.format.library.FormattingOrder.*;
import static org.eclipse.lemminx.extensions.cbr.format.library.Predicates.*;
import static org.eclipse.lemminx.extensions.cbr.format.library.Predicates.hasDitaBlockAncestor;

/**
 * Комбинация нескольких форматов.
 * Форматы применяются к узлу один за другим.
 */
public class FormatSequence {
    private List<Format> formats;
    private final Context ctx;

    public FormatSequence(Context ctx) {
        this.ctx = ctx;
    }

    public void doFormatting(DOMNode node) {
        formats = new ArrayList<>();

        // Новая строка должна вставляться перед нетекстовым и непустым текстовым элементом
        if (isNotText().or(isNotEmptyText()).test(node))
            formats.add(new NewLineIfContextDemands(node, ctx, BEFORE_HEAD));

        // Перед головой элемента вставляется дополнительный перенос строки и отступ если узел должен иметь отступ
        if ((isNotDocumentNode().and(isTypicalAsChild())).test(node))
            formats.add(new AnotherNewLineAndIndentIfIndented(node, ctx, BEFORE_HEAD));

        // Форматирование текста внутри блочного элемента Dita (связки между соседними элементами)
        if (isText().and(hasDitaBlockAncestor()).test(node))
            formats.add(new DitaBeforeTextFormat(node, ctx, BEFORE_HEAD));

        // Форматирование текста внутри блочного элемента Dita (связки между соседними элементами)
        if (isNotOneLineComment().and(isNotComment()).and(isNotText())
                .and(isNotDitaBlockElement()).and(hasDitaBlockAncestor()).test(node))
            formats.add(new DitaBeforeNonBlockElementFormat(node, ctx, BEFORE_HEAD));

        // Формат головы элемента
        if (node.isElement())
            formats.add(new FormatElementHead(node, ctx, HEAD));

        // Узлы CDATA форматируются по собственным правилам
        if (node.isCDATA())
            formats.add(new FormatCData(node, ctx, HEAD));

        //Комментарии форматируются по своим собственным правилам
        if (node.isComment())
            formats.add(new FormatComment(node, ctx, HEAD));

        // Узлы DOCTYPE форматируются по своим собственным правилам.
        if (node.isDoctype())
            formats.add(new FormatDocumentType(node, ctx, HEAD));

        // Инструкции форматируются по собственным правилам
        if (node.isProcessingInstruction())
            formats.add(new FormatProcessingInstruction(node, ctx, HEAD));

        // Пролог форматируется по собственным правилам
        if (node.isProlog())
            formats.add(new FormatProlog(node, ctx, HEAD));

        // Форматирование текста внутри блочного элемента Dita по особым правилам (с учетом длины строки)
        if (isText().and(hasDitaBlockAncestor()).test(node))
            formats.add(new DitaTextFormat(node, ctx, HEAD));

        // Форматирование текста
        if (isText().test(node))
            formats.add(new FormatText(node, ctx, HEAD));

        // Особое правило для головы элементов в контексте Dita (внутри блочного элемента Dita)
        // кроме комментариев, текста, не являющихся блочными элементами Dita
        if (isNotOneLineComment().and(isNotComment()).and(isNotText())
                .and(isNotDitaBlockElement()).and(hasDitaBlockAncestor()).test(node))
            formats.add(new DitaContentsHeadFormat(node, ctx, HEAD));

        // Формат после головы элемента Dita
        if (isDitaBlockElement().and(isNotSelfClosed()).test(node))
            formats.add(new DitaNewLineAndIndentAfterBlockElementHead(node, ctx, AFTER_HEAD));

        // Перед вложенными элементами увеличивается отступ
        if (node.isElement())
            formats.add(new IncreaseIndent(node, ctx, BEFORE_CHILDREN));

        // Если у узла есть дочерние, их нужно выводить.
        if (node.hasChildNodes() && !node.isDoctype())
            formats.add(new ChildrenFormat(node, ctx, CHILDREN));

        // UnindentElementChildren
        if (node.isElement())
            formats.add(new DecreaseIndent(node, ctx, AFTER_CHILDREN));

        // Перед хвостом блочного элемента ДИТА (кроме самозакрывающихся) -- перенос строки и отступ
        if (isDitaBlockElement().and(isNotSelfClosed()).test(node))
            formats.add(new DitaNewLineAndIndentBeforeBlockElementTail(node, ctx, BEFORE_TAIL));

        // Элементы перед хвостовиком
        if (node.isElement())
            formats.add(new FormatElementBeforeTail(node, ctx, BEFORE_TAIL));

        // Особое правило для хвоста элементов в контексте Dita (внутри блочного элемента Dita)
        // кроме комментариев, текста, не являющихся блочными элементами Dita
        if (isNotOneLineComment().and(isNotComment()).and(isNotText())
                .and(isNotDitaBlockElement()).and(hasDitaBlockAncestor()).test(node))
            formats.add(new DitaContentsTailFormat(node, ctx, TAIL));

        // Формат хвоста элемента
        if (node.isElement())
            formats.add(new FormatElementTail(node, ctx, TAIL));

        if (formats.isEmpty()) {
            throw new IllegalStateException("No rules applicable. Node '" + node.getNodeName() +
                    "' will be lost during formatting");
        }
        overrideFormats();
        formats.forEach(Format::doFormatting);

    }

    void overrideFormats() {
        override(DitaTextFormat.class,
                FormatText.class);

        override(DitaBeforeNonBlockElementFormat.class,
                NewLineIfContextDemands.class,
                AnotherNewLineAndIndentIfIndented.class);

        override(DitaNewLineAndIndentBeforeBlockElementTail.class,
                FormatElementBeforeTail.class);

        override(DitaContentsHeadFormat.class,
                FormatElementHead.class,
                IncreaseIndent.class,
                DecreaseIndent.class,
                FormatElementBeforeTail.class,
                FormatElementTail.class);

        override(DitaContentsTailFormat.class,
                NewLineIfContextDemands.class,
                AnotherNewLineAndIndentIfIndented.class,
                FormatElementHead.class,
                FormatElementBeforeTail.class,
                FormatElementTail.class);
    }

    @SafeVarargs
    final void override(Class<? extends Format> newFormat, Class<? extends Format>... list) {
        List.copyOf(formats).forEach(format -> {
            if (format.getClass().equals(newFormat))
                Arrays.stream(list).forEach(fmt -> formats.removeIf(f -> f.getClass().equals(fmt)));
        });
    }
}
