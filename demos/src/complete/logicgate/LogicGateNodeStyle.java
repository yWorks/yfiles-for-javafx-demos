/****************************************************************************
 **
 ** This demo file is part of yFiles for JavaFX 3.3.
 **
 ** Copyright (c) 2000-2019 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package complete.logicgate;

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.Matrix2D;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.VisualGroup;
import com.yworks.yfiles.view.input.IInputModeContext;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;

/**
 * An implementation of an {@link INodeStyle} that displays logic gates.
 */
public class LogicGateNodeStyle extends AbstractNodeStyle {
  // node fill
  private static final Color FILL_COLOR = Color.WHITESMOKE;
  private static final Color OUTLINE_COLOR = Color.BLACK;

  private static final GeneralPath AND_OUTLINE_PATH, OR_OUTLINE_PATH, NAND_OUTLINE_PATH, NOR_OUTLINE_PATH, NOT_OUTLINE_PATH;

  private LogicGateType type;

  static {
    // path for AND nodes
    AND_OUTLINE_PATH = new GeneralPath();
    AND_OUTLINE_PATH.moveTo(0.6, 0);
    AND_OUTLINE_PATH.lineTo(0.1, 0);
    AND_OUTLINE_PATH.lineTo(0.1, 1);
    AND_OUTLINE_PATH.lineTo(0.6, 1);
    AND_OUTLINE_PATH.quadTo(0.8, 1.0, 0.8, 0.5);
    AND_OUTLINE_PATH.quadTo(0.8, 0.0, 0.6, 0);

    // path for OR nodes
    OR_OUTLINE_PATH = new GeneralPath();
    OR_OUTLINE_PATH.moveTo(0.6, 0);
    OR_OUTLINE_PATH.lineTo(0.1, 0);
    OR_OUTLINE_PATH.quadTo(0.3, 0.5, 0.1, 1);
    OR_OUTLINE_PATH.lineTo(0.6, 1);
    OR_OUTLINE_PATH.quadTo(0.8, 1.0, 0.8, 0.5);
    OR_OUTLINE_PATH.quadTo(0.8, 0.0, 0.6, 0);

    // path for NAND nodes
    NAND_OUTLINE_PATH = new GeneralPath();
    NAND_OUTLINE_PATH.moveTo(0.6, 0);
    NAND_OUTLINE_PATH.lineTo(0.1, 0);
    NAND_OUTLINE_PATH.lineTo(0.1, 1);
    NAND_OUTLINE_PATH.lineTo(0.6, 1);
    NAND_OUTLINE_PATH.quadTo(0.8, 1.0, 0.8, 0.5);
    NAND_OUTLINE_PATH.quadTo(0.8, 0.0, 0.6, 0);
    NAND_OUTLINE_PATH.appendEllipse(new RectD(0.8, 0.4, 0.1, 0.2), false);

    // path for NOR nodes
    NOR_OUTLINE_PATH = new GeneralPath();
    NOR_OUTLINE_PATH.moveTo(0.6, 0);
    NOR_OUTLINE_PATH.lineTo(0.1, 0);
    NOR_OUTLINE_PATH.quadTo(0.3, 0.5, 0.1, 1);
    NOR_OUTLINE_PATH.lineTo(0.6, 1);
    NOR_OUTLINE_PATH.quadTo(0.8, 1.0, 0.8, 0.5);
    NOR_OUTLINE_PATH.quadTo(0.8, 0.0, 0.6, 0);
    NOR_OUTLINE_PATH.appendEllipse(new RectD(0.8, 0.4, 0.1, 0.2), false);

    // path for NOT nodes
    NOT_OUTLINE_PATH = new GeneralPath();
    NOT_OUTLINE_PATH.moveTo(0.8, 0.5);
    NOT_OUTLINE_PATH.lineTo(0.1, 0);
    NOT_OUTLINE_PATH.lineTo(0.1, 1);
    NOT_OUTLINE_PATH.lineTo(0.8, 0.5);
    NOT_OUTLINE_PATH.appendEllipse(new RectD(0.8, 0.4, 0.1, 0.2), false);
  }

  /**
   * Default constructor for serialization.
   */
  public LogicGateNodeStyle() {
  }

  public LogicGateNodeStyle(LogicGateType type) {
    this.type = type;
  }

  /**
   * Gets the type of the logic gate.
   */
  public LogicGateType getGateType() {
    return type;
  }

