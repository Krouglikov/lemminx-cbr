package org.eclipse.lemminx.extensions.cbr.format;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.execution.base.SequenceFormat;
import org.eclipse.lemminx.extensions.cbr.format.rules.children.IndentElementChildrenRule;
import org.eclipse.lemminx.extensions.cbr.format.rules.children.PrintChildrenIfExistRule;
import org.eclipse.lemminx.extensions.cbr.format.rules.children.UnindentElementChildrenRule;
import org.eclipse.lemminx.extensions.cbr.format.rules.head.*;
import org.eclipse.lemminx.extensions.cbr.format.rules.special.*;
import org.eclipse.lemminx.extensions.cbr.format.rules.tail.DitaBlockElementBeforeTailRule;
import org.eclipse.lemminx.extensions.cbr.format.rules.tail.FormatElementBeforeTailRule;
import org.eclipse.lemminx.extensions.cbr.format.rules.tail.FormatElementTailRule;

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

    public FormatConfiguration(FormatRule... rules) {
        this.rules = new LinkedList<>(Arrays.asList(rules));
    }

    public FormatConfiguration(FormatRuleGroup... ruleGroups) {
        this.rules = Arrays.stream(ruleGroups).flatMap(FormatRuleGroup::stream).collect(Collectors.toList());
    }

    /**
     * Базовое форматирование lemminx
     */
    public static FormatConfiguration lemminx() {
        return new FormatConfiguration(
                XML_ELEMENT_RULES,
                FormatRuleGroup.single(new FormatCdataRule()),
                FormatRuleGroup.single(new FormatCommentRule()),
                FormatRuleGroup.single(new FormatDocumentTypeRule()),
                FormatRuleGroup.single(new FormatPrologTypeRule()),
                FormatRuleGroup.single(new FormatProcessingInstructionRule()),
                FormatRuleGroup.single(new FormatTextRule())
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

    public Format configure(DOMNode node) {
        List<Format> formats = rules.stream()
                .filter(r -> r.applicable(node))
                .sorted(Comparator.comparing(FormatRule::sequence))
                .map(r -> r.apply(node))
                .collect(Collectors.toList());
        if (formats.isEmpty()) {
            throw new IllegalStateException("No rules applicable");
        } else if (formats.size() == 1) {
            return formats.get(0);
        } else {
            List<Format> result = resolveFormatOverrides(formats);
            return new SequenceFormat(result);
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

}
