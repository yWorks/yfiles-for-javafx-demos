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
package tutorial02_CustomStyles.step27_CustomGroupBounds;

import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.styles.CollapsibleNodeStyleDecorator;
import com.yworks.yfiles.graph.styles.CollapsibleNodeStyleDecoratorRenderer;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.input.INodeInsetsProvider;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;

/**
 * Customizes the collapse button visualization of the {@link com.yworks.yfiles.graph.styles.CollapsibleNodeStyleDecorator}.
 */
public class MyCollapsibleNodeStyleDecorator extends CollapsibleNodeStyleDecorator {
  private SizeD buttonSize;

  /**
   * Initializes a new <code>MyCollapsibleNodeStyleDecorator</code> instance using a size of 15x15 for the
   * collapse button.
   */
  public MyCollapsibleNodeStyleDecorator(INodeStyle decoratedStyle) {
    this(decoratedStyle, new SizeD(15, 15));
  }

  @Override
  public SizeD getButtonSize() {
    return buttonSize;
  }

  /**
   * Initializes a new <code>MyCollapsibleNodeStyleDecorator</code> instance using the given size for the
   * collapse button.
   */
  public MyCollapsibleNodeStyleDecorator(INodeStyle decoratedStyle, SizeD buttonSize) {
    super(decoratedStyle, new MyCollapsibleNodeStyleDecoratorRenderer());
    this.buttonSize = buttonSize;
  }



  /**
   * Customizes the {@link #lookup(Class)} method to provide the {@link com.yworks.yfiles.view.input.INodeInsetsProvider}
   * of the wrapped style.
   */
  private static class MyCollapsibleNodeStyleDecoratorRenderer extends CollapsibleNodeStyleDecoratorRenderer {
    /**
     * Overridden to avoid the base insets provider, which adds insets for the label.
     */
    @Override
    public <TLookup> TLookup lookup(Class<TLookup> type) {
      if (type == INodeInsetsProvider.class) {
        // return the implementation of the wrapped style directly
        INodeStyle wrappedStyle = getWrappedStyle();
        return wrappedStyle.getRenderer().getContext(getNode(), wrappedStyle).lookup(type);
      }
      return super.lookup(type);
    }

    @Override
    protected void updateButton(IRenderContext context, Node button, final boolean expanded) {
      // create a 10x10 icon
      Group icon = new Group();
      icon.setMouseTransparent(true);

      Ellipse background = new Ellipse(5, 5, 5, 5);
      background.setFill(Color.GRAY);
      icon.getChildren().add(background);

      Rectangle horizontal = new Rectangle(2, 4, 6, 2);
      horizontal.setFill(Color.WHITE);
      icon.getChildren().add(horizontal);

      if (!expanded) {
        Rectangle vertical = new Rectangle(4, 2, 2, 6);
        vertical.setFill(Color.WHITE);
        icon.getChildren().add(vertical);
      }

      // scale the icon to the size of the button
      icon.setScaleX(0.1 * getButtonSize().getWidth());
      icon.setScaleY(0.1 * getButtonSize().getHeight());

      button.setStyle("-fx-background-color: transparent; -fx-border-width: 0; -fx-padding: 0;");
      ((Button)button).setGraphic(icon);
    }
  }
}
