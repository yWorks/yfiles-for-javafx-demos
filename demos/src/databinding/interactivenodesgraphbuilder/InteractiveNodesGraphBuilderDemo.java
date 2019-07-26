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
package databinding.interactivenodesgraphbuilder;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.AdjacentNodesGraphBuilder;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.ILabelDefaults;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.INodeDefaults;
import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayoutData;
import com.yworks.yfiles.layout.hierarchic.LayoutMode;
import com.yworks.yfiles.utils.ICloneable;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.ShowFocusPolicy;
import com.yworks.yfiles.view.input.GraphViewerInputMode;
import com.yworks.yfiles.view.input.IInputMode;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import toolkit.DemoApplication;
import toolkit.WebViewUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.web.WebView;

/**
 * Shows data binding with class {@link AdjacentNodesGraphBuilder}.
 */
public class InteractiveNodesGraphBuilderDemo extends DemoApplication {
  public GraphControl graphControl;
  public WebView helpView;
  public ListView<BusinessData> nodesView;
  public ListView<BusinessData> predecessorsView;
  public ListView<BusinessData> successorsView;
  public Label currentItemLabel;

  private AdjacentNodesGraphBuilder<BusinessData, BusinessData> graphBuilder;
  private BusinessData currentItem;

  /**
   * Initializes the controller.
   */
  public void initialize() {
    // setup the help text on the right side.
    WebViewUtils.initHelp(helpView, this);

    nodesView.getSelectionModel().selectedItemProperty().addListener(this::selectedValueChanged);

    // Initialize input mode
    graphControl.setInputMode(createInputMode());

    graphControl.getFocusIndicatorManager().setShowFocusPolicy(ShowFocusPolicy.ALWAYS);
    graphControl.currentItemProperty().addListener(this::currentItemChanged);

    initializeGraphDefaults();
  }

  /**
   * Creates a viewer input mode that allows panning and dragging nodes.
   */
  private IInputMode createInputMode() {
    // create new input mode
    GraphViewerInputMode gvim = new GraphViewerInputMode();
    gvim.setSelectableItems(GraphItemTypes.NONE);

    // add context menu callback to add the right-clicked BusinessData as Predecessor/Successor of currentItem
    gvim.addPopulateItemContextMenuListener((source, args) -> {
      Object tag = args.getItem().getTag();
      if (tag instanceof BusinessData && currentItem != null) {
        BusinessData businessData = (BusinessData) tag;
        if (businessData != currentItem) {
          ContextMenu menu = (ContextMenu) args.getMenu();
          if (!currentItem.getPredecessors().contains(businessData)) {
            MenuItem addPredecessorItem = new MenuItem("Add as Predecessor");
            addPredecessorItem.setOnAction(event -> {
              currentItem.getPredecessors().add(businessData);
              update(true, currentItem, businessData);
            });
            menu.getItems().add(addPredecessorItem);
          }
          if (!currentItem.getSuccessors().contains(businessData)) {
            MenuItem addSuccessorItem = new MenuItem("Add as Successor");
            addSuccessorItem.setOnAction(event -> {
              currentItem.getSuccessors().add(businessData);
              update(true, currentItem, businessData);
            });
            menu.getItems().add(addSuccessorItem);
          }
        }
      }
    });

    return gvim;
  }

  /**
   * Sets default styles for the graph.
   */
  void initializeGraphDefaults() {
    IGraph graph = graphControl.getGraph();

    ShapeNodeStyle nodeStyle = new ShapeNodeStyle();
    nodeStyle.setShape(ShapeNodeShape.ROUND_RECTANGLE);
    nodeStyle.setPaint(Color.rgb(255, 237, 204));
    nodeStyle.setPen(Pen.getDarkOrange());
    INodeDefaults nodeDefaults = graph.getNodeDefaults();
    nodeDefaults.setStyle(nodeStyle);
    DefaultLabelStyle nodeLabelStyle = new DefaultLabelStyle();
    nodeLabelStyle.setFont(Font.font("System", FontWeight.NORMAL, FontPosture.REGULAR, 13));
    ILabelDefaults nodeLabelDefaults = nodeDefaults.getLabelDefaults();
    nodeLabelDefaults.setStyle(nodeLabelStyle);
    nodeLabelDefaults.setLayoutParameter(InteriorLabelModel.CENTER);

    PolylineEdgeStyle edgeStyle = new PolylineEdgeStyle();
    edgeStyle.setSmoothingLength(20);
    edgeStyle.setTargetArrow(IArrow.DEFAULT);
  }

