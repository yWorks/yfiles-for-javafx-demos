/****************************************************************************
 **
 ** This demo file is part of yFiles for JavaFX 3.3.
 **
 ** Copyright (c) 2000-2020 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package viewer.graphcopy;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.GraphCopier;
import com.yworks.yfiles.graph.IBend;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.labelmodels.InteriorStretchLabelModel;
import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.graph.portlocationmodels.IPortLocationModelParameter;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.PanelNodeStyle;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.IGraphSelection;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.GraphViewerInputMode;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.KeyboardInputMode;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.WebView;
import toolkit.DemoApplication;
import toolkit.WebViewUtils;

/**
 * Shows how to use yFiles' {@link GraphCopier} utility class.
 */
public class GraphCopyDemo extends DemoApplication {
  // The GraphControl for the left side
  public GraphControl originalGraphControl;
  // The GraphControl for the right side
  public GraphControl copyGraphControl;

  public WebView helpView;

  /**
   * Copies the selected part of the original graph to the copy graph.
   */
  public void copyGraph() {
    IGraph source = originalGraphControl.getGraph();
    IGraph target = copyGraphControl.getGraph();
    target.clear();

    IGraphSelection selection = originalGraphControl.getSelection();

    GraphCopier graphCopier = new GraphCopier();
    graphCopier.copy(source, item -> {
      if (item instanceof INode) {
        // copy selected node
        return selection.isSelected(item);
      } else if (item instanceof IEdge) {
        // copy selected edge when its source and target is also selected
        // because an edge cannot exist without its incident nodes
        return selection.isSelected(item) &&
            selection.isSelected(((IEdge) item).getSourceNode()) &&
            selection.isSelected(((IEdge) item).getTargetNode());
      } else if (item instanceof IPort) {
        return selection.isSelected(((IPort) item).getOwner());
      } else if (item instanceof IBend) {
        return selection.isSelected(((IBend) item).getOwner());
      } else if (item instanceof ILabel) {
        return selection.isSelected(((ILabel) item).getOwner());
      }
      return false;
    }, target);

    // notify all commands that the copy graph has changed
    // this will cause the commands to re-evaluate their enabled states
    ICommand.invalidateRequerySuggested();

    // ensure that all copied elements are visible
    copyGraphControl.fitGraphBounds();
  }

  /**
   * Initializes the graph and the input modes.
   */
  public void initialize() {
    initializeDefaults();

    // Initializes the graph
    initializeGraph();

    // Enable undoability
    originalGraphControl.getGraph().setUndoEngineEnabled(true);

    // Initializes the input modes
    createEditorInputMode(originalGraphControl);
    createViewerInputMode(copyGraphControl);

    // Includes the "resources/help.html"
    WebViewUtils.initHelp(helpView, this);
  }

  /**
   * Initializes the defaults for the styles.
   */
  private void initializeDefaults() {
    IGraph graph = originalGraphControl.getGraph();
    // Sets the default style for nodes
    ShinyPlateNodeStyle defaultNodeStyle = new ShinyPlateNodeStyle();
    defaultNodeStyle.setPaint(Color.DARKORANGE);
    graph.getNodeDefaults().setStyle(defaultNodeStyle);
    // Sets the default node size explicitly to 40x40
    graph.getNodeDefaults().setSize(new SizeD(40, 40));

    // Specifies the default style for group nodes.

    // PanelNodeStyle is a style especially suited to group nodes
    // Creates a panel with a light blue background
    PanelNodeStyle panelNodeStyle = new PanelNodeStyle();
    Color groupNodeColor = Color.rgb(214, 229, 248);
    panelNodeStyle.setColor(groupNodeColor);
    // Specifies insets that provide space for a label at the top
    panelNodeStyle.setInsets(new InsetsD(23, 5, 5, 5));
    panelNodeStyle.setLabelInsetsColor(groupNodeColor);
    graph.getGroupNodeDefaults().setStyle(panelNodeStyle);

    // Sets a label style with right-aligned text
    DefaultLabelStyle defaultLabelStyle = new DefaultLabelStyle();
    defaultLabelStyle.setTextAlignment(TextAlignment.RIGHT);
    graph.getGroupNodeDefaults().getLabelDefaults().setStyle(defaultLabelStyle);

    // Places the label at the top inside of the panel.
    // For PanelNodeStyle, InteriorStretchLabelModel is usually the most appropriate label model
    graph.getGroupNodeDefaults().getLabelDefaults().setLayoutParameter(InteriorStretchLabelModel.NORTH);
  }

