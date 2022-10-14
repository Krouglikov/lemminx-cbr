package org.eclipse.lemminx.extensions.cbr.format;

import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.cbr.format.execution.base.SequenceFormat;
import org.eclipse.lemminx.extensions.cbr.format.rules.*;

import java.util.*;
import java.util.stream.Collectors;

public class FormatConfiguration {

    private List<FormatRule> rules;

    private FormatConfiguration(FormatRule... rules) {
        this.rules = new LinkedList<>(Arrays.asList(rules));
    }

    /**
     * Базовое форматирование lemminx
     */
    public static FormatConfiguration lemminx() {
        return new FormatConfiguration(
                new PrintChildrenIfExist(),
                new NewLine(),
                new AnotherNewLineAndIndent(),
                new FormatElementHeadRule(),
                new IndentElementChildren(),
                new UnindentElementChildren(),
                new FormatElementBeforeTailRule(),
                new FormatElementTailRule(),
                new FormatCdataRule(),
                new FormatCommentRule(),
                new FormatDocumentTypeRule(),
                new FormatPrologTypeRule(),
                new FormatProcessingInstructionRule(),
                new FormatTextRule()
        );
    }

    /**
     * Форматирование с дополнительными правилами БР
     */
    public static FormatConfiguration cbr() {
        // lemminx as a base
        FormatConfiguration config = lemminx();
        // cbr-specific overrides
        config.rules.addAll(Arrays.asList(
                new OverrideCbrText(),
                new DitaNonBlockElements(),
                new BlockDitaElementBeforeTail()
        ));
        return config;
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
