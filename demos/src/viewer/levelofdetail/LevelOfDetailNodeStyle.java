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
package viewer.levelofdetail;

import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.VisualGroup;
import javafx.geometry.Bounds;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * Visualizes different pieces of information about employees depending on
 * the graph control's current zoom factor.
 */
public class LevelOfDetailNodeStyle extends AbstractNodeStyle {
  /**
   * The minimum zoom factor to display all employee information.
   */
  private static final double DETAIL_THRESHOLD = 0.8;
  /**
   * The minimum zoom factor to display some but not all employee information.
   */
  private static final double INTERMEDIATE_THRESHOLD = 0.5;

  /**
   * Creates the JavaFX node for the given graph node and its associated
   * employee instance.
   */
  @Override
  protected Node createVisual( IRenderContext context, INode node ) {
    double zoom = context.getZoom();
    if (zoom > DETAIL_THRESHOLD) {
      DetailVisual visual = new DetailVisual();
      visual.update(node);
      return visual;
    } else if (zoom > INTERMEDIATE_THRESHOLD) {
      IntermediateVisual visual = new IntermediateVisual();
      visual.update(node);
      return visual;
    } else {
      OverviewVisual visual = new OverviewVisual();
      visual.update(node);
      return visual;
    }
  }

  /**
   * Updates the JavaFX node for the given graph node and its associated
   * employee instance.
   */
  @Override
  protected Node updateVisual( IRenderContext context, Node oldVisual, INode node ) {
    double zoom = context.getZoom();
    if (zoom > DETAIL_THRESHOLD) {
      if (oldVisual instanceof DetailVisual) {
        ((DetailVisual) oldVisual).update(node);
        return oldVisual;
      }
    } else if (zoom > INTERMEDIATE_THRESHOLD) {
      if (oldVisual instanceof IntermediateVisual) {
        ((IntermediateVisual) oldVisual).update(node);
        return oldVisual;
      }
    } else {
      if (oldVisual instanceof OverviewVisual) {
        ((OverviewVisual) oldVisual).update(node);
        return oldVisual;
      }
    }

    return createVisual(context, node);
  }



  /**
   * Base class for displaying employee information.
   */
  private abstract static class AbstractVisual extends VisualGroup {
    private final int nameFontSize;
    private final int dataFontSize;

    AbstractVisual( int nameFontSize, int dataFontSize ) {
      this.nameFontSize = nameFontSize;
      this.dataFontSize = dataFontSize;

      Rectangle bounds = new Rectangle();
      bounds.setStroke(Color.BLACK);
      bounds.setStrokeWidth(3);
      bounds.setFill(Color.TRANSPARENT);
      add(bounds);
    }

    /**
     * Updates geometry and displayed information for the given node and its
     * associated employee.
     */
    void update( INode node ) {
      IRectangle nl = node.getLayout();
      setLayoutX(nl.getX());
      setLayoutY(nl.getY());

      Rectangle bounds = (Rectangle) get(0);
      bounds.setWidth(nl.getWidth());
      bounds.setHeight(nl.getHeight());

      updateEmployeeData((Employee) node.getTag());
    }

    /**
     * Updates the displayed information for the given employee.
     */
    abstract void updateEmployeeData( Employee employee );

    /**
     * Adds a {@link Text} node for displaying some piece of employee data.
     * @return the distance to the next text line
     */
    int addTextNode( int x, int y, boolean headline ) {
      Color textColor = headline ? Color.BLUE : Color.BLACK;
      int fontSize = headline ? nameFontSize : dataFontSize;
      return addTextNode(x, y, textColor, fontSize);
    }

    /**
     * Adds a {@link Text} node for displaying some piece of employee data.
     * @return the distance to the next text line
     */
    int addTextNode( int x, int y, Color textColor, int fontSize ) {
      int textOffset = 3;
      Text node = new Text(x, y, "Xy");
      node.setFill(textColor);
      node.setTextAlignment(TextAlignment.CENTER);
      node.setFont(newFont(fontSize));
      node.setTextOrigin(VPos.TOP);
      node.setWrappingWidth(0);
      Bounds textBnds = node.getLayoutBounds();
      add(node);
      return textOffset + (int)Math.ceil(textBnds.getHeight());
    }

    /**
     * Returns the JavaFX child node at the given index.
     */
    Node get( int index ) {
      return getNullableChildren().get(index);
    }

    /**
     * Creates a new {@link Font} instance with the specified font size.
     */
    private static Font newFont( int size ) {
      return Font.font("System", FontWeight.NORMAL, FontPosture.REGULAR, size);
    }
  }

  /**
   * Displays the most pieces of information for high-detail rendering
   */
  private static final class DetailVisual extends AbstractVisual {
    DetailVisual() {
      super(16, 10);

      int y = 2;
      y += addTextNode(10, y, true);
      y += addTextNode(10, y, false);
      y += 8;
      y += addTextNode(10, y, false);
      addTextNode(10, y, false);
      addTextNode(90, y, false);
    }

    /**
     * Updates the employee's name, position, mail address, phone and fax
     * numbers.
     */
    @Override
    void updateEmployeeData( Employee employee ) {
      ((Text) get(1)).setText(employee.getName());
      ((Text) get(2)).setText(employee.getPosition());
      ((Text) get(3)).setText(employee.getMail());
      ((Text) get(4)).setText(employee.getPhone());
      ((Text) get(5)).setText(employee.getFax());
    }
  }

  /**
   * Displays some but not all information for intermediate detail rendering.
   */
  private static final class IntermediateVisual extends AbstractVisual {
    IntermediateVisual() {
      super(26, 15);

      int y = 2;
      y += addTextNode(10, y, true);
      y += addTextNode(10, y, false);
    }

    /**
     * Updates the employee's name and position.
     */
    @Override
    void updateEmployeeData( Employee employee ) {
      ((Text) get(1)).setText(employee.getName());
      ((Text) get(2)).setText(employee.getPosition());
    }
  }

  /**
   * Displays only the employee name for low-detail rendering.
   */
  private static final class OverviewVisual extends AbstractVisual {
    OverviewVisual() {
      super(35, 35);

      addTextNode(10, 20, true);
    }

    /**
     * Updates the employee's name.
     */
    @Override
    void updateEmployeeData( Employee employee ) {
      ((Text) get(1)).setText(employee.getName());
    }
  }
}
