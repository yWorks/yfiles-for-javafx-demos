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
package toolkit;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;

/**
 * Base class for demos that makes the use of {@link Application} easier
 * by implementing the main and start method and collecting the data
 * to do this from abstract getter methods that clients have to implement.
 * <p>
 *   The getter for event handler methods are implemented by default to return null.
 * </p>
 */
public abstract class DemoApplication extends Application {
  private static final String DEFAULT_CSS_FILE_NAME = "toolkit/resources/DemoApplication.css";

  @Override
  public void start(Stage primaryStage) throws IOException {
    // enable error reporting for errors that occur while running the demo
    Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> reportError(primaryStage, throwable));

    // catch exceptions that occur while loading the demo
    try {
      // really start the demo
      startImpl(primaryStage);
    } catch (Exception e) {
      // Prevent JavaFX error handling by not re-throwing the throwable. Bring up the error reporter instead.
      reportError(primaryStage, e);
      e.printStackTrace();
    }
  }

  private void startImpl(final Stage primaryStage) throws IOException {
    setup();
    Parent sceneRoot = getSceneRoot();

    Scene scene = new Scene(sceneRoot, getSceneWidth(), getSceneHeight());

    String cssFileName = getCssFileName();
    if (cssFileName != null) {
      scene.getStylesheets().add(cssFileName);
    }

    if (getOnCloseRequestHandler() != null){
      primaryStage.setOnCloseRequest(getOnCloseRequestHandler());
    }
    if (getOnHiddenHandler() != null){
      primaryStage.setOnHidden(getOnHiddenHandler());
    }
    if (getOnHidingHandler() != null){
      primaryStage.setOnHiding(getOnHidingHandler());
    }
    if (getOnShowingHandler() != null){
      primaryStage.setOnShowing(getOnShowingHandler());
    }
    if (getOnShownHandler() != null){
      primaryStage.setOnShown(getOnShownHandler());
    }

    primaryStage.getIcons().addAll(
        new Image("resources/logo_16.png"),
        new Image("resources/logo_24.png"),
        new Image("resources/logo_32.png"),
        new Image("resources/logo_48.png"),
        new Image("resources/logo_64.png"),
        new Image("resources/logo_128.png"));
    primaryStage.setTitle(getTitle());
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  /**
   * Creates an error reporter and invokes it by calling Platform#runLater.
   */
  private void reportError(final Stage primaryStage, final Throwable e) {
    Platform.runLater(createErrorReporter(primaryStage, e));
  }

  /**
   * Creates the error reporter that shows the dialog and sends the error report.
   */
  private ErrorReporter createErrorReporter(final Stage primaryStage, final Throwable e) {
    return new ErrorReporter(primaryStage.getScene(), e, getTitle());
  }

  /**
   * Specifies the width of the scene. Used to construct the scene in the {@link #start(javafx.stage.Stage)} method.
   */
  public int getSceneWidth() {
    return 1365;
  }

  /**
   * Specifies the height of the scene. Used to construct the scene in the {@link #start(javafx.stage.Stage)} method.
   */
  public int getSceneHeight() {
    return 768;
  }

  /**
   * Method that is called prior to building the demo scene root.
   */
  public void setup(){}

  /**
   * Method that is called after the Window is shown.
   * @see Stage#getOnShown()
   */
  protected void onLoaded(){}

  /**
   * Returns the name of the CSS file used for the demo application.
   */
  public String getCssFileName() {
    return DEFAULT_CSS_FILE_NAME;
  }

  /**
   * Returns the root for the scene graph of the demo.
   * <p>
   * The {@link #getFxmlResource() FXML resource} is {@link FXMLLoader#load() loaded} using this class as
   * {@link FXMLLoader#setController(Object) controller}.
   * </p>
   * The result of this will be passed to {@link Scene#setRoot(javafx.scene.Parent)}.
   */
  public Parent getSceneRoot() throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(getFxmlResource());
    fxmlLoader.setController(this);
    return (Parent) fxmlLoader.load();
  }

  /**
   * Returns the {@link URL} to the FXML file of the demo.
   * <p>
   * This resource is used by {@link #getSceneRoot()}.
   * </p>
   */
  protected URL getFxmlResource() {
    String simpleName = getClass().getSimpleName();
    String rowName = simpleName.substring(0, simpleName.length() - "Demo".length());
    return getClass().getResource(rowName + ".fxml");
  }

  /**
   * Returns the title of the stage for this demo.
   */
  public String getTitle() {
    StringBuilder demoName = new StringBuilder();
    String[] nameParts = getClass().getSimpleName().split("(?=[A-Z])");
    for (String part : nameParts) {
      demoName.append(part).append(" ");
    }
    demoName.append(" - yFiles for JavaFX");
    return demoName.toString();
  }

  /**
   * Returns a handler that is passed to the stages {@link Stage#setOnShown(javafx.event.EventHandler)} method.
   * The default implementation calls {@link #onLoaded()}.
   */
  public EventHandler<WindowEvent> getOnShownHandler() {
    return windowEvent -> this.onLoaded();
  }

  /**
   * Returns a handler that is passed to the stages {@link Stage#setOnShowing(javafx.event.EventHandler)} method.
   * The default implementation in this class returns null.
   */
  public EventHandler<WindowEvent> getOnShowingHandler(){
    return null;
  }

  /**
   * Returns a handler that is passed to the stages {@link Stage#setOnHidden(javafx.event.EventHandler)} method.
   * The default implementation in this class returns null.
   */
  public EventHandler<WindowEvent> getOnHiddenHandler(){
    return null;
  }

  /**
   * Returns a handler that is passed to the stages {@link Stage#setOnHiding(javafx.event.EventHandler)} method.
   * The default implementation in this class returns null.
   */
  public EventHandler<WindowEvent> getOnHidingHandler(){
    return null;
  }

  /**
   * Returns a handler that is passed to the stages {@link Stage#setOnCloseRequest(javafx.event.EventHandler)} method.
   * The default implementation in this class returns null.
   */
  public EventHandler<WindowEvent> getOnCloseRequestHandler(){
    return null;
  }

}
