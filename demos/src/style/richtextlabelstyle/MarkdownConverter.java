/****************************************************************************
 **
 ** This demo file is part of yFiles for JavaFX 3.6.
 **
 ** Copyright (c) 2000-2023 by yWorks GmbH, Vor dem Kreuzberg 28,
 ** 72070 Tuebingen, Germany. All rights reserved.
 **
 ** yFiles demo files exhibit yFiles for JavaFX functionalities. Any redistribution
 ** of demo files in source code or binary form, with or without
 ** modification, is not permitted.
 **
 ** Owners of a valid software license for a yFiles for JavaFX version that this
 ** demo is shipped with are allowed to use the demo source code as basis
 ** for their own yFiles for JavaFX powered applications. Use of such programs is
 ** governed by the rights and conditions as set out in the yFiles for JavaFX
 ** license agreement.
 **
 ** THIS SOFTWARE IS PROVIDED ''AS IS'' AND ANY EXPRESS OR IMPLIED
 ** WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 ** MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
 ** NO EVENT SHALL yWorks BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 ** SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 ** TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 ** PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 ** LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 ** NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 ** SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **
 ***************************************************************************/
package style.richtextlabelstyle;

import javafx.scene.Node;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;

/**
 * Converts a simplified version of Markdown formatted text into JavaFX TextFlows.
 */
public class MarkdownConverter {

  private static final char ASTERISK = '*';
  private static final char UNDERSCORE = '_';

  private static final Font DEFAULT_FONT;
  private static final boolean VALID_DEFAULT_FONT;

  static {
    Font defaultFont = Font.font(isMacOs() ? "Helvetica" : "System", FontWeight.NORMAL, FontPosture.REGULAR, 12);
    // check if the defaultFont supports italic and bold formatting
    Font italicBoldTestFont = Font.font(defaultFont.getFamily(), FontWeight.BOLD, FontPosture.ITALIC, 12);
    DEFAULT_FONT =  defaultFont;
    VALID_DEFAULT_FONT = italicBoldTestFont.getStyle().contains("Bold") && italicBoldTestFont.getStyle().contains("Italic");
  }

  private static boolean isMacOs() {
    try {
      return System.getProperty("os.name").toLowerCase().startsWith("mac os");
    } catch (Throwable t) {
      return false;
    }
  }

  /**
   * Returns if the default font supports italic and bold formatting.
   */
  public static boolean isDefaultFontValid() {
    return VALID_DEFAULT_FONT;
  }

  /**
   * Converts a generic string to a JavaFX TextFlow by applying a simplified version
   * of the Markdown formatting to it.
   */
  public static TextFlow convertMarkDownToTextFlow(String s) {
    TextFlow flow = new TextFlow();

    Text text = new Text(s);
    text.setFont(DEFAULT_FONT);

    flow.getChildren().add(text);

    // replace the formatting marks
    createMarkDownFormattings(flow);

    // now enforce the newline rules of Markdown
    createMarkDownLineBreaks(flow);

    return flow;
  }

  /**
   * Iterates over the nodes of the flow and splits them into separate text nodes with
   * different styling dependent on the Markdown formatting.
   */
  private static void createMarkDownFormattings(TextFlow flow) {
    // we use a continuous loop because we need to restart the splitting
    // simply because this is easier than to keep track of the index changes
    // that occur when splitting up a text node into multiple ones.
    while (true) {
      // iterate over a copy of the actual list to have the ability to modify it during the process
      ArrayList<Node> nodes = new ArrayList<>(flow.getChildren());

      boolean splitted = false;
      for (Node node : nodes) {
        // try to split the text node according to the formatting rules.
        splitted = splitText(flow, (Text) node);
        if (splitted ) {
          // if a splitting occurred, start over.
          break;
        }
      }
      if (!splitted) {
        // if we iterated over all nodes and had nothing to do, we are done.
        break;
      }
    }
  }

