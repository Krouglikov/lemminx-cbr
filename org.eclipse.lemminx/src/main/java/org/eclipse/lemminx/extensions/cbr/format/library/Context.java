package org.eclipse.lemminx.extensions.cbr.format.library;

import org.eclipse.lemminx.commons.BadLocationException;
import org.eclipse.lemminx.commons.TextDocument;
import org.eclipse.lemminx.dom.*;
import org.eclipse.lemminx.extensions.cbr.format.FormatSequence;
import org.eclipse.lemminx.services.extensions.format.IFormatterParticipant;
import org.eclipse.lemminx.settings.SharedSettings;
import org.eclipse.lemminx.settings.XMLFormattingOptions;
import org.eclipse.lemminx.utils.XMLBuilder;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextEdit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Context {

    //region Fields

    //region Stolen from lemminx

    public DOMDocument fullDomDocument;
    public DOMDocument rangeDomDocument; //todo remove public
    int startOffset;
    public int endOffset;
    Range range;
    TextDocument textDocument;
    public SharedSettings sharedSettings;
    Collection<IFormatterParticipant> formatterParticipants;
    XMLFormattingOptions.EmptyElements emptyElements;

    private boolean withinDTDContent;
    public boolean linefeedOnNextWrite; //todo remove public

    //endregion

    // region additional
    public FormatSequence formatSequence;

    // todo remove?
    public XMLBuilder xmlBuilder; //todo remove

    public int indentLevel;

    //endregion

    //endregion

    public Context(TextDocument textDocument, Range range, SharedSettings sharedSettings,
                   Collection<IFormatterParticipant> formatterParticipants) {
        this.textDocument = textDocument;
        this.range = range;
        this.sharedSettings = sharedSettings;
        this.formatterParticipants = formatterParticipants;
        this.emptyElements = sharedSettings.getFormattingSettings().getEmptyElements();
        this.linefeedOnNextWrite = false;
        try {
            setup();
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
    }

    public List<? extends TextEdit> getFormatTextEdit() throws BadLocationException {
        Position startPosition = this.textDocument.positionAt(this.startOffset);
        Position endPosition = this.textDocument.positionAt(this.endOffset);
        Range r = new Range(startPosition, endPosition);
        List<TextEdit> edits = new ArrayList<>();

        // check if format range reaches the end of the document
        if (this.endOffset == this.textDocument.getText().length()) {

            if (this.sharedSettings.getFormattingSettings().isTrimFinalNewlines()) {
                this.xmlBuilder.trimFinalNewlines();
            }

            if (this.sharedSettings.getFormattingSettings().isInsertFinalNewline()
                    && !this.xmlBuilder.isLastLineEmptyOrWhitespace()) {
                this.xmlBuilder.linefeed();
            }
        }

        edits.add(new TextEdit(r, this.xmlBuilder.toString()));
        return edits;
    }

    //region setup
    private void setup() throws BadLocationException {
        this.fullDomDocument = DOMParser.getInstance()
                .parse(textDocument.getText(), textDocument.getUri(), null, false);

        if (isRangeFormatting()) {
            setupRangeFormatting(range);
        } else {
            setupFullFormatting(range);
        }

        this.indentLevel = getStartingIndentLevel();
    }

    private void setupRangeFormatting(Range range) throws BadLocationException {
        int startOffset = this.textDocument.offsetAt(range.getStart());
        int endOffset = this.textDocument.offsetAt(range.getEnd());

        Position startPosition = this.textDocument.positionAt(startOffset);
        Position endPosition = this.textDocument.positionAt(endOffset);
        enlargePositionToGutters(startPosition, endPosition);

        this.startOffset = this.textDocument.offsetAt(startPosition);
        this.endOffset = this.textDocument.offsetAt(endPosition);

        String fullText = this.textDocument.getText();
        String rangeText = fullText.substring(this.startOffset, this.endOffset);

        withinDTDContent = this.fullDomDocument.isWithinInternalDTD(startOffset);
        String uri = this.textDocument.getUri();
        if (withinDTDContent) {
            uri += ".dtd";
        }
        this.rangeDomDocument = DOMParser.getInstance().parse(rangeText, uri, null, false);

        if (containsTextWithinStartTag()) {
            adjustOffsetToStartTag();
            rangeText = fullText.substring(this.startOffset, this.endOffset);
            this.rangeDomDocument = DOMParser.getInstance().parse(rangeText, uri, null, false);
        }

        this.xmlBuilder = newXmlBuilder(startPosition);
    }

    XMLBuilder newXmlBuilder(Position startPosition) throws BadLocationException {
        return new XMLBuilder(this.sharedSettings, "",
                textDocument.lineDelimiter(startPosition.getLine()), formatterParticipants);
    }

    public XMLBuilder newXmlBuilder(String lineDelimiter) {
        return new XMLBuilder(this.sharedSettings, "", lineDelimiter, formatterParticipants);
    }

    private void setupFullFormatting(Range range) throws BadLocationException {
        this.startOffset = 0;
        this.endOffset = textDocument.getText().length();
        this.rangeDomDocument = this.fullDomDocument;

        Position startPosition = textDocument.positionAt(startOffset);
        this.xmlBuilder = newXmlBuilder(startPosition);
    }

    //endregion

    //region utility methods

    private int getStartingIndentLevel() throws BadLocationException {
        if (withinDTDContent) {
            return 1;
        }
        DOMNode startNode = this.fullDomDocument.findNodeAt(this.startOffset);
        if (startNode.isOwnerDocument()) {
            return 0;
        }

        DOMNode startNodeParent = startNode.getParentNode();

        if (startNodeParent.isOwnerDocument()) {
            return 0;
        }

        // the starting indent level is the parent's indent level + 1
        int startNodeIndentLevel = getNodeIndentLevel(startNodeParent) + 1;
        return startNodeIndentLevel;
    }

    private boolean containsTextWithinStartTag() {

        if (this.rangeDomDocument.getChildren().size() < 1) {
            return false;
        }

        DOMNode firstChild = this.rangeDomDocument.getChild(0);
        if (!firstChild.isText()) {
            return false;
        }

        int tagContentOffset = firstChild.getStart();
        int fullDocOffset = getFullOffsetFromRangeOffset(tagContentOffset);
        DOMNode fullNode = this.fullDomDocument.findNodeAt(fullDocOffset);

        if (!fullNode.isElement()) {
            return false;
        }
        return ((DOMElement) fullNode).isInStartTag(fullDocOffset);
    }

    private void adjustOffsetToStartTag() throws BadLocationException {
        int tagContentOffset = this.rangeDomDocument.getChild(0).getStart();
        int fullDocOffset = getFullOffsetFromRangeOffset(tagContentOffset);
        DOMNode fullNode = this.fullDomDocument.findNodeAt(fullDocOffset);
        Position nodePosition = this.textDocument.positionAt(fullNode.getStart());
        nodePosition.setCharacter(0);
        this.startOffset = this.textDocument.offsetAt(nodePosition);
    }

    private void enlargePositionToGutters(Position start, Position end) throws BadLocationException {
        start.setCharacter(0);

        if (end.getCharacter() == 0 && end.getLine() > 0) {
            end.setLine(end.getLine() - 1);
        }

        end.setCharacter(this.textDocument.lineText(end.getLine()).length());
    }

    public DOMElement getFullDocElemFromRangeElem(DOMElement elemFromRangeDoc) { //todo remove public
        int fullOffset = -1;

        if (elemFromRangeDoc.hasStartTag()) {
            fullOffset = getFullOffsetFromRangeOffset(elemFromRangeDoc.getStartTagOpenOffset()) + 1;
            // +1 because offset must be here: <|root
            // for DOMNode.findNodeAt() to find the correct element
        } else if (elemFromRangeDoc.hasEndTag()) {
            fullOffset = getFullOffsetFromRangeOffset(elemFromRangeDoc.getEndTagOpenOffset()) + 1;
            // +1 because offset must be here: <|/root
            // for DOMNode.findNodeAt() to find the correct element
        } else {
            return null;
        }

        DOMElement elemFromFullDoc = (DOMElement) this.fullDomDocument.findNodeAt(fullOffset);
        return elemFromFullDoc;
    }

    int getFullOffsetFromRangeOffset(int rangeOffset) {
        return rangeOffset + this.startOffset;
    }

    public int getNodeIndentLevel(DOMNode node) throws BadLocationException {

        Position nodePosition = this.textDocument.positionAt(node.getStart());
        String textBeforeNode = this.textDocument.lineText(nodePosition.getLine()).substring(0,
                nodePosition.getCharacter() + 1);

        int spaceOrTab = getSpaceOrTabStartOfString(textBeforeNode);

        if (this.sharedSettings.getFormattingSettings().isInsertSpaces()) {
            return (spaceOrTab / this.sharedSettings.getFormattingSettings().getTabSize());
        }
        return spaceOrTab;
    }

    int getSpaceOrTabStartOfString(String string) {
        int i = 0;
        int spaceOrTab = 0;
        while (i < string.length() && (string.charAt(i) == ' ' || string.charAt(i) == '\t')) {
            spaceOrTab++;
            i++;
        }
        return spaceOrTab;
    }

    /**
     * Returns true if first offset and second offset belong in the same line of the
     * document
     * <p>
     * If current formatting is range formatting, the provided offsets must be
     * ranged offsets (offsets relative to the formatting range)
     *
     * @param first  the first offset
     * @param second the second offset
     * @return true if first offset and second offset belong in the same line of the
     * document
     * @throws BadLocationException
     */
    public boolean isSameLine(int first, int second) throws BadLocationException {
        if (isRangeFormatting()) {
            // adjust range offsets so that they are relative to the full document
            first = getFullOffsetFromRangeOffset(first);
            second = getFullOffsetFromRangeOffset(second);
        }
        return getLineNumber(first) == getLineNumber(second);
    }

    private boolean isRangeFormatting() {
        return this.range != null;
    }

    private int getLineNumber(int offset) throws BadLocationException {
        return this.textDocument.positionAt(offset).getLine();
    }

    /**
     * Returns true if the provided element has one attribute in the fullDomDocument
     * (not the rangeDomDocument)
     *
     * @param element
     * @return true if the provided element has one attribute in the fullDomDocument
     * (not the rangeDomDocument)
     */
    public boolean hasSingleAttributeInFullDoc(DOMElement element) {
        DOMElement fullElement = getFullDocElemFromRangeElem(element);
        return fullElement.getAttributeNodes().size() == 1;
    }


    /**
     * Return the option to use to generate empty elements.
     *
     * @param element the DOM element
     * @return the option to use to generate empty elements.
     */
    public XMLFormattingOptions.EmptyElements getEmptyElements(DOMElement element) {
        if (this.emptyElements != XMLFormattingOptions.EmptyElements.ignore) {
            if (element.isClosed() && element.isEmpty()) {
                // Element is empty and closed
                switch (this.emptyElements) {
                    case expand:
                    case collapse: {
                        if (this.sharedSettings.getFormattingSettings().isPreserveEmptyContent()) {
                            // preserve content
                            if (element.hasChildNodes()) {
                                // The element is empty and contains somes spaces which must be preserved
                                return XMLFormattingOptions.EmptyElements.ignore;
                            }
                        }
                        return this.emptyElements;
                    }
                    default:
                        return this.emptyElements;
                }
            }
        }
        return XMLFormattingOptions.EmptyElements.ignore;
    }


    public DOMAttr getLastAttribute(DOMElement element) {
        if (!element.hasAttributes()) {
            return null;
        }
        List<DOMAttr> attributes = element.getAttributeNodes();
        return attributes.get(attributes.size() - 1);
    }

    public void formatAttributes(DOMElement element, XMLBuilder xmlBuilder) throws BadLocationException {
        List<DOMAttr> attributes = element.getAttributeNodes();
        boolean isSingleAttribute = hasSingleAttributeInFullDoc(element);
        int prevOffset = element.getStart();
        for (DOMAttr attr : attributes) {
            formatAttribute(attr, isSingleAttribute, prevOffset, xmlBuilder);
            prevOffset = attr.getEnd();
        }
        XMLFormattingOptions options = sharedSettings.getFormattingSettings();
        if ((options.getClosingBracketNewLine()
                && options.isSplitAttributes())
                && !isSingleAttribute) {
            xmlBuilder.linefeed();
            // Indent by tag + splitAttributesIndentSize to match with attribute indent level
            int totalIndent = indentLevel + options.getSplitAttributesIndentSize();
            xmlBuilder.indent(totalIndent);
        }
    }

    private void formatAttribute(DOMAttr attr, boolean isSingleAttribute, int prevOffset, XMLBuilder xmlBuilder)
            throws BadLocationException {
        if (sharedSettings.getFormattingSettings().isPreserveAttrLineBreaks()
                && !isSameLine(prevOffset, attr.getStart())) {
            xmlBuilder.linefeed();
            xmlBuilder.indent(indentLevel + 1);
            xmlBuilder.addSingleAttribute(attr, false, false);
        } else if (isSingleAttribute) {
            xmlBuilder.addSingleAttribute(attr);
        } else {
            xmlBuilder.addAttribute(attr, indentLevel);
        }
    }


    /**
     * Formats the start tag's closing bracket (>) according to
     * {@code XMLFormattingOptions#isPreserveAttrLineBreaks()}
     * <p>
     * {@code XMLFormattingOptions#isPreserveAttrLineBreaks()}: If true, must add a
     * newline + indent before the closing bracket if the last attribute of the
     * element and the closing bracket are in different lines.
     *
     * @param element
     * @throws BadLocationException
     */
    public void formatElementStartTagCloseBracket(DOMElement element, XMLBuilder xmlBuilder) throws BadLocationException {
        if (sharedSettings.getFormattingSettings().isPreserveAttrLineBreaks() && element.hasAttributes()
                && !isSameLine(getLastAttribute(element).getEnd(), element.getStartTagCloseOffset())) {
            xmlBuilder.linefeed();
            xmlBuilder.indent(indentLevel);
        }
        xmlBuilder.closeStartElement();
    }

    //endregion
}
