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
package deploy.obfuscation;

import com.yworks.yfiles.utils.Obfuscation;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLAnchorElement;

import java.io.IOException;
import java.net.URL;

/**
 * <p>
 *   Obfuscation demo that contains a small sample program and shows how to obfuscate a yFiles for JavaFX application.
 *   For an explanation of the annotation mechanism that is used here, have a look at the yGuard manual at
 *
 *                     https://www.yworks.com/products/yguard/yguard_ant_howto.html#annotation
 *
 *   You can also read this anytime in the demo in the help panel on the right.
 *
 *   Information regarding the build and obfuscation process itself, yGuard and the used mechanisms in this demo
 *   can be found in the description in the build.xml file.
 * </p>
 *
 * This application class is excluded from obfuscation by declaring it as the main class.
 */
public class ObfuscationDemo extends Application {

  /**
   * This field is assigned in fxml and therefore needs to be named like this in runtime.
   * Thus, prevent obfuscation.
   */
  @Obfuscation( exclude = true )
  public GraphControl graphControl;

  /**
   * This field is assigned in fxml and therefore needs to be named like this in runtime.
   * Thus, prevent obfuscation.
   */
  @Obfuscation ( exclude = true )
  public WebView helpView;

  /**
   * This field is assigned in fxml and therefore needs to be named like this in runtime.
   * Thus, prevent obfuscation.
   */
  @Obfuscation ( exclude = true )
  public WebView yguardDocView;


  /**
   * A field that is only used in code and thus can be obfuscated. This field exists solely for demonstration purposes.
   */
  private GraphEditorInputMode inputMode;

  /**
   * A field that is only used in code and thus can be obfuscated. This field exists solely for demonstration purposes.
   */
  private ShinyPlateNodeStyle nodeStyle;

  /**
   * This method is called via reflection after loading the fxml file by the {@link javafx.fxml.FXMLLoader}.
   * Thus, this method needs to be kept.
   */
  @Obfuscation ( exclude = true )
  public void initialize(){
    // setup the help text on the right side.
    initHelp(helpView, this);
    yguardDocView.getEngine().load("http://www.yworks.com/products/yguard/yguard_ant_howto.html");

    initializeInputModes();
  }

  /**
   * This method is called after showing the stage and invokes the initialization of the graph
   * It is called only from code that is not public and can thus be obfuscated.
   */
  public void onLoaded(){
    loadSampleGraph();
  }

  /**
   * Loads the sample graph. This is a private method and thus can be obfuscated.
   */
  private void loadSampleGraph() {
    try {
      graphControl.importFromGraphML(getClass().getResource("resources/example.graphml"));
      graphControl.fitContent();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Initializes the {@link com.yworks.yfiles.view.input.GraphEditorInputMode}. This is a private method and thus can be obfuscated.
   */
  private void initializeInputModes() {
    inputMode = new GraphEditorInputMode();
    graphControl.setInputMode(inputMode);
    nodeStyle = new ShinyPlateNodeStyle();
    nodeStyle.setPaint(Color.ORANGE);
    graphControl.getGraph().getNodeDefaults().setStyle(nodeStyle);
  }

  /**
   * This method is assigned in fxml to a button and thus needs to be kept.
   */
  @Obfuscation (exclude = true )
  public void increaseZoom() {
    graphControl.setZoom(graphControl.getZoom() * 1.25);
  }

  /**
   * This method is assigned in fxml to a button and thus needs to be kept.
   */
  @Obfuscation (exclude = true )
  public void decreaseZoom() {
    graphControl.setZoom(graphControl.getZoom() * 0.8);
  }

  /**
   * This method is assigned in fxml to a button and thus needs to be kept.
   */
  @Obfuscation (exclude = true )
  public void fitGraphBounds() {
    graphControl.fitContent();
  }

  /**
   * This method is assigned in fxml to a button and thus needs to be kept.
   */
  @Obfuscation (exclude = true )
  public void updateContentRect(ActionEvent actionEvent) {
    graphControl.updateContentRect();
  }

  @Override
  public void start(Stage primaryStage) throws IOException {

    final FXMLLoader loader = new FXMLLoader(getClass().getResource("Obfuscation.fxml"));
    loader.setController(this);

    BorderPane pane = (BorderPane) loader.load();

    primaryStage.setOnShown(windowEvent -> onLoaded());
    primaryStage.setTitle("Obfuscation Demo - yFiles for JavaFX");

    Scene scene = new Scene(pane, 1365, 768);
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public static void initHelp( WebView view, Application application ) {
    WebEngine engine = view.getEngine();
    URL helpFile = application.getClass().getResource("help.html");
    if (helpFile == null) {
      helpFile = application.getClass().getResource("resources/help.html");
    }
    if (helpFile != null) {
      engine.load(helpFile.toExternalForm());
    } else {
      engine.loadContent("Could not resolve help text. Please ensure that your build process or IDE adds the " +
              "help.html file to the class path.", "text/plain");
    }
    engine.getLoadWorker().stateProperty().addListener(
            new RegisterHyperlinkHandler(engine, application.getHostServices()));
  }

  private static final class RegisterHyperlinkHandler
          implements ChangeListener<Worker.State> {
    final WebEngine engine;
    final HostServices services;

    RegisterHyperlinkHandler( WebEngine engine, HostServices services ) {
      this.engine = engine;
      this.services = services;
    }

    @Override
    public void changed(
            ObservableValue<? extends Worker.State> observable,
            Worker.State oldValue,
            Worker.State newValue
    ) {
      if (Worker.State.SUCCEEDED == newValue) {
        Document document = engine.getDocument();
        NodeList anchors = document.getElementsByTagName("a");
        HyperlinkHandler handler = new HyperlinkHandler(services);
        for (int i = 0, n = anchors.getLength(); i < n; ++i) {
          ((EventTarget) anchors.item(i)).addEventListener("click", handler, false);
        }
      }
    }
  }

  private static final class HyperlinkHandler implements EventListener {
    final HostServices services;

    HyperlinkHandler( HostServices services ) {
      this.services = services;
    }

    @Override
    public void handleEvent( Event e ) {
      String href = ((HTMLAnchorElement) e.getCurrentTarget()).getHref();
      services.showDocument(href);

      e.preventDefault();
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
}