  /**
   * Markdown defines rules for line breaks and paragraphs.
   * For simplicity, the following rule is satisfactory for this:
   * Remove all line breaks that are not double line breaks ("\n\n") or line breaks with two spaces in front of them ("  \n")
   */
  private static void createMarkDownLineBreaks(TextFlow flow) {
    // we split up the original text and introduce line breaks where it is appropriate.
    for (Node node : flow.getChildren()) {
      Text textNode = (Text) node;

      String[] lines = textNode.getText().split("\n");
      String normalizedText = "";

      for (String line : lines) {
        if (line.isEmpty()) {
          // empty lines create a paragraph
          normalizedText += "\n\n";
        } else if (line.endsWith("  ")) {
          // lines with double spaces at the end create a new line.
          normalizedText += line + "\n";
        } else {
          // all other line breaks are simply ignored.
          normalizedText += normalizedText.endsWith(" ") ? " " + line : line;
        }
      }
      textNode.setText(normalizedText);
    }
  }

  /**
   * Tries to split up a text node by applying a FormatRule to its content.
   * Splitting up means removing the original text node from the TextFlow's
   * children list and replacing it by multiple distinct ones.
   * This method my modify the children list of the given TextFlow.
   */
  private static boolean splitText(TextFlow flow, Text textNode) {

    String text = textNode.getText();

    // we iterate over the char array because this is easier than to use indexOf with substring arithmetic
    char[] chars = text.toCharArray();

    for (int i = 0; i < chars.length; i++) {
      // look if a rule can be applied
      FormatRule ruleToApply = checkForRuleApplication(chars, i, ASTERISK);
      if (ruleToApply == null) {
        ruleToApply = checkForRuleApplication(chars, i, UNDERSCORE);
      }
      if (ruleToApply != null) {
        ruleToApply.apply(flow, textNode);
        return true;
      }
    }
    return false;
  }

  /**
   * Checks if a formatting rule can be applied and chooses the appropriate one.
   * @param chars  the character array of the text.
   * @param i the index where we are currently at.
   * @param tag the formatting mark to look for (either an asterisk or an underscore)
   * @return a rule that can successfully be applied to the text.
   */
  private static FormatRule checkForRuleApplication(char[] chars, int i, char tag) {
    FormatRule rule = null;
    if (chars[i] == tag) {
      // we have a match, find out which one
      int formattedTextStartIndex;
      if (i < chars.length-1 && chars[i+1] == tag) {
        if (i < chars.length-2 && chars[i+2] == tag) {
          formattedTextStartIndex = i+3;
          rule = new ItalicBoldRule();
        } else {
          formattedTextStartIndex = i+2;
          rule = new BoldRule();
        }
      } else {
        formattedTextStartIndex = i+1;
        rule = new ItalicRule();
      }
      rule.setStartIndex(i);

      // look for the matching end tag
      for (int j = formattedTextStartIndex; j < chars.length; j++) {
        if (chars[j] == tag) {
          if (j < chars.length-1 && chars[j+1] == tag) {
            if (j < chars.length-2 && chars[j+2] == tag) {
              if (rule.getTag1().length() == 3) {
                rule.setEndIndex(j);
                break;
              }
            } else if (rule.getTag1().length() == 2) {
              rule.setEndIndex(j);
              break;
            }
          } else if (rule.getTag1().length() == 1) {
            rule.setEndIndex(j);
            break;
          }
        }
      }
    }
    if (rule != null && rule.getEndIndex() > 0){
      return rule;
    } else {
      // no end tag index means that this is a non-closing tag and shouldn't be processed.
      return null;
    }
  }

  /**
   * Abstract class for formatting rules that change the weight or posture of a font (regular, italic and bold).
   * In Markdown, those formattings can be expressed with either a number of asterisks ("*") or underscores ("_").
   * A rule can be applied to a string and splits the JavaFX Text node that holds the string into
   * three different Text nodes: The formatted region of the string itself as well as the pre- and suffix.
   * The pre- and suffix are styled as the original text node and the region to be formatted will be styled
   * by the #stylePerRule method.
   * Each rule holds the index to which it is applied for in a given string.
   */
  private static abstract class FormatRule {

    protected String tag1;
    protected String tag2;

    /**
     * Each rule can be either invoked by enclosing a text region with asterisks or underscores. We store both possibilities in simple
     * properties that are accessible through getters.
     */
    public FormatRule(String tag1, String tag2) {
      this.tag1 = tag1;
      this.tag2 = tag2;
    }

    protected String getTag1() {
      return tag1;
    }

    protected String getTag2() {
      return tag2;
    }

    /**
     * The index of the first occurrence of the beginning tag in the text.
     */
    private int startIndex;

    /**
     * The index of the first occurrence of the ending tag in the text.
     */
    private int endIndex;

