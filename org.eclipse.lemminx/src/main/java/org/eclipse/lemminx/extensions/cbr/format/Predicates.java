package org.eclipse.lemminx.extensions.cbr.format;

import org.eclipse.lemminx.dom.DOMComment;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.dom.DOMText;
import org.eclipse.lemminx.extensions.cbr.format.execution.Context;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public final class Predicates {

    /**
     * Только элементы с такими именами будут форматироваться особым образом по правилам ДИТА
     */
    public static final List<String> DITA_BLOCK_ELEMENTS = Arrays.asList(
            "amendments", "anchor", "anchorref", "appendices", "appendix",
            "backmatter", "bookid", "booklibrary", "booklists", "bookmeta", "bookowner", "bookrights", "booktitle",
            "chapter", "choices", "conbody", "concept", "context", "copyrfirst", "copyright", "copyrlast", "critdates",
            "div",
            "example",
            "fig", "frontmatter",
            "glossAlt", "glossarylist", "glossBody", "glossentry", "glossref",
            "image", "isbn",
            "keydef", "keywords",
            "mainbooktitle", "maintainer", "map", "mapref",
            "note",
            "ol",
            "postreq", "prereq", "prolog", "prop",
            "refbody", "reference", "reltable", "row",
            "section", "step", "steps",
            "table", "task", "taskbody", "tgroup", "thead", "toc", "topichead", "topicmeta", "topicref", "topicsetref",
            "val"
    );

    public static Predicate<DOMNode> isText() {
        return DOMNode::isText;
    }

    /**
     * Узел не является текстовым
     */
    public static Predicate<DOMNode> isNotText() {
        return node -> !node.isText();
    }

    /**
     * Узел текстовый и состоит не только из пробелов
     */
    public static Predicate<DOMNode> isNotEmptyText() {
        return node -> !((DOMText) node).isWhitespace();
    }

    /**
     * Узел является всем документом
     */
    public static Predicate<DOMNode> isDocumentNode() {
        return node -> node.getNodeType() == DOMNode.DOCUMENT_NODE;
    }

    /**
     * Узел не является всем документом
     */
    public static Predicate<DOMNode> isNotDocumentNode() {
        return isDocumentNode().negate();
    }

    /**
     * Родительский документ элемента не есть DTD
     */
    public static Predicate<DOMNode> isNotPartOfDTD() {
        return node -> !node.getOwnerDocument().isDTD();
    }

    /**
     * Узел является комментарием
     */
    public static Predicate<DOMNode> isComment() {
        return DOMNode::isComment;
    }

    /**
     * Узел не является комментарием
     */
    public static Predicate<DOMNode> isNotComment() {
        return isComment().negate();
    }

    /**
     * Узел - однострочный комментарий
     */
    public static Predicate<DOMNode> isNotOneLineComment() {
        return node -> !(node.isComment() && ((DOMComment) node).isCommentSameLineEndTag());
    }

    /**
     * У текстового узла есть братья
     */
    public static Predicate<DOMNode> textHasSibnlings() {
        return node -> ((DOMText) node).hasSiblings();
    }

    public static Predicate<DOMNode> needAnotherLineFeed() {
        return isNotPartOfDTD()
                .and(isNotOneLineComment())
                .and(isNotText().or(isNotEmptyText().and(textHasSibnlings())));
    }

    public static Predicate<DOMNode> startTagExistsInRangeDocument() {
        return node -> {
            if (!node.isElement()) {
                return false;
            }

            return ((DOMElement) node).hasStartTag();
        };
    }

    public static Predicate<DOMNode> startTagExistsInFullDocument(Context ctx) {
        return node -> {
            if (!node.isElement()) {
                return false;
            }

            DOMElement elemFromFullDoc = ctx.getFullDocElemFromRangeElem((DOMElement) node);

            if (elemFromFullDoc == null) {
                return false;
            }

            return elemFromFullDoc.hasStartTag();
        };
    }

    /**
     * У узла есть вложенные помимо текста
     */
    public static Predicate<DOMNode> hasNonTextChildren() {
        return domNode -> {
            boolean hasElements = false;
            if (domNode.hasChildNodes()) {
                // element has body
                for (DOMNode child : domNode.getChildren()) {
                    hasElements = hasElements || !child.isText();
                }
            }
            return hasElements;
        };
    }

    /**
     * Блочный элемиент ДИТА
     */
    public static Predicate<DOMNode> isDitaBlockElement() {
        return node -> node.isElement() && DITA_BLOCK_ELEMENTS.contains(node.getNodeName());
    }

    /**
     * Не является блочным элементом ДИТА
     */
    public static Predicate<DOMNode> isNotDitaBlockElement() {
        return isDitaBlockElement().negate();
    }

    /**
     * Вложен в блочный элемент ДИТА
     */
    public static Predicate<DOMNode> hasDitaBlockParent() {
        return node ->
                node.getParentNode() != null
                        && isDitaBlockElement().test(node.getParentNode());
    }

}
