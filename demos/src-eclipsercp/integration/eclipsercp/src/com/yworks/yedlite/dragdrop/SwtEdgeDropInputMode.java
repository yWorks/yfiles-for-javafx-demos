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
package com.yworks.yedlite.dragdrop;

import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.graph.styles.IEdgeStyle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.DefaultGraph;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.utils.IListEnumerable;
import com.yworks.yfiles.view.CanvasControl;
import com.yworks.yfiles.view.input.ConcurrencyController;
import com.yworks.yfiles.view.input.CreateEdgeInputMode;
import com.yworks.yfiles.view.input.DefaultPortCandidate;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.IHitTestable;
import com.yworks.yfiles.view.input.IHitTester;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.INodeHitTester;
import com.yworks.yfiles.view.input.ItemDropInputMode;
import com.yworks.yfiles.view.input.IInputMode;
import com.yworks.yfiles.view.input.IPortCandidate;

import javafx.scene.input.DragEvent;

import java.util.Iterator;

/**
 * Handles drop events for edge templates.
 * This mode assumes that drag and drop operations transfer plain text that
 * may be used as key for retrieving edge templates from a
 * {@link com.yworks.yedlite.dragdrop.DragAndDropDataManager text-to-template map}.
 * Additionally, this mode also ensures that edges may be dropped on nodes only. 
 */
public class SwtEdgeDropInputMode extends ItemDropInputMode<IEdge> {
  
  private final DefaultGraph factory;
  private final IEdge edge;

  /**
   * Initializes a new <code>SwtEdgeDropInputMode</code> instance.
   */
  public SwtEdgeDropInputMode() {
    super(IEdge.class, DATA_FORMAT_DROP_ID);
    // restrict drops onto nodes in the associated graph control
    setValidDropHitTestable(new NodeHitTestable());
    factory = new DefaultGraph();
    edge = factory.createEdge(factory.createNode(), factory.createNode());
  }

  /**
   * Accepts drag events if plain text is transferred that identifies
   * an edge template.
   */
  @Override
  protected boolean acceptDrag(DragEvent e) {
    ConcurrencyController controller = getController();
    if (controller == null || !controller.isActive()) {
      return false;
    }
    return e.getDragboard().hasString() && getDraggedItem() != null;
  }

  /**
   * Retrieves the edge template that is identified by the currently
   * transferred text.
   * @return the edge template that determines the visual representation
   * of the new edge created on drop or <code>null</code> if there is no
   * edge template corresponding to the currently transferred text.
   */
  @Override
  protected IEdge getDraggedItem() {
    DragAndDropDataManager manager = DragAndDropDataManager.INSTANCE;
    String dropId = manager.getCurrentId();
    Object dropData = dropId == null ? null : manager.get(dropId);
    if (dropData instanceof IEdge) {
      factory.setStyle(edge, newStyle(((IEdge) dropData).getStyle()));
      return edge;
    }
    return null;
  }

  private IEdgeStyle newStyle(IEdgeStyle style) {
    return (IEdgeStyle) style.clone();
  }

  /**
   * Starts edge creation when dropping an edge template onto a node.
   */
  @Override
  protected void onDragDropped(DragEvent e) {
    boolean success = false;

    CanvasControl canvas = getCanvas();
    // check if there is a node at the current drop location
    INode source = NodeHitTestable.firstNodeHit(
            getInputModeContext(),
            canvas.toWorldCoordinates(new PointD(e.getX(), e.getY())));
    // if so, use the node as source node for a new edge
    if (source != null) {
      // check if interactive edge creation is possible
      IInputMode mode = canvas.getInputMode();
      if (mode instanceof GraphEditorInputMode) {
        CreateEdgeInputMode ceim =
                ((GraphEditorInputMode) mode).getCreateEdgeInputMode();
        // ensure that the new edge uses the template's visual representation
        ceim.getDummyEdgeGraph().setStyle(ceim.getDummyEdge(), getDraggedItem().getStyle());
        // start interactive edge creation
        ceim.doStartEdgeCreation(getPortCandidate(source));
      }

      success = true;
    }

    e.setDropCompleted(success);
  }

  private CanvasControl getCanvas() {
    IInputModeContext context = getInputModeContext();
    return context != null ? context.getCanvasControl() : null;
  }

  /**
   * Finds a source port candidate for interactive edge creation.
   */
  private static IPortCandidate getPortCandidate(INode node) {
    IPort port = first(node.getPorts());
    if (port == null) {
      return new DefaultPortCandidate(
              node, FreeNodePortLocationModel.NODE_CENTER_ANCHORED);
    } else {
      return new DefaultPortCandidate(port);
    }
  }

  /**
   * Returns the first port from the given list.
   */
  private static IPort first(IListEnumerable<IPort> ports) {
    return ports.size() > 0 ? ports.getItem(0) : null;
  }


  /**
   * Performs node hits.
   */
  private static class NodeHitTestable implements IHitTestable {
    /**
     * Determines whether the given coordinates lie within a node
     * that is displayed in the graph control associated to the given context.
     */
    @Override
    public boolean isHit(IInputModeContext context, PointD location) {
      return firstNodeHit(context, location) != null;
    }

    /**
     * Finds the first node that contains the given coordinates in the
     * graph control associated to the given context.
     */
    static INode firstNodeHit(IInputModeContext context, PointD location) {
      final IHitTester<INode> enumerator = context.lookup(INodeHitTester.class);
      return first(enumerator.enumerateHits(context, location));
    }

    /**
     * Retrieves the first node from the given data structure.
     */
    private static INode first(Iterable<INode> nodes) {
      Iterator<INode> it = nodes.iterator();
      return it.hasNext() ? it.next() : null;
    }
  }
}