    public int getStartIndex() {
      return startIndex;
    }

    public void setStartIndex(int startIndex) {
      this.startIndex = startIndex;
    }

    public int getEndIndex() {
      return endIndex;
    }

    public void setEndIndex(int endIndex) {
      this.endIndex = endIndex;
    }

    /**
     * This method actually styles the newly created text node for the spliced text.
     */
    protected abstract void stylePerRule(Text oldTextNode, Text newTextNode);

    /**
     * Applies this formatting rule to the given text.
     * @param flow The flow that contains the given text node and in which the new text will be inserted to at the position of the given text node.
     * @param textNode The text node that holds the text, font and styling of the region to apply the formatting to.
     */
    public void apply(TextFlow flow, Text textNode) {
      // the text that we will split up
      String text = textNode.getText();
      // the region of the text prior to the region that will be formatted
      String prefix = text.substring(0, getStartIndex());
      // the text that will have the new formatting as per this rule
      String formattedText = text.substring(getStartIndex() + getTag1().length(), getEndIndex());
      // the region of the text after the region that will be formatted.
      String suffix = text.substring(getEndIndex()+getTag1().length());

      // we will insert up to three new text nodes in place of the old one in the TextFlow.

      // remember the index of the old text node
      int textNodeIndex = flow.getChildren().indexOf(textNode);
      // remove it, it will be discarded
      flow.getChildren().remove(textNode);

      // for all non empty regions we insert a new text node starting with the last
      if (!suffix.equals("")){
        flow.getChildren().add(textNodeIndex, createTextFromText(textNode, suffix));
      }
      if (!formattedText.equals("")){
        Text newTextNode = createTextFromText(textNode, formattedText);
        stylePerRule(textNode, newTextNode);
        flow.getChildren().add(textNodeIndex, newTextNode);
      }
      if (!prefix.equals("")){
        flow.getChildren().add(textNodeIndex, createTextFromText(textNode, prefix));
      }
    }

    /**
     * Returns a new text node that has the same styling as the given text node plus the given string.
     */
    private Text createTextFromText(Text textNode, String text) {
      Text newTextNode = new Text(text);
      newTextNode.setStrikethrough(textNode.isStrikethrough());
      newTextNode.setUnderline(textNode.isUnderline());
      newTextNode.setFont(textNode.getFont());
      return newTextNode;
    }
  }

  /**
   * Formats the new area to be italic.
   */
  private static class ItalicRule extends FormatRule {

    /**
     * A text is formatted in italics by enclosing it with a pair of single asterisks or underscores.
     */
    public ItalicRule() {
      super("*", "_");
    }

    @Override
    protected void stylePerRule(Text oldTextNode, Text newTextNode) {
      Font oldFont = newTextNode.getFont();
      FontWeight weight = oldFont.getStyle().contains("Bold") ? FontWeight.BOLD : FontWeight.NORMAL;
      newTextNode.setFont(Font.font(oldFont.getFamily(), weight, FontPosture.ITALIC, oldFont.getSize()));
    }
  }

  /**
   * Formats the new area to be bold.
   */
  private static class BoldRule extends FormatRule {

    /**
     * A text is formatted in bold by enclosing it with a pair of double asterisks or underscores.
     */
    public BoldRule() {
      super("**", "__");
    }

    @Override
    protected void stylePerRule(Text oldTextNode, Text newTextNode) {
      Font oldFont = newTextNode.getFont();
      FontPosture fontPosture = oldFont.getStyle().contains("Italic") ? FontPosture.ITALIC : FontPosture.REGULAR;
      newTextNode.setFont(Font.font(oldFont.getFamily(), FontWeight.BOLD, fontPosture, oldFont.getSize()));
    }
  }

  /**
   * Formats the new area to be both italic and bold.
   */
  private static class ItalicBoldRule extends FormatRule {

    /**
     * A text is formatted in italic and bold by enclosing it with a pair of triple asterisks or underscores.
     */
    public ItalicBoldRule() {
      super("***", "___");
    }

    @Override
    protected void stylePerRule(Text oldTextNode, Text newTextNode) {
      Font oldFont = newTextNode.getFont();
      newTextNode.setFont(Font.font(oldFont.getFamily(), FontWeight.BOLD, FontPosture.ITALIC, oldFont.getSize()));
    }
  }
}
