package org.eclipse.lemminx.extensions.cbr.format;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.execution.Context;
import org.eclipse.lemminx.extensions.cbr.format.execution.base.*;
import org.eclipse.lemminx.extensions.cbr.format.execution.dita.*;
import org.eclipse.lemminx.extensions.cbr.format.rules.FormattingOrder;
import org.eclipse.lemminx.extensions.cbr.format.rules.SimpleFormatRule;
import org.eclipse.lemminx.extensions.cbr.format.rules.children.IndentElementChildrenRule;
import org.eclipse.lemminx.extensions.cbr.format.rules.children.PrintChildrenIfExistRule;
import org.eclipse.lemminx.extensions.cbr.format.rules.children.UnindentElementChildrenRule;
import org.eclipse.lemminx.extensions.cbr.format.rules.head.*;
import org.eclipse.lemminx.extensions.cbr.format.rules.special.*;
import org.eclipse.lemminx.extensions.cbr.format.rules.tail.DitaBlockElementBeforeTailRule;
import org.eclipse.lemminx.extensions.cbr.format.rules.tail.FormatElementBeforeTailRule;
import org.eclipse.lemminx.extensions.cbr.format.rules.tail.FormatElementTailRule;
import org.eclipse.lemminx.logs.LogToFile;

import java.util.*;
import java.util.stream.Collectors;

import static org.eclipse.lemminx.extensions.cbr.format.Predicates.*;

public class FormatConfiguration {

    private static final FormatRuleGroup XML_ELEMENT_RULES = new FormatRuleGroup(
            new NewLineBeforeHeadRule(),
            new AnotherNewLineAndIndentBeforeHeadRule(),
            new FormatElementHeadRule(),
            new IndentElementChildrenRule(),
            new PrintChildrenIfExistRule(),
            new UnindentElementChildrenRule(),
            new FormatElementBeforeTailRule(),
            new FormatElementTailRule()
    );

    private final List<FormatRule> rules;

    private Context ctx;

    public FormatConfiguration(FormatRule... rules) {
        this.rules = new LinkedList<>(Arrays.asList(rules));
    }

    public FormatConfiguration(FormatRuleGroup... ruleGroups) {
        this.rules = Arrays.stream(ruleGroups).flatMap(FormatRuleGroup::stream).collect(Collectors.toList());
    }

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
        return new FormatConfiguration(
                XML_ELEMENT_RULES,
                new FormatRuleGroup(
                        new FormatCdataRule(),
                        new FormatCommentRule(),
                        new FormatDocumentTypeRule(),
                        new FormatPrologTypeRule(),
                        new FormatProcessingInstructionRule(),
                        new FormatTextRule()
                )
        );
    }

    /**
     * Форматирование с дополнительными правилами БР
     */
    public static FormatConfiguration cbr() {
        // lemminx as a base and cbr-specific overrides
        return lemminx().cbrOverrides();
    }

    /**
     * Правила форматирования, специфические для Банка России
     */
    public FormatConfiguration cbrOverrides() {
        this.rules.addAll(Arrays.asList(
                new BeforeCbrTextRule(),
                new CbrTextRule(),
                new DitaBeforeNonBlockElementRule(),
                new DitaNonBlockElementHeadRule(),
                new DitaNonBlockElementTailRule(),
                new DitaBlockElementAfterHeadRule(),
                new DitaBlockElementBeforeTailRule()
        ));
        return this;
    }

    public FormatSequence getSequenceFormatForNode(DOMNode node) {
        StringBuilder sb = new StringBuilder();
        String gap = Predicates.isDitaBlockElement().test(node) ? "    " : "        ";
        rules.stream()
                .filter(r -> r.applicable(node))
                .sorted(Comparator.comparing(FormatRule::sequence))
                .forEach(formatRule -> {
                            sb.append("\n").append(gap).append(formatRule.getClass().getSimpleName());
                            Format format = formatRule.apply(node);
                        }
                );

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
