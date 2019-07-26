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
package viewer.printing;

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.MutableRectangle;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.utils.ObservableCollection;
import com.yworks.yfiles.utils.IEventHandler;
import com.yworks.yfiles.view.CanvasPrinter;
import com.yworks.yfiles.view.DashStyle;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.ICanvasObject;
import com.yworks.yfiles.view.IVisualTemplate;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.PrintingEvent;
import com.yworks.yfiles.view.RectangleIndicatorInstaller;
import com.yworks.yfiles.view.VisualGroup;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.HandleInputMode;
import com.yworks.yfiles.view.input.HandlePositions;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.MoveInputMode;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.input.RectangleReshapeHandleProvider;
import javafx.print.PageLayout;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.scene.web.WebView;
import toolkit.DemoApplication;
import toolkit.WebViewUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * This demo shows how to use the JavaFX printing API to print the contents of a yFiles GraphControl.
 * It utilizes the class {@link com.yworks.yfiles.view.CanvasPrinter} which leverages the
 * printing API of JavaFX to comfortably print the contents of a yFiles CanvasControl.
 * <p>
 *   The main logic for printing can be found in {@link #print()}.
 * </p>
 */
public class PrintingDemo extends DemoApplication {
  public static final int PAGE_HEADER_INSET = 50;
  public static final int PAGE_FOOTER_INSET = 50;
  public static double SCREEN_TO_PRINT_DPI = 72d / 96d;

  public GraphControl graphControl;
  public WebView helpView;

  // the controller for the general settings
  public SettingsController settingsController;

  // the controller of the settings for the ContentMargins property of the CanvasPrinter
  public ContentMarginsInputFieldController contentMarginsInputFieldController;

  // the controller of the settings for the page layout of the PrinterJob
  public PageLayoutInputFieldController pageLayoutInputFieldController;

  // the rectangle which defines which area to print of the graph in world coordinates
  private MutableRectangle printRectangle;
  private PrinterJob job = PrinterJob.createPrinterJob();

  // the canvas object that represents the print rectangle in the GraphComponent.l
  private ICanvasObject printRectangleCanvasObject;
  // the InputModes that controls the handles that can be used to resize the print rectangle
  private HandleInputMode handleInputMode;
  // the InputModes that controls the dragging of the print rectangle
  private MoveInputMode moveInputMode;
  // the CanvasPrinter class that will print the GraphControl
  private CanvasPrinter canvasPrinter;

  // the header and footer for each page that is printed if the PrintDecorations checkbox is checked.
  private PageHeader pageHeader = new PageHeader(30, "yWorks GmbH", "yFiles for JavaFX Printing Demo",
      LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.uuuu")));
  private PageFooter pageFooter = new PageFooter(30);

  /**
   * Initializes the controller. This is called when the FXMLLoader instantiates the scene graph.
   * At the time this method is called, all nodes in the scene graph are available. Most importantly,
   * the GraphControl instance is initialized.
   * <p>
   *   In this demo however, the main initialization step is done in {@link #onLoaded()}.
   * </p>
   */
  public void initialize() {
    // setup the help text on the left side.
    WebViewUtils.initHelp(helpView, this);

    canvasPrinter = new CanvasPrinter(graphControl);

    // set content margins: these margins are used to place the header and footer in between the page margins and the content to print
    contentMarginsInputFieldController.setContentMargins(InsetsD.fromLTRB(0, PAGE_HEADER_INSET, 0, PAGE_FOOTER_INSET));

    // bind the properties of the CanvasPrinter to the various input control values and link the other ui parts to this one
    canvasPrinter.scalingUpToFitPageEnabledProperty().bind(settingsController.scaleUpToFitPageSelectedProperty());
    canvasPrinter.scalingDownToFitPageEnabledProperty().bind(settingsController.scaleDownToFitPageSelectedProperty());
    canvasPrinter.centeringContentEnabledProperty().bind(settingsController.centerContentSelectedProperty());
    canvasPrinter.scaleProperty().bind(settingsController.scaleProperty());
    canvasPrinter.contentMarginsProperty().bind(contentMarginsInputFieldController.contentMarginsProperty());
    canvasPrinter.pageMarkPrintingEnabledProperty().bind(settingsController.printPageMarksSelectedProperty());
    pageLayoutInputFieldController.setJob(job);
    contentMarginsInputFieldController.printDecorationsProperty().bind(settingsController.printDecorationsSelectedProperty());


    // prepare the transformations for the header and footer of the printed pages
    Scale screenDPIScaling = Transform.scale(SCREEN_TO_PRINT_DPI, SCREEN_TO_PRINT_DPI);
    pageHeader.getTransforms().add(screenDPIScaling);
    pageFooter.getTransforms().add(screenDPIScaling);
    pageFooter.getTransforms().add(new Translate());

    // decorate the pages that are printed with a header and footer
    IEventHandler<PrintingEvent> addDecorationsEventHandler = (source, args) -> {
      Node printedNode = args.getNode();
      if (printedNode instanceof VisualGroup) {
        VisualGroup container = (VisualGroup) printedNode;
        PageLayout pageLayout = job.getJobSettings().getPageLayout();

        double printableWidth = pageLayout.getPrintableWidth();
        double printableHeight = pageLayout.getPrintableHeight();

        if (!container.getChildren().contains(pageHeader)) {
          pageHeader.setWidth(printableWidth / SCREEN_TO_PRINT_DPI);
          container.add(pageHeader);
        }
        if (!container.getChildren().contains(pageFooter)) {
          pageFooter.setWidth(printableWidth / SCREEN_TO_PRINT_DPI);

          // translate footer to the bottom
          Translate translate = (Translate) pageFooter.getTransforms().get(1);
          translate.setY((printableHeight - 40) / SCREEN_TO_PRINT_DPI);
          container.add(pageFooter);
        }

        // update footer page number
        pageFooter.setPageNumber((args.getRow() * args.getPrintInfo().getColumnCount()) + args.getColumn() + 1);
      }
    };

    // add the handler that adds the decorations to the CanvasPrinter as listener for the printing event that occurs right before printing each page by default.
    canvasPrinter.addPrintingListener(addDecorationsEventHandler);

    // dependent on the checkbox in the SettingsController, add or remove the decorator for the printed pages.
    settingsController.printDecorationsSelectedProperty().addListener((observable, wasSelected, isSelected) -> {
      if (isSelected) {
        // add the page decorator to the printing process
        canvasPrinter.addPrintingListener(addDecorationsEventHandler);
      } else {
        // remove the page decorator from the printing process
        canvasPrinter.removePrintingListener(addDecorationsEventHandler);
      }
    });

    // Remove the printRectangle from the GraphControl and uninstall the InputModes for it
    // when the check box was unchecked.
    // Otherwise install the rectangle and add the InputModes.
    settingsController.usePrintRectangleSelectedProperty().addListener((observable, wasSelected, isSelected) -> {
      if (isSelected) {
        addPrintRectangle();
      } else {
        removePrintRectangle();
      }
    });
  }

  /**
   * Initializes the input modes and build a sample graph.
   */
  public void onLoaded() {
    // build the input modes and wire up the printing rectangle
    initializeInputModes();
    // construct a sample graph
    initializeGraph();
    // initialize the values of the page layout in the view on the left
    pageLayoutInputFieldController.updateTextFields();
  }

  /**
   * Initializes a {@link com.yworks.yfiles.view.input.GraphEditorInputMode} and the rectangle that indicates the region to
   * print.
   */
  private void initializeInputModes() {
    // create a GraphEditorInputMode instance
    GraphEditorInputMode editMode = new GraphEditorInputMode();
    // and install the edit mode into the canvas.
    graphControl.setInputMode(editMode);

    // create the model for the print rectangle
    printRectangle = new MutableRectangle(0, 0, 100, 100);

    // we want to display a rectangle that works just like the selection indicators and also has handles.
    // for this, we can use an implementation of the ISelectionIndicatorInstaller interface.
    addPrintRectangle();


    // add command binding for 'print'
    editMode.getKeyboardInputMode().addCommandBinding(ICommand.PRINT,
        (command, param, sender) -> print(), (command, param, sender) -> job != null);
    ICommand.invalidateRequerySuggested();
  }

  /**
   * Installs the visual representation of the print rectangle onto the GraphControl and wires up the input modes to us it.
   */
  private void addPrintRectangle() {
    // build an installer using the bounds of our rectangle
    RectangleIndicatorInstaller rectangleIndicatorInstaller = new RectangleIndicatorInstaller(printRectangle);
    rectangleIndicatorInstaller.setTemplate(new PrintRectangleTemplate());
    rectangleIndicatorInstaller.addCanvasObject(graphControl.getCanvasContext(), graphControl.getInputModeGroup(), printRectangle);
    // now add our rectangle to the input mode group which will be rendered on top of the graph items
    printRectangleCanvasObject = rectangleIndicatorInstaller.addCanvasObject(graphControl.getCanvasContext(), graphControl.getInputModeGroup(),
        printRectangle);

    // add view modes that handle the resizing and movement of the print rectangle
    addInputModesForRectangle();
  }

  /**
   * Removes the visual representation of the print rectangle from the GraphControl and removes
   * the InputModes from the GraphControl that dealt with the print rectangle as well.
   */
  private void removePrintRectangle() {
    if (printRectangleCanvasObject != null) {
      // to remove the print rectangle that we previously installed using the RectangularSelectionIndicatorInstaller,
      printRectangleCanvasObject.remove();
      // we also need to remove the input modes for the print rectangle from the GraphEditorInputMode that we set up earlier.
      removeInputModesForRectangle();
      // we delete the canvas object, when the print rectangle is enabled again we build another one.
      printRectangleCanvasObject = null;
    }
  }

  /**
   * Adds view modes that handle the resizing and movement of the print rectangle.
   */
  private void addInputModesForRectangle() {
    GraphEditorInputMode inputMode = (GraphEditorInputMode) graphControl.getInputMode();
    if (handleInputMode == null && moveInputMode == null) {
      // create handles for interactively resizing the print rectangle
      RectangleReshapeHandleProvider rectangleHandles = new RectangleReshapeHandleProvider(printRectangle);
      rectangleHandles.setMinimumSize(new SizeD(10, 10));
      // create a input mode that renders the handles and deals with mouse gestures to drag the handles
      handleInputMode = new HandleInputMode();
      // specify certain handles the input mode should manage
      IInputModeContext inputModeContext = handleInputMode.getInputModeContext();
      handleInputMode.setHandles(new ObservableCollection<>(
          Arrays.asList(
              rectangleHandles.getHandle(inputModeContext, HandlePositions.NORTH_EAST),
              rectangleHandles.getHandle(inputModeContext, HandlePositions.NORTH_WEST),
              rectangleHandles.getHandle(inputModeContext, HandlePositions.SOUTH_EAST),
              rectangleHandles.getHandle(inputModeContext, HandlePositions.SOUTH_WEST))));

      // create a mode that allows for dragging the print rectangle at the sides
      moveInputMode = new MoveInputMode();
      // assign the print rectangle as moveable so that it will be repositioned during dragging
      moveInputMode.setPositionHandler(new PrintRectanglePositionHandler(printRectangle));
      // define the hit test that determines where the user can begin dragging the rectangle
      moveInputMode.setHitTestable((context, location) -> {
        GeneralPath path = new GeneralPath(5);
        path.appendRectangle(printRectangle, false);
        return path.pathContains(location, context.getHitTestRadius());
      });
    }

    // add the HandleInputMode to the graph editor mode
    handleInputMode.setPriority(1);
    inputMode.add(handleInputMode);
    // Add the MoveInputMode to the graph editor mode:
    // The MoveInputMode that controls the node dragging behavior have a higher priority than the
    // MoveInputMode that is responsible for moving the rectangle around.
    moveInputMode.setPriority(inputMode.getMoveInputMode().getPriority() + 1);
    inputMode.add(moveInputMode);
  }

  /**
   * Removes the InputModes from the GraphControl that deal with the print rectangle.
   */
  private void removeInputModesForRectangle() {
    GraphEditorInputMode inputMode = (GraphEditorInputMode) graphControl.getInputMode();
    inputMode.remove(handleInputMode);
    inputMode.remove(moveInputMode);
  }

  /**
   * Initializes a simple sample graph and makes the printing rectangle
   * enclose a part of it.
   */
  private void initializeGraph() {
    IGraph graph = graphControl.getGraph();
    // initialize defaults
    ShinyPlateNodeStyle nodeStyle = new ShinyPlateNodeStyle();
    nodeStyle.setPaint(Color.DARKORANGE);
    graph.getNodeDefaults().setStyle(nodeStyle);
    PolylineEdgeStyle edgeStyle = new PolylineEdgeStyle();
    edgeStyle.setTargetArrow(IArrow.DEFAULT);
    graph.getEdgeDefaults().setStyle(edgeStyle);

    // create sample graph
    graph.addLabel(graph.createNode(new PointD(30, 30)), "Node");
    INode node = graph.createNode(new PointD(90, 30));
    graph.createEdge(node, graph.createNode(new PointD(90, 90)));

    // fit the graph bounds now to enclose the current graph
    graphControl.fitGraphBounds();
    // initially set the printing rectangle to enclose part of the graph's contents
    printRectangle.reshape(graphControl.getContentRect());
    // create graph elements that are outside the current content rectangle (and printing rectangle)
    graph.createEdge(node, graph.createNode(new PointD(200, 30)));

    // now fit the graph bounds again to make the whole graph visible
    graphControl.fitGraphBounds();
    // the printing rectangle still encloses the same part of the graph as before
  }

  /**
   * Brings up the printing dialog and continues to print with the current settings, if chosen.
   */
  public boolean print(){
    // apply the settings to the CanvasPrinter
    if (settingsController.usePrintRectangleSelectedProperty().get()) {
      canvasPrinter.setPrintRectangle(printRectangle.toRectD());
    } else {
      graphControl.fitGraphBounds();
      canvasPrinter.setPrintRectangle(graphControl.getContentRect());
    }

    boolean success = canvasPrinter.print(job, true);
    if (success) {
      job.endJob();
      // reuse the page layout for the new printer job
      PageLayout oldPageLayout = job.getJobSettings().getPageLayout();
      job = PrinterJob.createPrinterJob();
      job.getJobSettings().setPageLayout(oldPageLayout);
    }
    return true;
  }

  public static void main(String[] args) {
    launch(args);
  }

  /**
   * A custom template that creates a rectangle with a red dotted stroke for the print rectangle.
   */
  private static class PrintRectangleTemplate implements IVisualTemplate {
    private static final Pen PEN = new Pen(Color.CRIMSON, 2);

    public PrintRectangleTemplate() {
      PEN.setDashStyle(DashStyle.getDot());
    }

    @Override
    public Node createVisual(final IRenderContext context, final RectD bounds, final Object dataObject) {
      // We return a custom JavaFX rectangle which can be resized, so we don't need to create a new rectangle for every rendering step.
      Rectangle rectangle = new Rectangle(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
      PEN.styleShape(rectangle);
      rectangle.setFill(Color.TRANSPARENT);
      rectangle.setMouseTransparent(true);
      return rectangle;
    }

    @Override
    public Node updateVisual(final IRenderContext context, final Node oldVisual, final RectD bounds, final Object dataObject) {
      if (oldVisual instanceof Rectangle) {
        Rectangle rectangle = (Rectangle) oldVisual;
        rectangle.setWidth(bounds.getWidth());
        rectangle.setHeight(bounds.getHeight());
        rectangle.setX(bounds.getX());
        rectangle.setY(bounds.getY());
        return rectangle;
      }
      return createVisual(context, bounds, dataObject);
    }
  }
}