  /**
   * Sets the type of the logic gate.
   */
  public void setGateType(LogicGateType type) {
    this.type = type;
  }

  @Override
  protected Node createVisual(IRenderContext context, INode node) {
    LogicGateVisual visual = new LogicGateVisual(getGateType());
    visual.update(node);
    return visual;
  }

  @Override
  protected Node updateVisual(IRenderContext context, Node oldVisual, INode node) {
    if (oldVisual instanceof LogicGateVisual) {
      LogicGateVisual visual = (LogicGateVisual) oldVisual;

      if (visual.type == getGateType()) {
        visual.update(node);
        return visual;
      }
    }
    return createVisual(context, node);
  }

  @Override
  protected GeneralPath getOutline(INode node) {
    IRectangle layout = node.getLayout();
    Matrix2D transform = new Matrix2D(layout.getWidth(), 0, 0, layout.getHeight(), layout.getX(), layout.getY());
    return getNodeOutlinePath(getGateType()).createGeneralPath(transform);
  }

  @Override
  protected boolean isHit(IInputModeContext context, PointD location, INode node) {
    return node.getLayout().toRectD().getEnlarged(context.getHitTestRadius()).contains(location);
  }

  private static GeneralPath getNodeOutlinePath(LogicGateType type) {
    switch (type) {
      default:
      case AND:
        return AND_OUTLINE_PATH;
      case NAND:
        return NAND_OUTLINE_PATH;
      case NOR:
        return NOR_OUTLINE_PATH;
      case NOT:
        return NOT_OUTLINE_PATH;
      case OR:
        return OR_OUTLINE_PATH;
    }
  }

  /**
   * An implementation of an {@link VisualGroup} that renders a logic gate of the given {@link LogicGateType type}.
   */
  static class LogicGateVisual extends VisualGroup {
    LogicGateType type;
    Matrix2D transform;
    Line inPortLine1, inPortLine2;
    Path outline;
    Line outPortLine;

    double x, y, w, h;

    public LogicGateVisual(LogicGateType type) {
      this.type = type;
      this.transform = new Matrix2D();

      // add logic-gate outline
      outline = new Path();
      outline.setFill(FILL_COLOR);
      new Pen(OUTLINE_COLOR, 2).styleShape(outline);
      add(outline);

      // add in-port line(s)
      inPortLine1 = new Line();
      inPortLine1.setFill(null);
      new Pen(Color.BLACK, 3).styleShape(inPortLine1);
      add(inPortLine1);

      if (type != LogicGateType.NOT) {
        inPortLine2 = new Line();
        inPortLine2.setFill(null);
        new Pen(Color.BLACK, 3).styleShape(inPortLine2);
        add(inPortLine2);
      }

      // add out-port line
      outPortLine = new Line();
      outPortLine.setFill(null);
      new Pen(Color.BLACK, 3).styleShape(outPortLine);
      add(outPortLine);
    }

    void update(INode node) {
      if (x != node.getLayout().getX() || y != node.getLayout().getY()) {
        x = node.getLayout().getX();
        y = node.getLayout().getY();

        setTranslateX(x);
        setTranslateY(y);
      }

      if (w != node.getLayout().getWidth() || h != node.getLayout().getHeight()) {
        w = node.getLayout().getWidth();
        h = node.getLayout().getHeight();

        // update in-port line(s)
        if (type == LogicGateType.NOT) {
          setLine(inPortLine1, 0, 0.5 * h, 0.1 * w, 0.5 * h);
        } else {
          setLine(inPortLine1, 0, 5, 0.3 * w, 5);
          setLine(inPortLine2, 0, 25, 0.3 * w, 25);
        }

        // update logic-gate outline
        GeneralPath gp = getNodeOutlinePath(type);
        transform.set(w, 0, 0, h, 0, 0);
        gp.updatePath(outline, transform);

        // update out-port line
        if (type == LogicGateType.AND || type == LogicGateType.OR) {
          setLine(outPortLine, 0.8 * w, 0.5 * h, w, 0.5 * h);
        } else {
          setLine(outPortLine, 0.9 * w, 0.5 * h, w, 0.5 * h);
        }
      }
    }

    private void setLine(Line line, double x1, double y1, double x2, double y2) {
      line.setStartX(x1);
      line.setStartY(y1);
      line.setEndX(x2);
      line.setEndY(y2);
    }
  }
}
