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
package com.yworks.yedlite.parts;

import javafx.embed.swt.SWTFXUtils;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.yworks.yedlite.dragdrop.DragAndDropDataManager;
import com.yworks.yedlite.ContextUtils;
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.styles.CollapsibleNodeStyleDecorator;
import com.yworks.yfiles.graph.styles.IEdgeStyle;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graph.labelmodels.InteriorStretchLabelModel;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.DefaultGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.ContextConfigurator;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisualCreator;

/**
 * A view that depicts a palette. Several differently styled nodes are listed and the user can drag
 * them from the palette and drop them into the editor to create a node with the same style as the
 * dragged node from the palette.
 */
public class PaletteViewPart {

  static final ElementSelectionType NORMAL_NODES_SELECTION_TYPE = new ElementSelectionType();

  static final ElementSelectionType GROUP_NODES_SELECTION_TYPE = new ElementSelectionType();

  static final ElementSelectionType EDGES_SELECTION_TYPE = new ElementSelectionType();

  @Inject
  private IEclipseContext ctx;

  @PostConstruct
  public void createPartControl(Composite parent) {
    // our palette is a simple composite with row layout filled with buttons that have an image
    final Composite composite = new Composite(parent, SWT.BORDER | SWT.SINGLE);
    composite.setLayout(new RowLayout());

    // fill the palette with the template nodes
    populatePalette(composite);
  }

  private static void configureDnd(Button button) {
    // we can only use strings to send with the drag and drop events
    Transfer[] transfers = new Transfer[] { TextTransfer.getInstance() };
    DragSource dragSource = new DragSource(button, DND.DROP_MOVE | DND.DROP_COPY);
    dragSource.setTransfer(transfers);
    dragSource.addDragListener(new ButtonDragHandler(button));
  }

  protected void populatePalette(Composite composite) {

  }

  protected void populateEdgePalette(Composite composite, ElementSelectionType elementType, NamedStyle<IEdgeStyle>... elements) {
    Display display = composite.getDisplay();
    DefaultGraph graph = new DefaultGraph();

    // each displayed button is assigned a unique identifier
    // (the button's data) in the loop below; additionally, this
    // identifier is mapped to a node template DragAndDropDataManager
    // stores said mappings for easy access in SwtNodeDropInputMode
    DragAndDropDataManager manager = DragAndDropDataManager.INSTANCE;

    if (elements != null) {
      for (NamedStyle<IEdgeStyle> template : elements) {
        IEdgeStyle style = template.style;

        // dummy source node for edge templates
        final INode src = graph.createNode(new RectD(0, 100, 1, 40));
        // dummy target node for edge templates
        final INode tgt = graph.createNode(new RectD(40, 100, 1, 40));

        final IEdge edge = graph.createEdge(src, tgt, style);

        Button item = createButton(composite, elementType);

        //set image of the button displaying the edge style
        item.setImage(new Image(display, toImage(edge)));

        //wire up the drag and drop functionality
        configureDnd(item);

        // the data that is actually transferred during the dnd operation
        // this data serves to identify the edge template that is used to
        // start edge creation when dropped into the graph control
        final String id = "\"edge\":{\"type\":\"" + template.id + "\"}";
        // make the transfer data available to the drag source listener that
        // is queried when the dnd operation is initiated
        item.setData(id);
        // map the transferred identifier to the corresponding template
        manager.put(id, edge);
      }
    }

  }

