package org.eclipse.lemminx.extensions.cbr.format;

import java.util.Arrays;
import java.util.stream.Stream;

public class FormatRuleGroup {

    private FormatRule[] elements;

    public FormatRuleGroup(FormatRule... elements) {
        this.elements = elements;
    }

    public Stream<FormatRule> stream() {
        return Arrays.stream(elements);
    }

    public static FormatRuleGroup single(FormatRule rule) {
        return new FormatRuleGroup(rule);
    }

}
