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
package layout.edgebundling;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.view.*;

import javafx.scene.paint.Color;

/**
 * Installs a visual representation of a highlight decoration for edges and nodes such
 * that an edge/node highlight is drawn below the node group.
 */
class DemoHighlightManager extends HighlightIndicatorManager<IModelItem> {
  private final ICanvasObjectGroup edgeHighlightGroup;

  /**
   * Initializes a new highlight manager for the given graph control.
   */
  DemoHighlightManager( GraphControl graphControl ) {
    super(graphControl);
    GraphModelManager modelManager = graphControl.getGraphModelManager();
    modelManager.setHierarchicNestingPolicy(HierarchicNestingPolicy.NONE);
    edgeHighlightGroup = modelManager.getContentGroup().addGroup();
    edgeHighlightGroup.below(modelManager.getNodeGroup());
  }

  /**
   * Retrieves the Canvas Object group to use for the given item.
   */
  @Override
  protected ICanvasObjectGroup getCanvasObjectGroup( IModelItem item ) {
    if (item instanceof IEdge) {
      return edgeHighlightGroup;
    }
    return super.getCanvasObjectGroup(item);
  }

  /**
   * Retrieves the installer for the given item.
   * Called from {@link ModelManager#install(Object)}.
   */
  @Override
  protected ICanvasObjectInstaller getInstaller( IModelItem item ) {
    if (item instanceof IEdge) {
      EdgeStyleDecorationInstaller installer = new EdgeStyleDecorationInstaller();
      installer.setEdgeStyle(new DemoEdgeStyle(5, Color.RED, Color.GOLD));
      installer.setZoomPolicy(StyleDecorationZoomPolicy.VIEW_COORDINATES);
      return installer;
    } else if (item instanceof INode) {
      NodeStyleDecorationInstaller installer = new NodeStyleDecorationInstaller();
      installer.setNodeStyle(newDemoNodeStyle(Color.RED));
      installer.setMargins(InsetsD.EMPTY);
      installer.setZoomPolicy(StyleDecorationZoomPolicy.WORLD_COORDINATES);
      return installer;
    }
    return super.getInstaller(item);
  }

  /**
   * The NodeStyle for a selected node.
   */
  private static ShapeNodeStyle newDemoNodeStyle( Color color ) {
    ShapeNodeStyle demoNodeStyle = new ShapeNodeStyle();
    demoNodeStyle.setPaint(color);
    demoNodeStyle.setPen(null);
    demoNodeStyle.setShape(ShapeNodeShape.ROUND_RECTANGLE);
    return demoNodeStyle;
  }
}
