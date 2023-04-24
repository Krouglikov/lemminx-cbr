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
    private static List<String> DITA_BLOCK_ELEMENTS = Arrays.asList(
            "amendments", "anchor", "anchorref", "appendices", "appendix",
            "backmatter", "bookid", "booklibrary", "booklists", "bookmeta", "bookowner", "bookrights", "booktitle",
            "chapter", "choices", "conbody", "concept", "context", "copyrfirst", "copyright", "copyrlast", "critdates",
            "data", "div",
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
            "section", "sl", "step", "steps",
            "table", "task", "taskbody", "tgroup", "thead", "toc", "topichead", "topicmeta", "topicref", "topicsetref",
            "ul",
            "val"
    );

    public static void setDitaBlockElements(final List<String> ditaBlockElements) {
        DITA_BLOCK_ELEMENTS = ditaBlockElements;
    }

    public static Predicate<DOMNode> isText() {
        return DOMNode::isText;
    }

    /**
     * Узел не является текстовым
     */
//    public static Predicate<DOMNode> isNotText() {
//        return node -> !node.isText();
//    }
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
    public static Predicate<DOMNode> textHasSiblings() {
        return node -> ((DOMText) node).hasSiblings();
    }

    /**
     * Узел с типичным поведением в роли потомка:
     * <ul>
     *     <li>Не является частью DTD-элемента;</li>
     *     <li>Не является однострочным комментарием;</li>
     *     <li>Не является текстом, а если и текст, то непустой и не единственный вложенный элемент.</li>
     * </ul>
     */
    public static Predicate<DOMNode> isTypicalAsChild() {
        return isNotPartOfDTD()
                .and(isNotOneLineComment())
                .and(isNotText().or(isNotEmptyText().and(textHasSiblings())));
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
     * У узла нет атрибутов
     */
    public static Predicate<DOMNode> hasNoAttributes() {
        return node -> node.getAttributeNodes() == null || node.getAttributeNodes().isEmpty();
    }

    /**
     * У узла нет потомков
     */
    public static Predicate<DOMNode> isChildFree() {
        return node -> node.getChildren() == null || node.getChildren().isEmpty();
    }

    /**
     * В единственном потомке текст
     */
    public static Predicate<DOMNode> isTextOnly() {
        return node -> node.getChildren() != null && node.getChildren().size() == 1 && node.getChild(0).isText();
    }

    /**
     * Узел является самозакрывающимся элементом (<elementName/>)
     */
    public static Predicate<DOMNode> isSelfClosed() {
        return node -> node.isElement() && ((DOMElement) node).isSelfClosed();
    }

    /**
     * Узел не является самозакрывающимся элементом (<elementName/>)
     */
    public static Predicate<DOMNode> isNotSelfClosed() {
        return isSelfClosed().negate();
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
     * Вложен в блочный элемент ДИТА (непосредственно, на первом уровне вложенности)
     */
    public static Predicate<DOMNode> hasDitaBlockParent() {
        return node -> node.getParentNode() != null
                && isDitaBlockElement().test(node.getParentNode());
    }

    /**
     * Вложен в блочный элемент ДИТА (возможно на n-ном уровне вложенности)
     */
    public static Predicate<DOMNode> hasDitaBlockAncestor() {
        return node -> {
            DOMNode parentNode = node.getParentNode();
            if (parentNode == null) {
                return false;
            } else if (isDitaBlockElement().test(parentNode)) {
                return true;
            } else {
                return (hasDitaBlockAncestor().test(parentNode));
            }
        };
    }

    public static Predicate<DOMNode> isEmptyOrWhitespaceOnlyText() {
        return isText().and(
                nd -> isEmptyOrWhitespaceOnly(((DOMText) nd).getData()));
    }

    public static boolean isEmptyOrWhitespaceOnly(String s) {
        String whitespaces = " \t\r\n";
        if (s == null || s.isEmpty()) {
            return true;
        } else {
            // как только найдем не-пробельный символ, сразу ясен итог
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (whitespaces.indexOf(c) == -1) {
                    return false;
                }
            }
            //если не нашли непробельных символов, то все
            return true;
        }
    }

}
