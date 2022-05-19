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
package com.yworks.yedlite.dragdrop;

import javafx.scene.input.DragEvent;

import com.yworks.yfiles.graph.DefaultGraph;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.CollapsibleNodeStyleDecorator;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.utils.ItemEventArgs;
import com.yworks.yfiles.view.input.ConcurrencyController;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.NodeDropInputMode;

/**
 * Handles drop events for node templates.
 * This mode assumes that drag and drop operations transfer plain text that
 * may be used as key for retrieving a INode instance from a
 * {@link com.yworks.yedlite.dragdrop.DragAndDropDataManager text-to-INode map}.
 */
public class SwtNodeDropInputMode extends NodeDropInputMode {
  
  private final DefaultGraph factory;
  private final INode node;
  
  /**
   * Indicates successful node creation.
   * See in-code comments in
   * {@link #onDragDropped(javafx.scene.input.DragEvent)}.
   */
  boolean nodeCreated;

  /**
   * Initializes a new <code>SwtNodeDropInputMode</code> instance.
   */
  public SwtNodeDropInputMode() {

    // Enables preview during drag operations.
    // Keep in mind that, prior to JDK 8u40, due to an issue in the DnD handling in FXCanvas (RT-37906),
    // using the preview feature of the yFiles library may cause problems when dropping a dragged node
    // outside of the GraphControl.
    // To prevent this, the native SWT preview, which shows a simple image, can also be used at this point.
    // To do this, just disable the option here instead (setPreviewEnabled(false);) and adjust the
    // TableHandler.dragStart method in the PaletteViewPart class.
    setPreviewEnabled(true);
    // define nodes with CollapsibleNodeStyleDecorator as group nodes
    setIsGroupNodePredicate(node -> node.getStyle() instanceof CollapsibleNodeStyleDecorator);
    factory = new DefaultGraph();
    node = factory.createNode();
    
    addItemCreatedListener(this::onEvent);
  }

  /**
   * Accepts drag events if plain text is transferred that identifies
   * a node template.
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
   * Retrieves the node template that is identified by the currently
   * transferred text.
   * @return an INode instance that determines the visual representation
   * of the new node created on drop or <code>null</code> if there is no
   * INode instance corresponding to the currently transferred text.
   */
  @Override
  protected INode getDraggedItem() {
    DragAndDropDataManager manager = DragAndDropDataManager.INSTANCE;
    String dropId = manager.getCurrentId();
    Object dropData = dropId == null ? null : manager.get(dropId);
    if (dropData instanceof INode) {
      final INode prototype = (INode) dropData;
      factory.setNodeLayout(node, prototype.getLayout().toRectD());
      factory.setStyle(node, newStyle(prototype.getStyle()));
      return node;
    }
    return null;
  }

  private INodeStyle newStyle(INodeStyle style) {
    return (INodeStyle) style.clone();
  }

  /**
   * Creates a new node when dropping a node template into the associated
   * graph control.
   */
  @Override
  protected void onDragDropped(DragEvent e) {
    // workaround for yFiles for JavaFX shortcoming
    // for inter-framework drag and drop operations it does not suffice
    // to mark the drop event as completed on successful node drops
    // the event needs to be marked as completed even if no new node could
    // created for some reason
    nodeCreated = false;
    super.onDragDropped(e);

    if (!nodeCreated) {
      e.setDropCompleted(false);
    }
  }

  /**
   * Sets a flag indicating successful node creation.
   * See in-code comments in
   * {@link #onDragDropped(javafx.scene.input.DragEvent)}.
   */
  private void onEvent( final Object source, final ItemEventArgs<INode> args ) {
    //store the style of the created node as default style
    storeNodeDefaults(args);
    
    nodeCreated = true;
  }

  private void storeNodeDefaults(ItemEventArgs<INode> args) {
    final IInputModeContext context = getInputModeContext();

    if (context != null) {
      final IGraph iGraph = context.getGraph();
      final INode n = args.getItem();

      if (getIsGroupNodePredicate().test(n)) {
        final IGraph groupedGraph = context.getGraph();
        groupedGraph.getGroupNodeDefaults().setStyle(n.getStyle());
      } else {
        iGraph.getNodeDefaults().setStyle(n.getStyle());
      }
    }
  }
}