  /**
   * Creates an initial diagram when the demo application becomes visible for
   * the first time.
   * <p>
   * <b>Note:</b> The controls for modifying business data work on the business
   * data structure created in {@link #createInitialBusinessData()} and do not
   * reflect the induced predecessor/successor relationships displayed in the
   * application's graph view.
   * </p>
   */
  @Override
  protected void onLoaded() {
    // configure the graph builder and its data sources
    ObservableList<BusinessData> nodeSource = createInitialBusinessData();
    nodesView.setItems(nodeSource);
    graphBuilder = new AdjacentNodesGraphBuilder<>(graphControl.getGraph());
    graphBuilder.setNodesSource(nodeSource);
    graphBuilder.setSuccessorProvider(BusinessData::getSuccessors);
    graphBuilder.setPredecessorProvider(BusinessData::getPredecessors);
    graphBuilder.setNodeLabelProvider(BusinessData::getNodeName);
    graphBuilder.addNodeCreatedListener((source, args) -> {
      INode node = args.getItem();
      // Set optimal node size
      ILabel l1 = node.getLabels().first();
      SizeD size1 = l1.getPreferredSize();
      SizeD bestSize = new SizeD(size1.getWidth() + 10, size1.getHeight() + 12);
      // Set node to that size. Location is irrelevant here, since we're running a layout anyway
      args.getGraph().setNodeLayout(node, new RectD(PointD.ORIGIN, bestSize));
    });

    // Create the graph from the model data
    graphBuilder.buildGraph();

    applyLayout(false);
  }

  /**
   * Creates the node source.
   * @return A list of {@link BusinessData} items.
   */
  private static ObservableList<BusinessData> createInitialBusinessData() {
    BusinessData jenny = new BusinessData("Jenny");
    BusinessData julia = new BusinessData("Julia");
    BusinessData marc = new BusinessData("Marc");
    BusinessData martin = new BusinessData("Martin");
    BusinessData natalie = new BusinessData("Natalie");
    BusinessData nicole = new BusinessData("Nicole");
    BusinessData petra = new BusinessData("Petra");
    BusinessData stephen = new BusinessData("Stephen");
    BusinessData tim = new BusinessData("Tim");
    BusinessData tom = new BusinessData("Tom");
    BusinessData tony = new BusinessData("Tony");

    julia.getPredecessors().add(jenny);
    julia.getSuccessors().add(petra);
    marc.getPredecessors().add(julia);
    marc.getSuccessors().add(tim);
    martin.getPredecessors().add(julia);
    martin.getSuccessors().add(natalie);
    martin.getSuccessors().add(nicole);
    nicole.getSuccessors().add(petra);
    tim.getSuccessors().add(tom);
    tom.getSuccessors().add(tony);
    tony.getSuccessors().add(tim);
    tony.getPredecessors().add(julia);
    stephen.getSuccessors().add(tom);

    ObservableList<BusinessData> list = FXCollections.observableArrayList();
    list.add(marc);
    list.add(martin);
    list.add(stephen);
    return list;
  }

  /**
   * Updates the current item of the graph component when the selected value
   * in the {@code nodesView} list has changed.
   */
  private void selectedValueChanged(
          ObservableValue<? extends BusinessData> observable, BusinessData oldValue, BusinessData newValue
  ) {
    if (currentItem != newValue) {
      IGraph graph = graphControl.getGraph();
      for (INode node : graph.getNodes()) {
        if (node.getTag() == null) {
          graphControl.setCurrentItem(node);
          break;
        }
      }
      setCurrentItem(newValue);
    }
  }

  /**
   * Set the current item and update the successor and predecessor list.
   * @param selected The item to select.
   */
  private void setCurrentItem( BusinessData selected ) {
    if (currentItem == selected) {
      return;
    }

    currentItem = selected;
    if (selected == null) {
      currentItemLabel.setText("");
      predecessorsView.setItems(FXCollections.observableArrayList());
      successorsView.setItems(FXCollections.observableArrayList());
    } else {
      currentItemLabel.setText(selected.getNodeName());
      predecessorsView.setItems(selected.getPredecessors());
      successorsView.setItems(selected.getSuccessors());
    }
  }

  /**
   * Called when the GraphControl's current item has changed.
   * Updates the current item and the successor and predecessor lists.
   */
  private void currentItemChanged(
          ObservableValue<? extends IModelItem> observable, IModelItem oldValue, IModelItem newValue
  ) {
    BusinessData item = newValue == null ? null : (BusinessData) newValue.getTag();
    if (this.currentItem != item) {
      if (nodesView.getItems().contains(item)) {
        nodesView.getSelectionModel().select(item);
      } else {
        nodesView.getSelectionModel().clearSelection();
      }
      setCurrentItem(item);
    }
  }

  /**
   * Updates the graph after changes to the business data.
   * @param incremental Whether to keep the unchanged parts of the graph stable.
   * @param incrementalNodes The nodes which have changed.
   */
  public void update( boolean incremental, BusinessData... incrementalNodes ) {
    graphBuilder.updateGraph();
    applyLayout(incremental, incrementalNodes);
  }

