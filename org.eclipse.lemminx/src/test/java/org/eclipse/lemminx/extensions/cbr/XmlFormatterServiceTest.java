package org.eclipse.lemminx.extensions.cbr;

import org.eclipse.lemminx.commons.BadLocationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import static org.eclipse.lemminx.XMLAssert.assertFormat;

@Disabled
class XmlFormatterServiceTest {

    private final static int MAX_TEXT_LENGTH = 60;

    @BeforeAll
    public static void setup() {
        XmlFormatterService.setMaxLineLength(MAX_TEXT_LENGTH);
    }

    @Test
    public void testFormatShort() throws BadLocationException {
        String unformatted = "<div>short div</div>";
        String expected = "" +
                "<div>\r\n" +
                "  short div\r\n" +
                "</div>";
        assertFormat(unformatted, expected);
    }

    @Test
    public void testFormatUnpartableLong() throws BadLocationException {
        String unformatted = "<div>Lorem_ipsum_dolor_sit_amet,consectetur_adipiscing_elit.Integer_vel_nibh.</div>";
        String expected = "" +
                "<div>\r\n" +
                "  \r\n" + //todo facy bug -- LineWriter inserts empty line if line too long
                "  Lorem_ipsum_dolor_sit_amet,consectetur_adipiscing_elit.Integer_vel_nibh.\r\n" +
                "</div>";
        assertFormat(unformatted, expected);
    }

    @Test
    public void testFormatPartableLong() throws BadLocationException {
        String unformatted = "<div>Lorem ipsum dolor  sit amet, consectetur adipiscing elit. Integer  vel nibh.</div>";
        String expected = "" +
                "<div>\r\n" +
                "  Lorem ipsum dolor sit amet, consectetur adipiscing elit. \r\n" +
                "  Integer vel nibh.\r\n" +
                "</div>";
        assertFormat(unformatted, expected);
    }

    @Test
    public void testFormatPartableLong1() throws BadLocationException {
        String unformatted = "" +
                "<div a=\"1234567890\" b=\"1234567890\" c=\"1234567890\" d=\"1234567890\" e=\"1234567890\" f=\"1234567890\">\r\n" +
                "  1234567890-1234567890-1234567890-1234567890- 1234567890-1234567890-1234567890-1234567890\r\n" +
                "</div>";
        String expected = "" +
                "<div a=\"1234567890\" b=\"1234567890\" c=\"1234567890\" d=\"1234567890\" e=\"1234567890\" f=\"1234567890\">\r\n" +
                "  1234567890-1234567890-1234567890-1234567890- \r\n" +
                "  1234567890-1234567890-1234567890-1234567890\r\n" +
                "</div>";
        assertFormat(unformatted, expected);
    }

    @Test
    public void testBug() throws BadLocationException {
        String unformatted = "" +
                "<div>Lorem ipsum dolor sit amet, " +
                "consectetur adipiscing elit. Vestibulum efficitur, <b>elit</b> ut mattis bibendum, " +
                "<div>augue</div> sem ornare nunc, nec aliquet libero purus porta lorem. Donec gravida mattis elit " +
                "eu placerat. Fusce id odio neque. Donec dignissim lacinia metus vel mollis. " +
                "Morbi nec erat sed justo dictum imperdiet ac vitae augue. Etiam mi sem, interdum sit amet " +
                "tincidunt sit amet, fringilla sed nisi. Cras gravida purus eu nulla vulputate suscipit. " +
                "Vestibulum hendrerit, urna eget euismod ornare, dolor est varius dolor, id eleifend metus ligula " +
                "et ex. Praesent non dui interdum, elementum mi placerat, rhoncus est. Vivamus vulputate augue sit " +
                "amet justo rutrum vehicula. Aenean vulputate feugiat facilisis. Duis quis tortor " +
                "eu lectus commodo laoreet.</div>";
        String expected = "" +
                "<div>\r\n" +
                "  Lorem ipsum dolor sit amet, consectetur adipiscing elit. \r\n" +
                "  Vestibulum efficitur, <b>elit</b> ut mattis bibendum, \r\n" +
                "  <div>\r\n" +
                "    augue\r\n" +
                "  </div>\r\n" +
                "  sem ornare nunc, nec aliquet libero purus porta lorem. \r\n" +
                "  Donec gravida mattis elit eu placerat. Fusce id odio \r\n" +
                "  neque. Donec dignissim lacinia metus vel mollis. Morbi \r\n" +
                "  nec erat sed justo dictum imperdiet ac vitae augue. Etiam\r\n" +
                "  mi sem, interdum sit amet tincidunt sit amet, fringilla \r\n" +
                "  sed nisi. Cras gravida purus eu nulla vulputate suscipit.\r\n" +
                "  Vestibulum hendrerit, urna eget euismod ornare, dolor est\r\n" +
                "  varius dolor, id eleifend metus ligula et ex. Praesent \r\n" +
                "  non dui interdum, elementum mi placerat, rhoncus est. \r\n" +
                "  Vivamus vulputate augue sit amet justo rutrum vehicula. \r\n" +
                "  Aenean vulputate feugiat facilisis. Duis quis tortor eu \r\n" +
                "  lectus commodo laoreet.\r\n" +
                "</div>";
        assertFormat(unformatted, expected);
    }

