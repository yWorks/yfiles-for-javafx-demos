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
package viewer.backgroundimage;


import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.ICanvasObject;
import com.yworks.yfiles.view.ICanvasObjectDescriptor;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import toolkit.DemoApplication;
import toolkit.WebViewUtils;

import java.io.IOException;
import javafx.scene.image.Image;
import javafx.scene.web.WebView;

/**
 * Shows how to add background visualizations to a graph control.
 */
public class BackgroundImageDemo extends DemoApplication {
  public WebView helpView;
  public GraphControl graphControl;

  /**
   * The canvas object that stores the background visualization.
   * This can be used to remove the background image.
   */
  private ICanvasObject background;

  /**
   * The background image to display in the demo's graph control.
   */
  private Image backgroundImage;

  /**
   * Initializes the demo.
   */
  public void initialize() {
    //Configure the interaction.
    initializeInputMode();

    //Create the sample graph
    loadGraph();

    //Display the image in the background.
    loadImage();
    displayImage();

    graphControl.getGraph().setUndoEngineEnabled(true);
    WebViewUtils.initHelp(helpView, this);
  }

  /**
   * Removes the current background and adds the image.
   */
  public void displayImage() {
    if (background != null) {
      background.remove();
    }
    background = graphControl.getBackgroundGroup().addChild(
            new ImageVisualCreator(backgroundImage),
            ICanvasObjectDescriptor.ALWAYS_DIRTY_INSTANCE
    );
  }

  /**
   * Removes the current background and adds the rectangle.
   */
  public void displayRectangle() {
    if (background != null) {
      background.remove();
    }
    background = graphControl.getBackgroundGroup().addChild(
            new RectangleVisualCreator(),
            ICanvasObjectDescriptor.ALWAYS_DIRTY_INSTANCE
    );
  }

  /**
   * Centers the graph.
   */
  @Override
  protected void onLoaded() {
    graphControl.fitGraphBounds();
  }

  /**
   * Loads the initial sample graph.
   */
  private void loadGraph() {
    try {
      graphControl.importFromGraphML(getClass().getResource("resources/sample.graphml"));
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }

  /**
   * Loads the image to be displayed by this creator's visuals.
   */
  private void loadImage() {
    backgroundImage = new Image(getClass().getResource("resources/ylogo.png").toExternalForm());
  }

  /**
   * Initializes the input mode for the demo.
   */
  private void initializeInputMode() {
    GraphEditorInputMode geim = new GraphEditorInputMode();
    geim.setGroupingOperationsAllowed(true);
    graphControl.setInputMode(geim);
  }

  public static void main(String[] args) {
    launch(args);
  }
}