  /**
   * Applies the layout. Uses a {@link HierarchicLayout}. If single graph items
   * are created or removed, the incremental mode of this layout algorithm is
   * used to keep most of the current layout of the graph unchanged.
   * @param incremental if set to {@code true} incremental.
   * @param incrementalNodes the incremental nodes.
   */
  private void applyLayout( boolean incremental, BusinessData... incrementalNodes ) {
    HierarchicLayout layout = new HierarchicLayout();
    HierarchicLayoutData layoutData = null;
    if (!incremental) {
      layout.setLayoutMode(LayoutMode.FROM_SCRATCH);
    } else {
      layout.setLayoutMode(LayoutMode.INCREMENTAL);

      if (incrementalNodes != null && incrementalNodes.length > 0) {
        // we need to add hints for incremental nodes
        ArrayList<INode> nodes = new ArrayList<>();
        for (BusinessData data : incrementalNodes) {
          nodes.add(graphBuilder.getNode(data));
        }
        layoutData = new HierarchicLayoutData();
        layoutData.getIncrementalHints().setIncrementalLayeringNodes(nodes);
      }
    }
    graphControl.morphLayout(layout, Duration.ofMillis(1000), layoutData);
  }


  /**
   * The "add to node source" button has been clicked.
   * Asks for a name for the new element and updates the lists.
   */
  public void addItem() {
    BusinessData businessData = addNewItem();
    if (businessData == null) {
      return;
    }
    nodesView.getItems().add(businessData);
    update(true, businessData);
  }

  /**
   * The "remove from node source" button has been clicked.
   * Removes the current item and updates the list.
   */
  public void removeItem() {
    BusinessData item = nodesView.getSelectionModel().getSelectedItem();
    if (item != null) {
      nodesView.getItems().remove(item);
      update(true);
    }
  }

  /**
   * The "add predecessor" button has been clicked.
   * Asks for a name for the new element and updates the lists.
   */
  public void addPredecessor() {
    if (currentItem != null) {
      BusinessData businessData = addNewItem();
      if (businessData == null) {
        return;
      }
      currentItem.getPredecessors().add(businessData);
      update(true, businessData);
    }
  }

  /**
   * The "remove predecessor" button has been clicked.
   * Removes the current item and updates the list.
   */
  public void removePredecessor() {
    if (currentItem != null) {
      BusinessData item = predecessorsView.getSelectionModel().getSelectedItem();
      if (item != null) {
        currentItem.getPredecessors().remove(item);
      }
      update(true);
    }
  }

  /**
   * The "add successor" button has been clicked.
   * Asks for a name for the new element and updates the lists.
   */
  public void addSuccessor() {
    if (currentItem != null) {
      BusinessData businessData = addNewItem();
      if (businessData == null) {
        return;
      }
      currentItem.getSuccessors().add(businessData);
      update(true, businessData);
    }
  }

  /**
   * The "remove successor" button has been clicked.
   * Removes the current item and updates the list.
   */
  public void removeSuccessor() {
    if (currentItem != null) {
      BusinessData item = successorsView.getSelectionModel().getSelectedItem();
      if (item != null) {
        currentItem.getSuccessors().remove(item);
      }
      update(true);
    }
  }

  /**
   * Opens a dialog to enter a name for a new element.
   * If a name has been entered and OK has been clicked a new BusinessData
   * object is returned. If no name has been entered or Cancel has been clicked
   * {@code null} is returned.
   * @return A newly created business object or {@code null}.
   */
  private BusinessData addNewItem() {
    Optional<String> result = new TextInputDialog().showAndWait();
    if (result.isPresent()) {
      String name = result.get();
      if (!name.isEmpty()) {
        return new BusinessData(name);
      }
    }
    return null;
  }


  public static void main( String[] args ) {
    launch();
  }


  /**
   * Represents an object of the business data.
   */
  public static final class BusinessData implements ICloneable {
    String nodeName;
    ObservableList<BusinessData> successors;
    ObservableList<BusinessData> predecessors;

    public BusinessData() {
      this("Unnamed");
    }

    public BusinessData( String name ) {
      nodeName = name;
      successors = FXCollections.observableArrayList();
      predecessors = FXCollections.observableArrayList();
    }

    public String getNodeName() {
      return nodeName;
    }

    public void setNodeName( String nodeName ) {
      this.nodeName = nodeName;
    }

    public ObservableList<BusinessData> getSuccessors() {
      return successors;
    }

    public ObservableList<BusinessData> getPredecessors() {
      return predecessors;
    }

    @Override
    public String toString() {
      return getNodeName();
    }

    @Override
    public BusinessData clone() {
      try {
        return (BusinessData) super.clone();
      } catch (CloneNotSupportedException exception) {
        throw new RuntimeException("Class doesn't implement java.lang.Cloneable");
      }
    }
  }
}