    @Test
    public void testBug2() throws BadLocationException {
        String unformatted = "" +
                "<a></a>" +
                "<b></b>" +
                "<c></c>\r\n" +
                "<div>Lorem ipsum dolor sit amet, " +
                "<b>elit</b> " +
                "<div>augue</div> " +
                "sem ornare nunc" +
                "</div>";
        String expected = "" +
                "<a></a>" +
                "<b></b>" +
                "<c></c>" +
                "<div>\r\n" +
                "  Lorem ipsum dolor sit amet, <b>elit</b> \r\n" +
                "  <div>\r\n" +
                "    augue\r\n" +
                "  </div>\r\n" +
                "  sem ornare nunc\r\n" +
                "</div>";
        assertFormat(unformatted, expected);
    }


    @Test
    public void testFormatDomNode() throws BadLocationException {
        String unformatted =
                "<div a=\"7\" b=\"1\">text <strong>with</strong> internal <i>formatting</i> <p>and paragraphs</p></div>";
        String expected = "" +
                "<div a=\"7\" b=\"1\">\r\n" +
                "  text <strong>with</strong> internal <i>formatting</i> <p>\r\n" +
                "  and paragraphs</p>\r\n" +
                "</div>";
        assertFormat(unformatted, expected);
    }

    @Test
    public void complexFormatNode() throws BadLocationException {
        String unformatted = "" +
                "<div a=\"7\" b=\"1\">text <strong>with</strong> internal <i>formatting</i> <p>and paragraphs</p>" +
                "and also <div> inner block elements with <strong>inside structure</strong>.</div>\r\n" +
                "</div>";
        String expected = "" +
                "<div a=\"7\" b=\"1\">\r\n" +
                "  text <strong>with</strong> internal <i>formatting</i> <p>\r\n" +
                "  and paragraphs</p>and also \r\n" +
                "  <div>\r\n" +
                "    inner block elements with <strong>inside structure\r\n" +
                "    </strong>.\r\n" +
                "  </div>\r\n" +
                "</div>";
        assertFormat(unformatted, expected);
    }

    @Test
    public void formatChildren1() throws BadLocationException {
        String unformatted =
                "<div><div>text1</div><div>text2</div><div>text3</div></div>";
        String expected = "" +
                "<div>\r\n" +
                "  <div>\r\n" +
                "    text1\r\n" +
                "  </div>\r\n" +
                "  <div>\r\n" +
                "    text2\r\n" +
                "  </div>\r\n" +
                "  <div>\r\n" +
                "    text3\r\n" +
                "  </div>\r\n" +
                "</div>";
        assertFormat(unformatted, expected);
    }

    @Test
    public void formatChildren2() throws BadLocationException {
        String unformatted = "" +
                "<div>\r\n" +
                "  <div>\r\n" +
                "    text1\r\n" +
                "  </div>\r\n" +
                "  <div>\r\n" +
                "    text2\r\n" +
                "  </div>\r\n" +
                "  <div>\r\n" +
                "    text3\r\n" +
                "  </div>\r\n" +
                "</div>";
        String expected = "" +
                "<div>\r\n" + //todo whitespace at end?
                "  <div>\r\n" +
                "    text1\r\n" + //todo whitespace at end?
                "  </div>\r\n" + //todo whitespace at end?
                "  <div>\r\n" +
                "    text2\r\n" + //todo whitespace at end?
                "  </div>\r\n" + //todo whitespace at end?
                "  <div>\r\n" +
                "    text3\r\n" + //todo whitespace at end?
                "  </div>\r\n" +
                "</div>";
        assertFormat(unformatted, expected);
    }

    @Test
    @Disabled
    public void testFile() throws BadLocationException {
        String unformatted = readFile("xml/cbr-formatter-test.xml");
        String expected = unformatted;
        assertFormat(unformatted, expected);
    }

    @Test
    @Disabled
    public void testFile2() throws BadLocationException {
        String unformatted = readFile("xml/cbr-formatter-test-2.dita");
        String expected = unformatted;
        assertFormat(unformatted, expected);
    }

    @Test
    @Disabled
    public void testFile3() throws BadLocationException {
        String unformatted = readFile("xml/cbr-formatter-test-3.dita");
        String expected = unformatted;
        assertFormat(unformatted, expected);
    }

    private String readFile(String name) {
        URL file = ClassLoader.getSystemClassLoader().getResource(name);
        try (InputStream inputStream = file.openStream();
             Scanner scanner = new Scanner(inputStream)
        ) {
            List<String> lines = new LinkedList<>();
            while (scanner.hasNextLine()) {
                lines.add(scanner.nextLine());
            }
            return String.join("\r\n", lines);
        } catch (Exception e) {
        }
        return "";
    }

}