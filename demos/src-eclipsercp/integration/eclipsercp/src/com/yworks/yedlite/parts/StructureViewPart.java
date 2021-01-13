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

import java.net.URL;
import java.util.Iterator;
import java.util.HashSet;

import javax.annotation.PostConstruct;

import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.view.ISelectionModel;
import com.yworks.yfiles.view.ItemSelectionChangedEventArgs;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.contexts.RunAndTrack;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.yworks.yedlite.ContextUtils;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.FoldingManager;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IFoldingView;
import com.yworks.yfiles.utils.IEventHandler;
import com.yworks.yfiles.utils.IListEnumerable;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.IGraphSelection;

/**
 * A widget for displaying the nesting structure of a graph in a tree view.
 */
public class StructureViewPart {
  private TreeViewer viewer;
  private IGraph masterGraph;
  private IFoldingView foldingView;
  private boolean selectionChanging;

  private GraphControl graphControl;
  private IGraph graph;

  private ISelectionChangedListener onSelectionChanged;
  private IEventHandler<ItemSelectionChangedEventArgs<IModelItem>> onGraphSelectionChanged;
  private IEventHandler onStructureChanged;
  private IEventHandler onLabelChanged;

  public StructureViewPart() {
    onGraphSelectionChanged = ( source, args ) -> updateInputSelection((IGraphSelection) source);
    onStructureChanged = ( source, args ) -> updateInput();
    onLabelChanged = ( source, args ) -> updateViewer();
  }

  /**
   * Creates this widget's tree view.
   */
  @PostConstruct
  public void createControls(Composite parent, IEclipseContext context) {
    viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    viewer.setContentProvider(new ViewContentProvider());
    viewer.setLabelProvider(new DelegatingStyledCellLabelProvider(new ViewLabelProvider(
            newImageDescriptor("icons/node.png"),
            newImageDescriptor("icons/group.png"),
            newImageDescriptor("icons/folder.png"))));

    update(context);

    context.runAndTrack(new RunAndTrack() {
      @Override
      public boolean changed(IEclipseContext context) {
        update(context);
        return true;
      }
    });
  }

  /**
   * Binds this widget to the graph and its view associated to the given
   * application context.
   */
  private void update(IEclipseContext context) {
    GraphControl graphControl = ContextUtils.getGraphControl(context);
    if (this.graphControl != graphControl) {
      if (this.graphControl != null) {
        viewer.removeSelectionChangedListener(onSelectionChanged);
        onSelectionChanged = null;
        this.graphControl.getSelection().removeItemSelectionChangedListener(onGraphSelectionChanged);
      }
      this.graphControl = graphControl;
      if (this.graphControl != null) {
        onSelectionChanged = new SelectionHandler(this.graphControl);
        viewer.addSelectionChangedListener(onSelectionChanged);
        this.graphControl.getSelection().addItemSelectionChangedListener(onGraphSelectionChanged);
      }
    }

    IGraph graph = ContextUtils.getGraph(context);
    if (this.graph != graph) {
      if (this.graph != null) {
        this.graph.removeNodeCreatedListener(onStructureChanged);
        this.graph.removeNodeRemovedListener(onStructureChanged);
        this.graph.removeIsGroupNodeChangedListener(onStructureChanged);
        this.graph.removeParentChangedListener(onStructureChanged);
        this.graph.removeLabelAddedListener(onLabelChanged);
        this.graph.removeLabelRemovedListener(onLabelChanged);
        this.graph.removeLabelTextChangedListener(onLabelChanged);

        foldingView = null;
        masterGraph = null;
      }
      this.graph = graph;
      if(this.graph != null) {
        this.graph.addNodeCreatedListener(onStructureChanged);
        this.graph.addNodeRemovedListener(onStructureChanged);
        this.graph.addIsGroupNodeChangedListener(onStructureChanged);
        this.graph.addParentChangedListener(onStructureChanged);
        this.graph.addLabelAddedListener(onLabelChanged);
        this.graph.addLabelRemovedListener(onLabelChanged);
        this.graph.addLabelTextChangedListener(onLabelChanged);

        foldingView = graph.getFoldingView();
        FoldingManager foldingManager = foldingView.getManager();
        masterGraph = foldingManager.getMasterGraph();

        updateInput();
      }
    }
  }

  private void updateInputSelection(IGraphSelection selection) {
  if (!selectionChanging) {
    selectionChanging = true;
    ISelectionModel<INode> selectedNodes = selection.getSelectedNodes();
    INode[] nodeArray = selectedNodes.stream().map(n -> foldingView.getMasterItem(n)).toArray(INode[]::new);
    StructuredSelection newSelection = new StructuredSelection(nodeArray);
    viewer.setSelection(newSelection, true);
    selectionChanging = false;
  }
  }

  /**
   * Sets the tree view's top-level items.
   */
  private void updateInput() {
    IListEnumerable<INode> topLevelNodes = masterGraph.getChildren(null);
    INode[] roots = new INode[topLevelNodes.size()];
    int i = 0;
    for (INode root : topLevelNodes) {
      roots[i] = root;
      i++;
    }
    viewer.setInput(roots);
  }

  /**
   * Updates the tree view's internal state.
   */
  private void updateViewer() {
    viewer.refresh();
  }

  /**
   * Creates a new image descriptor for the given image resource path.
   */
  private static ImageDescriptor newImageDescriptor(String path) {
    Bundle bundle = FrameworkUtil.getBundle(ViewLabelProvider.class);
    URL url = FileLocator.find(bundle, new Path(path), null);
    return ImageDescriptor.createFromURL(url);
  }

