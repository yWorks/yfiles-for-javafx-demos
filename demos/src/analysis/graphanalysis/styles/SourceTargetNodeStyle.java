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
package analysis.graphanalysis.styles;

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.GeomUtilities;
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.VisualGroup;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Ellipse;

/**
 * Visualizes source and/or target nodes of paths as segmented rings in one or
 * two colors.
 */
public class SourceTargetNodeStyle extends AbstractNodeStyle {
  private Type type;

  /**
   * Initializes a new {@code SourceTargetNodeStyle} instance for
   * {@link Type#TYPE_SOURCE source} nodes.
   */
  public SourceTargetNodeStyle() {
    this(Type.TYPE_SOURCE);
  }

  /**
   * Initializes a new {@code SourceTargetNodeStyle} instance with the given
   * type.
   * @param type the node type to be visualized.
   */
  public SourceTargetNodeStyle(Type type) {
    this.type = type;
  }

  /**
   * Returns the node type visualized by this style instance.
   * @return the node type visualized by this style instance.
   */
  public Type getType() {
    return type;
  }

  /**
   * Sets the node type visualized by this style instance.
   * @param type the node type to be visualized.
   */
  public void setType(Type type) {
    this.type = type;
  }

  @Override
  protected Node createVisual(IRenderContext context, INode node) {
    DashedCircleVisual visual = new DashedCircleVisual();
    visual.update(node, getType());
    return visual;
  }

  @Override
  protected Node updateVisual(IRenderContext context, Node oldVisual, INode node) {
    DashedCircleVisual visual = oldVisual instanceof DashedCircleVisual
            ? (DashedCircleVisual) oldVisual : new DashedCircleVisual();
    visual.update(node, getType());
    return visual;
  }

  /**
   * Determines whether or not the given point lies inside the given node's
   * visualization.
   * <p>
   * Necessary for calculating intersection points of nodes and incident edges.
   * </p>
   */
  @Override
  protected boolean isInside(INode node, PointD location) {
    return GeomUtilities.ellipseContains(node.getLayout().toRectD(), location, 0);
  }

  /**
   * Returns the outline of the given node's visualization.
   * <p>
   * Necessary for calculating intersection points of nodes and incident edges.
   * </p>
   */
  @Override
  protected GeneralPath getOutline(INode node) {
    RectD bounds = node.getLayout().toRectD();
    GeneralPath outline = new GeneralPath();
    outline.appendEllipse(bounds, false);
    return outline;
  }



  private static class DashedCircleVisual extends VisualGroup {
    static final Pen SOURCE_PEN = new Pen(Color.YELLOWGREEN, 5);
    static final Pen TARGET_PEN = new Pen(Color.INDIANRED, 5);

    DashedCircleVisual() {
      for (int i = 0; i < 4; ++i) {
        Arc shape = new Arc();
        shape.setStartAngle(i * 90);
        shape.setLength(90);
        shape.setType(ArcType.OPEN);
        shape.setFill(Color.WHITE);
        add(shape);
      }
    }

    void update( INode node, Type type) {
      IRectangle nl = node.getLayout();
      double width = nl.getWidth();
      double height = nl.getHeight();
      double size = Math.max(width, height);
      double x = nl.getX() + (width - size) * 0.5;
      double y = nl.getY() + (height - size) * 0.5;

      Pen pen0 = TARGET_PEN;
      Pen pen1 = SOURCE_PEN;
      switch (type) {
        case TYPE_TARGET:
          pen1 = TARGET_PEN;
          break;
        case TYPE_SOURCE:
          pen0 = SOURCE_PEN;
          break;
      }

      for (int i = 0, n = size(); i < n; ++i) {
        Arc shape = (Arc) get(i);
        ((i % 2) == 0 ? pen0 : pen1).styleShape(shape);
        setFrame(shape, x, y, size, size);
      }
    }


    /*
     * ###################################################################
     * convenience methods
     * ###################################################################
     */

    Node get( int index ) {
      return getNullableChildren().get(index);
    }

    int size() {
      return getNullableChildren().size();
    }


    /*
     * ###################################################################
     * utility methods
     * ###################################################################
     */

    private static void setFrame(
            Arc shape, double x, double y, double width, double height
    ) {
      shape.setCenterX(width * 0.5);
      shape.setRadiusX(width * 0.5);
      shape.setCenterY(height * 0.5);
      shape.setRadiusY(height * 0.5);
      shape.setLayoutX(x);
      shape.setLayoutY(y);
    }

    private static void setFrame(
            Ellipse shape, double x, double y, double width, double height
    ) {
      shape.setCenterX(width * 0.5);
      shape.setRadiusX(width * 0.5);
      shape.setCenterY(height * 0.5);
      shape.setRadiusY(height * 0.5);
      shape.setLayoutX(x);
      shape.setLayoutY(y);
    }
  }


  public enum Type {
    TYPE_SOURCE,
    TYPE_TARGET,
    TYPE_SOURCE_AND_TARGET,
  }
}
