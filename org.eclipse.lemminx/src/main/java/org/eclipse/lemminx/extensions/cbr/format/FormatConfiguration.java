package org.eclipse.lemminx.extensions.cbr.format;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.execution.Context;
import org.eclipse.lemminx.extensions.cbr.format.execution.base.*;
import org.eclipse.lemminx.extensions.cbr.format.execution.dita.*;
import org.eclipse.lemminx.logs.LogToFile;

import java.util.*;
import java.util.stream.Collectors;

import static org.eclipse.lemminx.extensions.cbr.format.Predicates.*;

public class FormatConfiguration {
    private Context ctx;

    public Context getCtx() {
        return ctx;
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }

    /**
     * Базовое форматирование lemminx
     */
    public static FormatConfiguration lemminx() {
        return new FormatConfiguration();
    }

    public FormatSequence getSequenceFormatForNode(DOMNode node) {
        StringBuilder sb = new StringBuilder();
        String gap = Predicates.isDitaBlockElement().test(node) ? "    " : "        ";

        LogToFile.getInstance().info("formatRules for node " + node.getNodeName() +
                (Predicates.isDitaBlockElement().test(node) ? " (Block element)" : " (Non-block element)") + ":" + sb);

        List<ContextBoundFormat> formats = new ArrayList<>();


        // Перед головой элемента вставляется дополнительный перенос строки и отступ если узел должен иметь отступ
        // AnotherNewLineAndIndentBeforeHeadRule BEFORE_HEAD
        if ((isNotDocumentNode().and(isTypicalAsChild())).test(node))
            formats.add(new AnotherNewLineAndIndentIfIndented());

        // Форматирование текста внутри блочного элемента ДИТА (связки между соседними элементами)
        // DitaBeforeNonBlockElementRule BEFORE_HEAD
        if (isNotOneLineComment().and(isNotComment()).and(isNotText())
                .and(isNotDitaBlockElement()).and(hasDitaBlockAncestor()).test(node))
            formats.add(new DitaBeforeNonBlockElementFormat());

        // Новая строка должна вставляться перед нетекстовым и непустым текстовым элементом
        // NewLineBeforeHeadRule BEFORE_HEAD
        if (isNotText().or(isNotEmptyText()).test(node))
            formats.add(new NewLineIfContextDemands());

        // Форматирование текста внутри блочного элемента ДИТА (связки между соседними элементами)
        // BeforeCbrTextRule BEFORE_HEAD
        if (isText().and(hasDitaBlockAncestor()).test(node))
            formats.add(new DitaBeforeTextFormat());

        // Особое правило для головы элементов в контексте ДИТА (внутри блочного элемента ДИТА)
        // кроме комментариев, текста, не являющихся блочными элементами ДИТА
        // DitaNonBlockElementHeadRule HEAD
        if (isNotOneLineComment().and(isNotComment()).and(isNotText())
                .and(isNotDitaBlockElement()).and(hasDitaBlockAncestor()).test(node))
            formats.add(new DitaContentsHeadFormat());

        // Формат головы элемента
        // FormatElementHeadRule HEAD
        if (node.isElement())
            formats.add(new FormatElementHead());

        // Форматирование текста внутри блочного элемента ДИТА по особым правилам (с учетом длины строки)
        // CbrTextRule HEAD
        if (isText().and(hasDitaBlockAncestor()).test(node))
            formats.add(new DitaTextFormat());

        // Узлы CDATA форматируются по собственным правилам
        // FormatCdataRule 1
        if (node.isCDATA())
            formats.add(new FormatCData());

        //Комментарии форматируются по своим собственным правилам
        // FormatCommentRule 1
        if (node.isComment())
            formats.add(new FormatComment());

        // Узлы DOCTYPE форматируются по своим собственным правилам.
        // FormatDocumentTypeRule 1
        if (node.isDoctype())
            formats.add(new FormatDocumentType());

        // Инструкции форматируются по собственным правилам
        // FormatProcessingInstructionRule 1
        if (node.isProcessingInstruction())
            formats.add(new FormatProcessingInstruction());

        // Пролог форматируется по собственным правилам
        // FormatPrologTypeRule 1
        if (node.isProlog())
            formats.add(new FormatProlog());

        // Форматирование текста
        // FormatTextRule 1
        if (isText().test(node))
            formats.add(new FormatText());

        //  Формат после головы элемента ДИТА
        // DitaBlockElementAfterHeadRule AFTER_HEAD
        if (isDitaBlockElement().and(isNotSelfClosed()).test(node))
            formats.add(new DitaNewLineAndIndentAfterBlockElementHead());

        // Перед вложенными элементами увеличивается отступ
        // IndentElementChildrenRule BEFORE_CHILDREN
        if (node.isElement())
            formats.add(new IncreaseIndent());

        // Если у узла есть дочерние, их нужно выводить.
        // PrintChildrenIfExistRule CHILDREN
        if (node.hasChildNodes() && !node.isDoctype())
            formats.add(new ChildrenFormat());

        // UnindentElementChildrenRule AFTER_CHILDREN
        if (node.isElement())
            formats.add(new DecreaseIndent());

        // Перед хвостом блочного элемента ДИТА (кроме самозакрывающихся) -- перенос строки и отступ
        // DitaBlockElementBeforeTailRule BEFORE_TAIL
        if (isDitaBlockElement().and(isNotSelfClosed()).test(node))
            formats.add(new DitaNewLineAndIndentBeforeBlockElementTail());

        // Элементы перед хвостовиком
        // FormatElementBeforeTailRule BEFORE_TAIL
        if (node.isElement())
            formats.add(new FormatElementBeforeTail());

        // Формат хвоста элемента
        // FormatElementTailRule TAIL
        if (node.isElement())
            formats.add(new FormatElementTail());

        // Особое правило для хвоста элементов в контексте ДИТА (внутри блочного элемента ДИТА)
        // кроме комментариев, текста, не являющихся блочными элементами ДИТА
        // DitaNonBlockElementTailRule TAIL
        if (isNotOneLineComment().and(isNotComment()).and(isNotText())
                .and(isNotDitaBlockElement()).and(hasDitaBlockAncestor()).test(node))
            formats.add(new DitaContentsTailFormat());
/*
        formats = rules.stream()
                .filter(r -> r.applicable(node)).
                sorted(Comparator.comparing(FormatRule::sequence))
                .map(r -> r.apply(node))
                .collect(Collectors.toList());
*/
        if (formats.isEmpty()) {
            throw new IllegalStateException("No rules applicable. Node '" + node.getNodeName() +
                    "' will be lost during formatting");
//        } else if (formats.size() == 1) {
//            return formats.get(0);
        } else {
            List<ContextBoundFormat> result = resolveFormatOverrides(formats); // Should be done?
            return new FormatSequence(node, ctx, result);
        }

    }

    private List<ContextBoundFormat> resolveFormatOverrides(List<ContextBoundFormat> original) {
        ArrayList<ContextBoundFormat> result = new ArrayList<>(original);
        original.stream()
                .filter(format -> format.priority() == Format.Priority.OVERRIDE)
                .flatMap(Format::overrides)
                .forEach(overridden ->
                        result.removeIf(format -> format.getClass() == overridden)
                );

        return result;
    }

}
