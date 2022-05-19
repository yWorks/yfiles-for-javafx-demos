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
package style.richtextlabelstyle;

import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.GraphControl;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import toolkit.DemoApplication;
import toolkit.WebViewUtils;

import java.util.Optional;

/**
 * Demonstrates a custom label style that turns Markdown formatted text into JavaFX rich text.
 */
public class RichTextLabelStyleDemo extends DemoApplication {

  public GraphControl graphControl;
  public WebView help;

  /**
   * Initializes the controller. This is called when the FXMLLoader instantiates the scene graph.
   * At the time this method is called, all nodes in the scene graph are available. Most importantly,
   * the GraphControl instance is initialized.
   */
  public void initialize() {
    // setup the help text on the right side.
    WebViewUtils.initHelp(help, this);

    // initialize the graph
    initializeGraph();

    // initialize the input mode
    initializeInputModes();
  }

  /**
   * Called when the stage is shown and the {@link GraphControl} is already resized to its preferred size.
   * The graph is moved to the center of the <code>GraphControl</code>.
   */
  public void onLoaded() {
    graphControl.fitGraphBounds();

    if (!MarkdownConverter.isDefaultFontValid()) {
      // MarkdownConverter uses different instances of one font family to format the text italic or bold.
      // If the used default font doesn't support this formatting, a warning is displayed.
      Alert alert = new Alert(Alert.AlertType.WARNING);
      ButtonType okButton = new ButtonType("Ok");
      alert.getButtonTypes().setAll(okButton);
      alert.setHeaderText("The DEFAULT_FONT used in MarkdownConverter doesn't support italic or bold text.");
      alert.setContentText("Change the font family used in the static initializer of MarkdownConverter to a font " +
          "on your system supporting italic and bold formatting.");
      alert.setTitle("Default font invalid");
      alert.initOwner(graphControl.getScene().getWindow());
      alert.initModality(Modality.WINDOW_MODAL);
      Optional<ButtonType> result = alert.showAndWait();
    }
  }

  /**
   * Sets the default styles for nodes and edges and also the default label style, which is the RichTextLabelStyle.
   */
  private void initializeGraph() {
    IGraph graph = graphControl.getGraph();

    // use shiny plates as default node style
    ShinyPlateNodeStyle nodeStyle = new ShinyPlateNodeStyle();
    nodeStyle.setPaint(Color.ALICEBLUE);
    graph.getNodeDefaults().setStyle(nodeStyle);
    PolylineEdgeStyle edgeStyle = new PolylineEdgeStyle();
    edgeStyle.setPen(new Pen(Color.rgb(0, 0, 0, 0.3)));
    graph.getEdgeDefaults().setStyle(edgeStyle);

    // use our RichTextLabelStyle as default for labels
    graph.getNodeDefaults().getLabelDefaults().setStyle(new RichTextLabelStyle());
    graph.getEdgeDefaults().getLabelDefaults().setStyle(new RichTextLabelStyle());

    // load a sample graph that shows some Markdown formatted labels
    createSampleGraph();
  }

  /**
   * Creates the initial sample graph with formatted labels.
   */
  private void createSampleGraph() {
    IGraph graph = graphControl.getGraph();

    INode n1 = graph.createNode(new PointD(50, 50));
    INode n2 = graph.createNode(new PointD(650, 50));
    INode n3 = graph.createNode(new PointD(50, 650));

    IEdge e1 = graph.createEdge(n1, n2);
    IEdge e2 = graph.createEdge(n1, n3);
    IEdge e3 = graph.createEdge(n2, n3);

    graph.addLabel(n1, "This is a normal single line text \nwithout any formatting.");
    graph.addLabel(n2, "This is a multi-line text using   \ntwo or more spaces at line end for a new line.");
    graph.addLabel(n3, "This is a text with a paragraph.\n\nThis is done by an empty line.");
    graph.addLabel(e1, "A text with an *italic* _formatting_.");
    graph.addLabel(e2, "A text with a **bold** __formatting__.");
    graph.addLabel(e3, "A text with an ***italic bold*** ___formatting___.");
  }

  /**
   * Creates a {@link com.yworks.yfiles.view.input.GraphEditorInputMode} and sets it as the InputMode of the GraphControl.
   */
  private void initializeInputModes() {
    GraphEditorInputMode mode = new GraphEditorInputMode();
    graphControl.setInputMode(mode);
  }

  public static void main(String[] args) {
    launch(args);
  }
}
