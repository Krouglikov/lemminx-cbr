package org.eclipse.lemminx.extensions.cbr.format;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.execution.Context;
import org.eclipse.lemminx.extensions.cbr.format.execution.base.*;
import org.eclipse.lemminx.extensions.cbr.format.execution.dita.DitaNewLineAndIndentAfterBlockElementHead;
import org.eclipse.lemminx.extensions.cbr.format.execution.dita.DitaNewLineAndIndentBeforeBlockElementTail;
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

public class FormatConfiguration {

    private static final FormatRuleGroup XML_ELEMENT_RULES = new FormatRuleGroup(
            new NewLineBeforeHeadRule(),
            new AnotherNewLineAndIndentBeforHeadRule(),
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
                new DitaBlockElementBeforeTailRule(),
                new SpTestRule()
        ));
        return this;
    }

    public SequenceFormat getSequenceFormatForNode(DOMNode node) {
// ?????
        StringBuilder sb = new StringBuilder();
        rules.stream()
                .filter(r -> r.applicable(node))
                .sorted(Comparator.comparing(FormatRule::sequence))
                .forEach(formatRule -> {
                            sb.append("\n").append(formatRule.getClass().getSimpleName());
                            Format format = formatRule.apply(node);
                        }
                );

        LogToFile.getInstance().info("formatRules for node " + node.getNodeName() + " :" + sb);


        List<Format> formats = new ArrayList<>();

        if (LogToFile.debuggingMode != 0 && Predicates.isDitaBlockElement().test(node)) {
//            NewLineBeforeHeadRule
//            AnotherNewLineAndIndentBeforHeadRule
//            FormatElementHeadRule
//            DitaBlockElementAfterHeadRule
//            IndentElementChildrenRule
//            PrintChildrenIfExistRule
//            UnindentElementChildrenRule
//            FormatElementBeforeTailRule
//            DitaBlockElementBeforeTailRule
//            FormatElementTailRule
            formats = getFormatsForDiv();
        } else {
            formats = rules.stream()
                    .filter(r -> r.applicable(node))
                    .sorted(Comparator.comparing(FormatRule::sequence))
                    .map(r -> r.apply(node))
                    .collect(Collectors.toList());
        }

        if (formats.isEmpty()) {
            throw new IllegalStateException("No rules applicable");
//        } else if (formats.size() == 1) {
//            return formats.get(0);
        } else {
            List<Format> result = resolveFormatOverrides(formats);
            return new SequenceFormat(node, ctx, result);
        }
    }

    private List<Format> resolveFormatOverrides(List<Format> original) {
        ArrayList<Format> result = new ArrayList<>(original);
        original.stream()
                .filter(format -> format.priority() == Format.Priority.OVERRIDE)
                .flatMap(Format::overrides)
                .forEach(overriden ->
                        result.removeIf(format -> format.getClass() == overriden)
                );
        return result;
    }

    private List<Format> getFormatsForDiv() {
        return List.of(
                new NewLineIfContextDemands(),
                new AnotherNewLineAndIndentIfIndented(),
                new FormatElementHead(),
                new DitaNewLineAndIndentAfterBlockElementHead(),
                new IncreaseIndent(),
                new ChildrenFormat(),
                new DecreaseIndent(),
                new FormatElementBeforeTail(),
                new DitaNewLineAndIndentBeforeBlockElementTail(),
                new FormatElementTail());
    }


}