  private Button createButton(Composite parent, ElementSelectionType elementType) {
    Button button = new Button(parent, SWT.FLAT | SWT.TOGGLE);

    button.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseDown(MouseEvent e) {
        elementType.changeSelection(button);
        updateDefaults((String) button.getData());
        button.setSelection(true);
      }
    });

    return button;
  }

  private void updateDefaults(String buttonData) {
    final Object elem = DragAndDropDataManager.INSTANCE.get(buttonData);

    if (elem != null) {
      final GraphControl gc = ContextUtils.getGraphControl(ctx);
      if (gc != null) {
        final IGraph iGraph = gc.getGraph();
        if (elem instanceof INode) {
          final INode node = (INode) elem;
          final INodeStyle style = node.getStyle();
          if (isGroupNodeStyle(style)) {
            iGraph.getGroupNodeDefaults().setStyle(style);
          } else {
            iGraph.getNodeDefaults().setStyle(node.getStyle());
          }
        } else if (elem instanceof IEdge) {
          final IEdge edge = (IEdge) elem;
          iGraph.getEdgeDefaults().setStyle(edge.getStyle());
        }
      }
    }
  }

  /**
   * Populates the given SWT composite with node templates.
   */
  protected void populateNodePalette(Composite composite, ElementSelectionType elementType, NamedStyle<INodeStyle>... elements) {
    if (elements != null) {
      Display display = composite.getDisplay();

      GraphControl control = new GraphControl();
      IGraph graph = control.getGraph();
      ContextConfigurator cc = new ContextConfigurator(null);

      // each displayed button is assigned a unique identifier
      // (the button's data) in the loop below; additionally, this
      // identifier is mapped to a node template DragAndDropDataManager
      // stores said mappings for easy access in SwtEdgeDropInputMode
      DragAndDropDataManager manager = DragAndDropDataManager.INSTANCE;

      for (NamedStyle<INodeStyle> template : elements) {
        boolean isGroup = isGroupNodeStyle(template.style);
        double size = isGroup ? 70.0 : 30.0;

        RectD bounds = new RectD(0, 0, size, size);
        INode node = graph.createNode(bounds, template.style);
        if (isGroup) {
          graph.addLabel(node, template.description, InteriorStretchLabelModel.NORTH, new DefaultLabelStyle());
        }

        Button item = createButton(composite, elementType);

        // show an image of the node template in the palette
        cc.setWorldBounds(bounds);
        IRenderContext ctx = cc.createRenderContext(control);
        item.setImage(new Image(display, toImage(ctx, node)));

        // wire up the drag and drop functionality
        configureDnd(item);

        // the data that is actually transferred during the dnd operation
        // this data serves to identify the node template that is used to
        // create a new node when dropped into the graph control
        String id = "\"node\":{\"type\":\"" + template.id + "\"}";
        // make the transfer data available to the drag source listener that
        // is queried when the dnd operation is initiated
        item.setData(id);
        // map the transferred identifier to the corresponding template
        manager.put(id, node);
      }
    }
  }

  private boolean isGroupNodeStyle(INodeStyle style) {
    return style instanceof CollapsibleNodeStyleDecorator;
  }

  /**
  * Creates a raster image displaying the given edge.
  */
 private static ImageData toImage(IEdge edge) {
   IRectangle src = ((INode) edge.getSourcePort().getOwner()).getLayout();
   IRectangle tgt = ((INode) edge.getTargetPort().getOwner()).getLayout();
   java.awt.geom.Rectangle2D.Double helper = new java.awt.geom.Rectangle2D.Double();
   helper.setFrame(src.getX(), src.getY(), src.getWidth(), src.getHeight());
   helper.add(tgt.getX(), tgt.getY());
   helper.add(tgt.getX() + tgt.getWidth(), tgt.getY() + tgt.getHeight());

   IEdgeStyle style = edge.getStyle();
   IVisualCreator creator = style.getRenderer().getVisualCreator(edge, style);
   SnapshotParameters params = new SnapshotParameters();
   params.setViewport(new Rectangle2D(helper.getX(), helper.getY(), helper.getWidth(), helper.getHeight()));
   return SWTFXUtils.fromFXImage(creator.createVisual(null).snapshot(params, null), null);
 }


  /**
   * Creates a raster image displaying the given node.
   */
  private static ImageData toImage( IRenderContext ctx, INode node ) {
    IRectangle layout = node.getLayout();
    // create a visualization of the node with its style
    INodeStyle style = node.getStyle();
    IVisualCreator creator = style.getRenderer().getVisualCreator(node, style);
    Node visual = creator.createVisual(ctx);
    // return an image of the visual
    SnapshotParameters params = new SnapshotParameters();
    params.setViewport(new Rectangle2D(layout.getX() - 5, layout.getY() - 5, layout.getWidth() + 10, layout.getHeight() + 10));
    return SWTFXUtils.fromFXImage(visual.snapshot(params, null), null);
  }

  /**
   * Handles drag source events for the SWT widget that serves as
   * palette for edge and node templates.
   */
  private static final class ButtonDragHandler extends DragSourceAdapter {

    private Button button;

    ButtonDragHandler(Button button) {
      this.button = button;
    }

    /**
     * Extracts the template identifier from the selected button on drag
     * start. The selected template identifier is exposed to
     * {@link com.yworks.yedlite.dragdrop.SwtNodeDropInputMode} as
     * {@link DragAndDropDataManager}'s
     * {@link DragAndDropDataManager#setCurrentId(String) current transfer data}.
     */
    @Override
    public void dragStart(DragSourceEvent e) {
      e.doit = true;
      DragAndDropDataManager.INSTANCE.setCurrentId((String) button.getData());
      // The following prevents the image of the template to be shown as preview,
      // since our GraphControl has its own.
      // Keep in mind that, prior to JDK 8u40, due to an issue in the DnD handling in FXCanvas (RT-37906),
      // using the preview feature of the yFiles library may cause problems when dropping a dragged node
      // outside of the GraphControl.
      // To prevent this, the native SWT preview, which shows a simple image, can also be used at this point.
      // To do this, simply do not set the image here to a blank image and set the NodePreviewShowingEnabled
      // property of the SwtNodeDropInputMode to false.
      e.image = new Image(button.getDisplay(), 1, 1);
    }

    /**
     * Extracts the template identifier from the selected button for use
     * as the native drag event's transfer data.
     */
    @Override
    public void dragSetData(DragSourceEvent e) {
      e.data = button.getData();
    }

    /**
     * Clears {@link DragAndDropDataManager}'s
     * {@link DragAndDropDataManager#setCurrentId(String) current transfer data}.
     */
    @Override
    public void dragFinished(DragSourceEvent event) {
      DragAndDropDataManager.INSTANCE.setCurrentId(null);
    }
  }

  static class NamedStyle<TStyle> {

    final String id;
    final String description;
    final TStyle style;

    NamedStyle(String id, String description, TStyle style) {
      this.id = id;
      this.description = description;
      this.style = style;
    }
  }

  /**
   * This class assures that of a specific type of drag-and-drop elements only one can be selected
   * at the same time.
   */
  static class ElementSelectionType {

    private ElementSelectionType() {
    }

    private Button selected;

    void changeSelection(Button button) {
      if (selected != null && selected != button) {
        selected.setSelection(false);
      }
      selected = button;
    }
  }
}
