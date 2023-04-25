package org.eclipse.lemminx.extensions.cbr.format;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.library.Context;
import org.eclipse.lemminx.extensions.cbr.format.library.FormattingOrder;
import org.eclipse.lemminx.extensions.cbr.format.library.base.*;
import org.eclipse.lemminx.extensions.cbr.format.library.dita.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        // NewLineBeforeHeadRule BEFORE_HEAD
        if (isNotText().or(isNotEmptyText()).test(node))
            formats.add(new NewLineIfContextDemands(node, ctx, FormattingOrder.BEFORE_HEAD));

        // Перед головой элемента вставляется дополнительный перенос строки и отступ если узел должен иметь отступ
        // AnotherNewLineAndIndentBeforeHeadRule BEFORE_HEAD
        if ((isNotDocumentNode().and(isTypicalAsChild())).test(node))
            formats.add(new AnotherNewLineAndIndentIfIndented(node, ctx, FormattingOrder.BEFORE_HEAD));

        // Форматирование текста внутри блочного элемента ДИТА (связки между соседними элементами)
        // BeforeCbrTextRule BEFORE_HEAD
        if (isText().and(hasDitaBlockAncestor()).test(node))
            formats.add(new DitaBeforeTextFormat(node, ctx, FormattingOrder.BEFORE_HEAD));

        // Форматирование текста внутри блочного элемента ДИТА (связки между соседними элементами)
        // DitaBeforeNonBlockElementRule BEFORE_HEAD
        if (isNotOneLineComment().and(isNotComment()).and(isNotText())
                .and(isNotDitaBlockElement()).and(hasDitaBlockAncestor()).test(node))
            formats.add(new DitaBeforeNonBlockElementFormat(node, ctx, FormattingOrder.BEFORE_HEAD));

        // Формат головы элемента
        // FormatElementHeadRule HEAD
        if (node.isElement())
            formats.add(new FormatElementHead(node, ctx, FormattingOrder.HEAD));

        // Узлы CDATA форматируются по собственным правилам
        // FormatCdataRule 1
        if (node.isCDATA())
            formats.add(new FormatCData(node, ctx, FormattingOrder.HEAD));

        //Комментарии форматируются по своим собственным правилам
        // FormatCommentRule 1
        if (node.isComment())
            formats.add(new FormatComment(node, ctx, FormattingOrder.HEAD));

        // Узлы DOCTYPE форматируются по своим собственным правилам.
        // FormatDocumentTypeRule 1
        if (node.isDoctype())
            formats.add(new FormatDocumentType(node, ctx, FormattingOrder.HEAD));

        // Инструкции форматируются по собственным правилам
        // FormatProcessingInstructionRule 1
        if (node.isProcessingInstruction())
            formats.add(new FormatProcessingInstruction(node, ctx, FormattingOrder.HEAD));

        // Пролог форматируется по собственным правилам
        // FormatPrologTypeRule 1
        if (node.isProlog())
            formats.add(new FormatProlog(node, ctx, FormattingOrder.HEAD));

        // Форматирование текста внутри блочного элемента ДИТА по особым правилам (с учетом длины строки)
        // CbrTextRule HEAD
        if (isText().and(hasDitaBlockAncestor()).test(node))
            formats.add(new DitaTextFormat(node, ctx, FormattingOrder.HEAD));

        // Форматирование текста
        // FormatTextRule 1
        if (isText().test(node))
            formats.add(new FormatText(node, ctx, FormattingOrder.HEAD));

        // Особое правило для головы элементов в контексте ДИТА (внутри блочного элемента ДИТА)
        // кроме комментариев, текста, не являющихся блочными элементами ДИТА
        // DitaNonBlockElementHeadRule HEAD
        if (isNotOneLineComment().and(isNotComment()).and(isNotText())
                .and(isNotDitaBlockElement()).and(hasDitaBlockAncestor()).test(node))
            formats.add(new DitaContentsHeadFormat(node, ctx, FormattingOrder.HEAD));

        // Формат после головы элемента ДИТА
        // DitaBlockElementAfterHeadRule AFTER_HEAD
        if (isDitaBlockElement().and(isNotSelfClosed()).test(node))
            formats.add(new DitaNewLineAndIndentAfterBlockElementHead(node, ctx, FormattingOrder.AFTER_HEAD));

        // Перед вложенными элементами увеличивается отступ
        // IndentElementChildrenRule BEFORE_CHILDREN
        if (node.isElement())
            formats.add(new IncreaseIndent(node, ctx, FormattingOrder.BEFORE_CHILDREN));

        // Если у узла есть дочерние, их нужно выводить.
        // PrintChildrenIfExistRule CHILDREN
        if (node.hasChildNodes() && !node.isDoctype())
            formats.add(new ChildrenFormat(node, ctx, FormattingOrder.CHILDREN));

        // UnindentElementChildrenRule AFTER_CHILDREN
        if (node.isElement())
            formats.add(new DecreaseIndent(node, ctx, FormattingOrder.AFTER_CHILDREN));

        // Перед хвостом блочного элемента ДИТА (кроме самозакрывающихся) -- перенос строки и отступ
        // DitaBlockElementBeforeTailRule BEFORE_TAIL
        if (isDitaBlockElement().and(isNotSelfClosed()).test(node))
            formats.add(new DitaNewLineAndIndentBeforeBlockElementTail(node, ctx, FormattingOrder.BEFORE_TAIL));

        // Элементы перед хвостовиком
        // FormatElementBeforeTailRule BEFORE_TAIL
        if (node.isElement())
            formats.add(new FormatElementBeforeTail(node, ctx, FormattingOrder.BEFORE_TAIL));

        // Особое правило для хвоста элементов в контексте ДИТА (внутри блочного элемента ДИТА)
        // кроме комментариев, текста, не являющихся блочными элементами ДИТА
        // DitaNonBlockElementTailRule TAIL
        if (isNotOneLineComment().and(isNotComment()).and(isNotText())
                .and(isNotDitaBlockElement()).and(hasDitaBlockAncestor()).test(node))
            formats.add(new DitaContentsTailFormat(node, ctx, FormattingOrder.TAIL));

        // Формат хвоста элемента
        // FormatElementTailRule TAIL
        if (node.isElement())
            formats.add(new FormatElementTail(node, ctx, FormattingOrder.TAIL));

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
