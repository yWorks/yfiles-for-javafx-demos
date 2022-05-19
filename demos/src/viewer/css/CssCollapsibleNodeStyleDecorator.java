/****************************************************************************
 **
 ** This demo file is part of yFiles for JavaFX 3.5.
 **
 ** Copyright (c) 2000-2022 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package viewer.css;

import com.yworks.yfiles.graph.styles.CollapsibleNodeStyleDecorator;
import com.yworks.yfiles.graph.styles.CollapsibleNodeStyleDecoratorRenderer;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.view.IRenderContext;
import javafx.scene.Node;
import javafx.scene.control.Labeled;

/**
 * A {@link CollapsibleNodeStyleDecorator} that allows CSS styling of the collapse button.
 */
public class CssCollapsibleNodeStyleDecorator extends CollapsibleNodeStyleDecorator {
  /**
   * Initializes a new {@code CssCollapsibleNodeStyleDecorator} instance.
   */
  public CssCollapsibleNodeStyleDecorator() {
    super(new ShapeNodeStyle(), new MyCollapsibleNodeStyleDecoratorRenderer());
  }

  /**
   * Initializes a new {@code CssCollapsibleNodeStyleDecorator} instance.
   */
  public CssCollapsibleNodeStyleDecorator(INodeStyle wrappedStyle) {
    super(wrappedStyle, new MyCollapsibleNodeStyleDecoratorRenderer());
  }

  private static class MyCollapsibleNodeStyleDecoratorRenderer extends CollapsibleNodeStyleDecoratorRenderer {
    /**
     * Updates the toggle button depending on the folding state of the group node.
     * Overwritten to remove the default yFiles stylesheet from the collapse button
     * to allow the demo's custom CSS to be used.
     */
    @Override
    protected void updateButton(IRenderContext context, Node button, boolean expanded) {
      super.updateButton(context, button, expanded);

      // remove the predefined stylesheets of the button, so that our own CSS is applied
      if (button instanceof Labeled) {
        Labeled labeled = (Labeled) button;
        labeled.getStylesheets().clear();
      }
    }
  }
}