  /**
   * Adapts this widget's master graph to the  content model of a tree view. 
   */
  class ViewContentProvider implements ITreeContentProvider {

    public void inputChanged(Viewer v, Object oldInput, Object newInput) {
    }

    @Override
    public void dispose() {
    }

    @Override
    public Object[] getElements(Object inputElement) {
      return (INode[]) inputElement;
    }

    @Override
    public Object[] getChildren(Object parentElement) {
      INode node = (INode) parentElement;
      INode[] children = new INode[masterGraph.getChildren(node).size()];
      int i = 0;
      for (INode child : masterGraph.getChildren(node)) {
        children[i] = child;
        i++;
      }
      return children;
    }

    @Override
    public Object getParent(Object element) {
      INode node = (INode) element;
      return masterGraph.getParent(node);
    }

    @Override
    public boolean hasChildren(Object element) {
      INode node = (INode) element;
      return masterGraph.getChildren(node).size() > 0;
    }
  }

  /**
   * Provides the icons and texts for items in the structure tree view.
   * The texts of the items will correspond to the label texts of the first
   * label of the corresponding node.
   */
  class ViewLabelProvider extends LabelProvider implements IStyledLabelProvider {
    private ImageDescriptor nodeImage;
    private ImageDescriptor groupImage;
    private ImageDescriptor folderImage;
    private ResourceManager resourceManager;

    public ViewLabelProvider(ImageDescriptor nodeImage, ImageDescriptor groupImage, ImageDescriptor folderImage) {
      this.nodeImage = nodeImage;
      this.groupImage = groupImage;
      this.folderImage = folderImage;
    }

    /**
     * Returns the text of the first label of the node corresponding to the
     * given structure tree view item.
     */
    @Override
    public StyledString getStyledText(Object element) {
      if (element instanceof INode) {
        INode node = (INode) element;
        return new StyledString(getNodeLabelText(node));
      }
      return null;
    }

    /**
     * Returns an icon for the node corresponding to the given structure tree
     * view item.
     */
    @Override
    public Image getImage(Object element) {
      if (element instanceof INode) {
        INode node = (INode) element;
        if (isFolder(node, masterGraph, foldingView)) {
          return getResourceManager().createImage(folderImage);
        } else if (isGroup(node, masterGraph)) {
          return getResourceManager().createImage(groupImage);
        } else {
          return getResourceManager().createImage(nodeImage);
        }
      }

      return super.getImage(element);
    }

    @Override
    public void dispose() {
      // garbage collect system resources
      if (resourceManager != null) {
        resourceManager.dispose();
        resourceManager = null;
      }
    }

    /**
     * Returns the resource manager for creating structure tree view item icons.
     */
    protected ResourceManager getResourceManager() {
      if (resourceManager == null) {
        resourceManager = new LocalResourceManager(JFaceResources.getResources());
      }
      return resourceManager;
    }

    /**
     * Retrieves the text of the first label of the given node.
     */
    private String getNodeLabelText(INode node) {
      IListEnumerable<ILabel> labels = node.getLabels();
      String name = null;
      if (labels.size() > 0) {
        name = labels.getItem(0).getText();
      }
      return name == null || name.isEmpty() ? "no label" : name;
    }
  }

  /**
   * Determines if the given node is a group node.
   */
  public static boolean isGroup(INode masterNode, IGraph masterGraph) {
    return masterGraph.isGroupNode(masterNode);
  }

  /**
   * Determines if the given node is a folder node in the given folding view.
   */
  public static boolean isFolder(
          INode masterNode, IGraph masterGraph, IFoldingView foldingView
  ) {
    return !foldingView.getGraph().isGroupNode(foldingView.getViewItem(masterNode)) &&
           masterGraph.isGroupNode(masterNode);
  }


  /**
   * Handles selection changes in the tree view.
   * If an item in the tree view is selected, the corresponding node will be
   * selected as well. Additionally, the viewport will be centered on the
   * selected node(s).
   */
  class SelectionHandler implements ISelectionChangedListener {
    final GraphControl graphControl;

    SelectionHandler(GraphControl graphControl) {
      this.graphControl = graphControl;
    }

    @Override
    public void selectionChanged(SelectionChangedEvent event) {
      if (selectionChanging) {
        return;
      }

      selectionChanging = true;

      IStructuredSelection structuredSelection = ((TreeViewer) event.getSource()).getStructuredSelection();
      if (structuredSelection.isEmpty()) {
        graphControl.getSelection().clear();
      } else {
        IGraphSelection selection = graphControl.getSelection();
        selection.clear();

        INode lastRepresentative = null;
        HashSet<INode> selectedNodes = new HashSet<INode>();
        // determine the nodes in the folding view that correspond to the
        // selected items of the structure tree view
        for (Iterator it = structuredSelection.iterator(); it.hasNext(); ) {
          INode node = (INode) it.next();
          INode representative = foldingView.getViewItem(node);
          selectedNodes.add(representative);
          if (representative != null && !selection.isSelected(representative)) {
            lastRepresentative = representative;
          }
        }

        // center the viewport of the foldingView on the node corresponding to
        // the last selected item
        if (lastRepresentative != null) {
          selection.setSelected(lastRepresentative, true);
          PointD nodeCenter = lastRepresentative.getLayout().getCenter();
          graphControl.zoomToAnimated(nodeCenter, graphControl.getZoom());
        }

        // prevent nodes other than the ones corresponding to selected items
        // from being marked as selected in the folding view
        IGraph graph = graphControl.getGraph();
        for (INode node : graph.getNodes()) {
          if (!selectedNodes.contains(node) && selection.isSelected(node)) {
            selection.setSelected(node, false);
          }
        }
      }

      selectionChanging = false;
    }
  }
}
