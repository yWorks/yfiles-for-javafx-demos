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
package viewer.css;

import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.styles.AbstractLabelStyle;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.VisualGroup;
import javafx.geometry.Bounds;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.Objects;

/**
 * A label style that can be styled with CSS.
 * <p>
 * You can change the visualization of the label by defining properties for the style classes
 * {@code label-style.background} and {@code label-style.text}. As the background and text
 * are essentially a JavaFX
 * <a href="http://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html#rectangle">Rectangle</a>
 * and a JavaFX
 * <a href="http://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html#text">Text</a>,
 * the {@code label-style.background} and {@code label-style.text} style classes support rectangle and text
 * properties respectively.
 * </p>
 */
public class CssLabelStyle extends AbstractLabelStyle {
  private String[] styleClasses;

  /**
   * Initializes a new instance of the {@link AbstractLabelStyle} class.
   */
  public CssLabelStyle() {
  }

  /**
   * Initializes a new instance of the {@link AbstractLabelStyle} class using the given style classes.
   */
  public CssLabelStyle(String... styleClasses) {
    this.styleClasses = styleClasses;
    }

  /**
   * Returns the style classes.
   */
  public String[] getStyleClasses() {
    return styleClasses;
  }

  /**
   * Sets the style classes.
   */
  public void setStyleClasses(String... styleClasses) {
    this.styleClasses = styleClasses;
  }

  /**
   * Creates the JavaFX node used for visualizing labels.
   */
  @Override
  protected Node createVisual(IRenderContext context, ILabel label) {
    CssLabelVisual group = new CssLabelVisual();

    Rectangle background = new Rectangle();
    background.getStyleClass().add("background");
    if (styleClasses != null) {
      background.getStyleClass().addAll(styleClasses);
    }
    group.add(background);

    Text foreground = new Text();
    foreground.setTextOrigin(VPos.BOTTOM);
    foreground.getStyleClass().add("text");
    if (styleClasses != null) {
      foreground.getStyleClass().addAll(styleClasses);
    }
    group.add(foreground);

    SizeD size = label.getLayout().toSizeD();
    String text = label.getText();
    update(group, size, text);

    arrangeByLayout(context, group, label.getLayout(), true);
    return group;
  }

  /**
   * Updates the JavaFX node used for visualizing labels.
   */
  @Override
  protected Node updateVisual(IRenderContext context, Node oldVisual, ILabel label) {
    if (!(oldVisual instanceof CssLabelVisual)) {
      return createVisual(context, label);
    }
    CssLabelVisual group = (CssLabelVisual) oldVisual;

    SizeD size = label.getLayout().toSizeD();
    String text = label.getText();
    if (!Objects.equals(text, group.text) || !Objects.equals(size, group.size)) {
      update(group, size, text);
    }

    arrangeByLayout(context, group, label.getLayout(), true);
    return group;
  }

  /**
   * Updates the given visual's {@link Rectangle} and {@link Text} instances
   * to reflect the given size and text.
   */
  private void update(CssLabelVisual group, SizeD size, String text) {
    // update background shape
    Rectangle bg = (Rectangle) group.getChildren().get(0);
    bg.setWidth(size.getWidth());
    bg.setHeight(size.getHeight());

    // update text position
    Text fg = (Text) group.getChildren().get(1);
    fg.setText(text);
    fg.setX((size.getWidth() - fg.getLayoutBounds().getWidth()) / 2);
    fg.setY((size.getHeight() + fg.getLayoutBounds().getHeight()) / 2);

    // update group
    group.size = size;
    group.text = text;
  }

  /**
   * Calculates the preferred size for the given label if this style is used for rendering.
   * The size is calculated from the label's text.
   */
  @Override
  protected SizeD getPreferredSize(ILabel label) {
    String labelText = label.getText();
    Text text = new Text(labelText);
    Bounds bounds = text.getLayoutBounds();
    return new SizeD(bounds.getWidth(), bounds.getHeight());
  }

  /**
   * Stores the size and text of the visualization to speed-up visualization updates in
   * {@link CssLabelStyle#updateVisual(IRenderContext, Node, ILabel)} if the label size and text have not changed.
   */
  private static class CssLabelVisual extends VisualGroup {
    SizeD size;
    String text;
  }
}
