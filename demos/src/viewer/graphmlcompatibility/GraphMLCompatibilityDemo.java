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
package viewer.graphmlcompatibility;

import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.input.GraphViewerInputMode;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.KeyboardInputMode;
import complete.bpmn.view.BpmnNodeStyle;
import complete.orgchart.Employee;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.web.WebView;
import toolkit.DemoApplication;
import toolkit.WebViewUtils;

import java.io.IOException;
import java.net.URL;

/**
 * Demonstrates how to enable backwards READ compatibility for GraphML.
 * <p>
 * Class {@link CompatibilitySupport} may be used to configure a given
 * {@link GraphMLIOHandler} instance so that it can read GraphML files written
 * in yFiles for JavaFX version 2.0.x or 3.0.x.
 * You can enable backwards compatibility with this single line:
 * </p><pre>
 * CompatibilitySupport.configureIOHandler(ioh);
 * </pre><p>
 * The resources directory contains a number of sample files from previous distributions.
 * </p>
 * <p>
 * <b>Known limitations:</b>
 * </p><p>
 * yFiles for JavaFX 2.0.x GraphML files that use a template style cannot be
 * read without also adjusting the corresponding FXML files because the
 * template controls (e.g. <code>com.yworks.yfiles.drawing.NodeTemplate</code>)
 * now reside in package <code>com.yworks.yfiles.graph.styles</code>.
 * </p>
 */
public class GraphMLCompatibilityDemo extends DemoApplication {
  /**
   * Opens legacy GraphML files.
   * In this context, <em>legacy</em> means yFiles for JavaFX 3.0.x.
   */
  public static final ICommand OPEN_LEGACY = ICommand.createCommand("OpenLegacy");

  public GraphControl graphControl;
  public ComboBox<String> graphChooserBox;
  public Button previousButton;
  public Button nextButton;
  public WebView webView;

  /**
   * Initializes file operations for this demo.
   */
  public void initialize() {
    graphChooserBox.getItems().addAll(
            "computer-network",
            "movies",
            "family-tree",
            "hierarchy",
            "nesting",
            "social-network",
            "uml-diagram",
            "large-tree");

    WebViewUtils.initHelp(webView, this);

    // the convenience commands for reading graphs from GrapML and writing
    // graphs to GraphML have to be explicitly enabled
    graphControl.setFileIOEnabled(true);

    // add key and command bindings for opening legacy GraphML files
    // note:
    // a GraphMLIOHandler instance configured for reading legacy GraphML
    // files cannot be used for writing yFiles for JavaFX 3.1.x GraphML files.
    // since a graph component's GraphMLIOHandler is used for both reading
    // and writing GraphML files, a legacy reader cannot be set as *the*
    // graph component's GraphMLIOHandler. Thus a custom command binding for
    // reading legacy GraphML files is used.
    GraphViewerInputMode gvim = new GraphViewerInputMode();
    KeyboardInputMode kim = gvim.getKeyboardInputMode();
    kim.addKeyBinding(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN), OPEN_LEGACY);
    kim.addCommandBinding(OPEN_LEGACY, this::executeOpen, this::canExecuteOpen);

    graphControl.setInputMode(gvim);