  /**
   * Creates a sample graph and introduces all important graph elements present in yFiles.
   */
  private void initializeGraph() {
    IGraph graph = originalGraphControl.getGraph();

    // Creates some nodes with the default node size
    // The location is specified for the center
    INode node1 = graph.createNode(new PointD(110, 20));
    INode node2 = graph.createNode(new PointD(145, 95));
    INode node3 = graph.createNode(new PointD(75, 95));
    INode node4 = graph.createNode(new PointD(30, 175));
    INode node5 = graph.createNode(new PointD(100, 175));

    // Creates some edges between the nodes
    graph.createEdge(node1, node2);
    graph.createEdge(node1, node3);
    IEdge edge3 = graph.createEdge(node3, node4);
    graph.createEdge(node3, node5);
    IEdge edge5 = graph.createEdge(node1, node5);

    // Changes the target position from the edge from node1 to node 5, so that there is no slope anymore
    IPort sp5 = edge5.getSourcePort();
    IPort tp5 = edge5.getTargetPort();
    double sp5x = sp5.getLocation().getX();
    double tp5y = tp5.getLocation().getY();
    // Creates a new FreeNodePortLocationModel
    FreeNodePortLocationModel model = new FreeNodePortLocationModel();
    // Create the new parameter for target port
    IPortLocationModelParameter parameter = model.createParameter(tp5.getOwner(), new PointD(sp5x, tp5y));
    // Set the new port location parameter
    graph.setPortLocationParameter(tp5, parameter);

    // Creates some bends
    IPort sp3 = edge3.getSourcePort();
    IPort tp3 = edge3.getTargetPort();
    double sp3x = sp3.getLocation().getX();
    double tp3x = tp3.getLocation().getX();
    double cy = (sp3.getLocation().getY() + tp3.getLocation().getY()) * 0.5;
    graph.addBend(edge3, new PointD(sp3x, cy));
    graph.addBend(edge3, new PointD(tp3x, cy));


    // Creates a group node
    INode groupNode = graph.createGroupNode();
    // Assigns some child nodes
    graph.setParent(node1, groupNode);
    graph.setParent(node2, groupNode);
    graph.setParent(node3, groupNode);
    // Ensures the group node bounds encompass the bounds of its child nodes
    graph.adjustGroupNodeLayout(groupNode);
    // Creates a label
    graph.addLabel(groupNode, "Group 1");
  }

  /**
   * Creates an input mode for interactive graph editing.
   */
  private void createEditorInputMode( GraphControl control ) {
    GraphEditorInputMode mode = new GraphEditorInputMode();

    // Enables grouping operations such as grouping selected nodes moving nodes
    // into group nodes
    mode.setGroupingOperationsAllowed(true);

    // Binds "new" command to its default shortcut CTRL+N on Windows and Linux
    // and COMMAND+N on Mac OS
    bindNewCommand(mode.getKeyboardInputMode());

    control.setInputMode(mode);
  }

  /**
   * Creates an input mode that prevents interactive editing except for
   * deleting all graph elements.
   */
  private void createViewerInputMode( GraphControl control ) {
    GraphViewerInputMode mode = new GraphViewerInputMode();

    // Binds "new" command to its default shortcut CTRL+N on Windows and Linux
    // and COMMAND+N on Mac OS
    bindNewCommand(mode.getKeyboardInputMode());

    control.setInputMode(mode);
  }

  /**
   * Assigns an effect to the new command if it is executed in the context
   * of the given keyboard input mode.
   */
  private void bindNewCommand( KeyboardInputMode mode ) {
    mode.addCommandBinding(
            ICommand.NEW,
            ( command, parameter, source ) -> {
              ((GraphControl) source).getGraph().clear();
              ICommand.invalidateRequerySuggested();
              return true;
            },
            ( command, parameter, source ) ->
                    ((GraphControl) source).getGraph().getNodes().size() != 0);
  }

  /**
   * Adjusts the views on the first start of the demo.
   */
  @Override
  protected void onLoaded() {
    originalGraphControl.fitGraphBounds();
    copyGraphControl.fitGraphBounds();
  }

  public static void main( String[] args ) {
    launch(args);
  }
}
