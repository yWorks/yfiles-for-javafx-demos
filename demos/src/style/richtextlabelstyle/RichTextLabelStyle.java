/****************************************************************************
 **
 ** This demo file is part of yFiles for JavaFX 3.4.
 **
 ** Copyright (c) 2000-2021 by yWorks GmbH, Vor dem Kreuzberg 28,
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

import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.styles.AbstractLabelStyle;
import com.yworks.yfiles.view.IRenderContext;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.text.TextFlow;

/**
 * A style for labels that converts Markdown formatting into a JavaFX rich text TextFlow element.
 * For demonstration purposes, we implement only the following rules of Markdown:
 * <ul>
 *   <li>An empty line creates a paragraph.</li>
 *   <li>Two or more spaces at the end of a line create a line break.</li>
 *   <li>Text that is enclosed with one asterisk or underscore is displayed italic./li>
 *   <li>Text that is enclosed with two asterisks or underscores is displayed bold./li>
 *   <li>Text that is enclosed with three asterisks or underscores is displayed bold and italic./li>
 * </ul>
 */
public class RichTextLabelStyle extends AbstractLabelStyle {

  @Override
  protected Node createVisual(IRenderContext renderContext, ILabel label) {
    String labelText = label.getText();
    TextFlow textFlow = MarkdownConverter.convertMarkDownToTextFlow(labelText);

    // move visual to correct location
    arrangeByLayout(renderContext, textFlow, label.getLayout(), true);

    // Because the text that the visual now holds maybe different from the original labels text
    // and we want to improve the performance to only recalculate the Markdown when the text actually
    // has changed, we need to transport this state to the next update call.
    // We choose to use the node's user data for this. There are several other ways to do this, but this
    // one is the easiest solution here.
    textFlow.setUserData(labelText);
    return textFlow;
  }

  @Override
  protected Node updateVisual(IRenderContext renderContext, Node oldVisual, ILabel label) {
    if (oldVisual instanceof TextFlow) {
      TextFlow oldTextFlow = (TextFlow) oldVisual;
      if (oldTextFlow.getChildren().size() > 0) {
        // we retrieve the old label text from the node's user data object
        String oldLabelText = oldTextFlow.getUserData() instanceof String ? (String) oldTextFlow.getUserData() : null;
        // If something has changed, most notably the text of the label, we recalculate the TextFlow.
        // Otherwise, we just adjust the layout and return.
        String labelText = label.getText();
        if (oldLabelText != null && !oldLabelText.equals(labelText)) {
          // We do not bother with determining the exact changes and preserving the existing data.
          // We simply discard it and recalculate.
          TextFlow newTextFlow = MarkdownConverter.convertMarkDownToTextFlow(labelText);
          arrangeByLayout(renderContext, newTextFlow, label.getLayout(), true);
          // again, remember the label text with which the TextFlow was created
          newTextFlow.setUserData(labelText);
          return newTextFlow;
        } else {
          // if nothing has changed, we simply adjust the position of the old TextFlow and return it
          arrangeByLayout(renderContext, oldTextFlow, label.getLayout(), true);
          return oldTextFlow;
        }
      }
    }
    return createVisual(renderContext, label);
  }

  /**
   * Calculates the preferred size given the current state of the renderer.
   * @return The size as suggested by this renderer.
   */
  @Override
  protected SizeD getPreferredSize(ILabel label) {
    // this method is not called that often so we can dare to use the sophisticated answer,
    // which is constructing the correct TextFlow and retrieving its bounds.
    TextFlow temp = MarkdownConverter.convertMarkDownToTextFlow(label.getText());
    Bounds bounds = temp.getBoundsInLocal();
    return new SizeD(bounds.getWidth(), bounds.getHeight());
  }

}