    ICommand.invalidateRequerySuggested();
  }

  /**
   * Called when stage is shown because it needs the scene to be ready.
   */
  public void onLoaded() {
    graphChooserBox.getSelectionModel().select(0);
  }

  /**
   * Reads a sample graph from a graphml file according currently selected entry in the graph chooser box.
   */
  private void readSampleGraph() {
    readSampleGraph(graphChooserBox.getSelectionModel().getSelectedItem());
  }

  private void readSampleGraph( String sample ) {
    // first derive the file name
    URL graphML = getClass().getResource("resources/" + sample + ".graphml");

    // then load the graph
    try {
      IGraph graph = graphControl.getGraph();
      graph.clear();

      newConfiguredReader().read(graph, graphML);

      // when done - fit the bounds
      graphControl.fitGraphBounds();

      // the commands CanExecute state might have changed - suggest a re-query.
      ICommand.invalidateRequerySuggested();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

  /**
   * Creates a new {@link GraphMLIOHandler} instance configured for reading
   * legacy GraphML files.
   */
  private static GraphMLIOHandler newConfiguredReader() {
    GraphMLIOHandler reader = new GraphMLIOHandler();

    // IMPORTANT:
    // configure the reader for reading yFiles for JavaFX 3.0.x GraphML files
    CompatibilitySupport.configureIOHandler(reader);

    // CompatibilitySupport will enable reading library styles
    // support for custom styles (or types) such as demo styles has to be added
    // separately like e.g. support for the BPMNEditorDemo and the OrgChartDemo
    // styles and types
    reader.addXamlNamespaceMapping("http://www.yworks.com/yfiles-for-javafx/demos/OrgChartEditor/1.0", Employee.class);
    reader.addXamlNamespaceMapping("http://www.yworks.com/xml/yfiles-bpmn/1.0", BpmnNodeStyle.class);

    return reader;
  }

  /**
   * Updates the current graph and the buttons in the toolbar when the selected graph changes.
   */
  public void graphChanged() {
    readSampleGraph();
    updateButtons();
  }

  /**
   * Selects the previous graph in the list and updates the buttons in the toolbar.
   */
  public void previousButtonClicked() {
    final SingleSelectionModel<String> selectionModel = graphChooserBox.getSelectionModel();
    selectionModel.select(selectionModel.getSelectedIndex() - 1);
    updateButtons();
  }

  /**
   * Selects the next graph in the list and updates the buttons in the toolbar.
   */
  public void nextButtonClicked() {
    final SingleSelectionModel<String> selectionModel = graphChooserBox.getSelectionModel();
    selectionModel.select(selectionModel.getSelectedIndex() + 1);
    updateButtons();
  }

  /**
   * Updates the toolbar buttons. Buttons that would leave the range of samples are disabled.
   */
  private void updateButtons() {
    final SingleSelectionModel<String> selectionModel = graphChooserBox.getSelectionModel();
    nextButton.setDisable(selectionModel.getSelectedIndex() == graphChooserBox.getItems().size() - 1);
    previousButton.setDisable(selectionModel.getSelectedIndex() == 0);
  }

  /**
   * Reads legacy GraphML files.
   * @param command the command that triggered this operation. Should be
   * {@link #OPEN_LEGACY}.
   * @param parameter an optional parameter for the operation. Ignored for
   * opening GraphML files.
   * @param source the graph component that will display the read graph.
   */
  private boolean executeOpen( ICommand command, Object parameter, Object source ) {
    if (source instanceof GraphControl) {
      // since a graph component's GraphMLIOHandler is used for both reading
      // and writing GraphML files, a legacy reader is set only temporarily
      // such that subsequent write operations use a default GraphMLIOHandler
      GraphControl graphControl = (GraphControl) source;
      GraphMLIOHandler oldHandler = graphControl.getGraphMLIOHandler();
      try {
        graphControl.setGraphMLIOHandler(newConfiguredReader());

        ICommand.OPEN.execute(parameter, source);
      } finally {
        graphControl.setGraphMLIOHandler(oldHandler);
      }
      return true;
    } else {
      return false;
    }
  }

  /**
   * Determines if the "open legacy GraphML files" command may be executed.
   * @param command the command for which the check is performed. Should be
   * {@link #OPEN_LEGACY}.  
   * @param parameter an optional parameter for the operation. Ignored for
   * opening GraphML files.
   * @param source the graph component that will display the read graph if the
   * command is executed.
   */
  private boolean canExecuteOpen( ICommand command, Object parameter, Object source ) {
    return ICommand.OPEN.canExecute(parameter, source);
  }

  @Override
  public String getTitle() {
    return "GraphML Compatibility Demo - yFiles for JavaFX";
  }

  public static void main(String[] args) {
    launch(args);
  }
}
