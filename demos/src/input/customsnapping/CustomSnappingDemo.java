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
package input.customsnapping;

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.PathType;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.GraphDecorator;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.ILabelOwner;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.labelmodels.FreeNodeLabelModel;
import com.yworks.yfiles.graph.labelmodels.SmartEdgeLabelModel;
import com.yworks.yfiles.graph.styles.IShapeGeometry;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.GridInfo;
import com.yworks.yfiles.view.GridStyle;
import com.yworks.yfiles.view.GridVisualCreator;
import com.yworks.yfiles.view.ICanvasObjectDescriptor;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.input.CollectGraphSnapLinesEventArgs;
import com.yworks.yfiles.view.input.CollectSnapResultsEventArgs;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.GraphSnapContext;
import com.yworks.yfiles.view.input.GridConstraintProvider;
import com.yworks.yfiles.view.input.GridSnapTypes;
import com.yworks.yfiles.view.input.ISnapLineProvider;
import com.yworks.yfiles.view.input.LabelSnapContext;
import com.yworks.yfiles.view.input.NodeSnapResultProvider;
import com.yworks.yfiles.view.input.OrthogonalSnapLine;
import com.yworks.yfiles.view.input.SnapLine;
import com.yworks.yfiles.view.input.SnapLineOrientation;
import com.yworks.yfiles.view.input.SnapLineSnapTypes;
import com.yworks.yfiles.view.input.SnapPolicy;
import com.yworks.yfiles.view.input.SnapResult;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.web.WebView;
import toolkit.DemoApplication;
import toolkit.WebViewUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * This demo shows how to customize <code>SnapLine</code> behaviour.
 */
public class CustomSnappingDemo extends DemoApplication {
  public GraphControl graphControl;
  public WebView help;

  // the snap contexts used in this demo
  private GraphSnapContext graphSnapContext;
  private LabelSnapContext labelSnapContext;

  // the list of line visuals of the free, movable snap lines
  private List<Line> freeSnapLines;

  /**
   * Returns a list of the free {@link javafx.scene.shape.Line additional snap lines} used in this demo.
   */
  private List<Line> getFreeSnapLines() {
    return freeSnapLines;
  }

  /**
   * Initializes the controller. This is called when the FXMLLoader instantiates the scene graph.
   * At the time this method is called, all nodes in the scene graph are available. Most importantly,
   * the GraphControl instance is initialized.
   */
  public void initialize() {
    // setup the help text on the right side.
    WebViewUtils.initHelp(help, this);

    // add the custom snap line provider and snap result provider using the graph decorator
    initializeGraphDecorator();

    // initialize the snap context and register our collectAdditionalLines method as collect snap line listener
    initializeSnapContext();

    // initialize the grid the nodes can be snapped to
    initializeGrid();

    // initialize two free snap lines that are also visualized in the GraphCanvasComponent
    freeSnapLines = new ArrayList<>();
    addFreeSnapLine(0, -70, 500, -70);
    addFreeSnapLine(-230, -50, -230, 400);

    // Initialize the input mode for this demo
    GraphEditorInputMode inputMode = new GraphEditorInputMode();
    inputMode.setSnapContext(graphSnapContext);
    inputMode.setLabelSnapContext(labelSnapContext);

    // add an input mode that allows to move the custom snap lines
    LineMoveInputMode lineVisualMoveInputMode = new LineMoveInputMode(freeSnapLines);
    lineVisualMoveInputMode.setPriority(-50);
    inputMode.add(lineVisualMoveInputMode);
    graphControl.setInputMode(inputMode);

    // initialize the graph default so new nodes and label have the same look and feel as those in the example graph
    initializeGraphDefaults();
  }

  /**
   * Decorates the ModelItem lookup for custom snapping behaviour.
   */
  private void initializeGraphDecorator() {
    GraphDecorator graphDecorator = graphControl.getGraph().getDecorator();

    // add additional snap lines for orthogonal labels of nodes
    graphDecorator.getNodeDecorator().getSnapLineProviderDecorator().setImplementationWrapper(
        (item, baseImplementation) -> new OrthogonalLabelSnapLineProviderWrapper(baseImplementation));

    // add additional snap lines for orthogonal labels of edges
    graphDecorator.getEdgeDecorator().getSnapLineProviderDecorator().setImplementationWrapper(
        (item, baseImplementation) -> new OrthogonalLabelSnapLineProviderWrapper(baseImplementation));

    // for nodes using IShapeNodeStyle use a customized grid snapping behaviour based on their shape
    graphDecorator.getNodeDecorator().getNodeSnapResultProviderDecorator().setImplementation(
        node -> node.getStyle() instanceof ShapeNodeStyle, new ShapeBasedGridNodeSnapResultProvider());
  }

  /**
   * Creates and configures the snap context. Registers additional snap lines to the snap context.
   */
  private void initializeSnapContext() {
    graphSnapContext = new GraphSnapContext();
    graphSnapContext.setGridSnapType(GridSnapTypes.ALL);
    graphSnapContext.setSnapDistance(10);

    labelSnapContext = new LabelSnapContext();
    labelSnapContext.setSnapDistance(10);
    labelSnapContext.setCollectingInitialLocationSnapLinesEnabled(false);

    // use the free additional snap lines
    graphSnapContext.addCollectSnapLinesListener((source, args) -> collectAdditionalSnapLines(args::addAdditionalSnapLine));
    labelSnapContext.addCollectSnapLinesListener((source, args) -> collectAdditionalSnapLines(args::addSnapLine));
  }

  /**
   * Creates and adds {@link SnapLine}s for the free {@link javafx.scene.shape.Line additional snap lines}.
   * While the {@link javafx.scene.shape.Line additional snap lines} are used to visualize and represent free snap lines, according
   * {@link OrthogonalSnapLine}s have to be added to the snapping mechanism to describe their snapping
   * behaviour.
   *
   * @param snapLineConsumer the consumer that adds the created snap lines to the collection event
   */
  private void collectAdditionalSnapLines(Consumer<OrthogonalSnapLine> snapLineConsumer) {
    for (Line line : getFreeSnapLines()) {
      PointD center = getCenter(line);

      if (line.getStartX() == line.getEndX()) { // it's vertical
        snapLineConsumer.accept(new OrthogonalSnapLine(SnapLineOrientation.VERTICAL, SnapLineSnapTypes.LEFT,
            SnapLine.SNAP_LINE_FIXED_LINE_KEY, center, line.getStartY(), line.getEndY(), line, 50));
        snapLineConsumer.accept(new OrthogonalSnapLine(SnapLineOrientation.VERTICAL, SnapLineSnapTypes.RIGHT,
            SnapLine.SNAP_LINE_FIXED_LINE_KEY, center, line.getStartY(), line.getEndY(), line, 50));

      } else if (line.getStartY() == line.getEndY()) { // it's horizontal
        snapLineConsumer.accept(new OrthogonalSnapLine(SnapLineOrientation.HORIZONTAL, SnapLineSnapTypes.TOP,
            SnapLine.SNAP_LINE_FIXED_LINE_KEY, center, line.getStartX(), line.getEndX(), line, 50));
        snapLineConsumer.accept(new OrthogonalSnapLine(SnapLineOrientation.HORIZONTAL, SnapLineSnapTypes.BOTTOM,
            SnapLine.SNAP_LINE_FIXED_LINE_KEY, center, line.getStartX(), line.getEndX(), line, 50));
      }
    }
  }

  /**
   * Returns the center point of the given line.
   */
  private static PointD getCenter(Line line) {
    return new PointD((line.getEndX() + line.getStartX()) / 2, (line.getEndY() + line.getStartY()) / 2);
  }

  /**
   * Adds grid to the GraphControl and grid constraint provider to the snap context.
   */
  private void initializeGrid() {
    GridInfo gridInfo = new GridInfo(200);
    GridVisualCreator grid = new GridVisualCreator(gridInfo);
    grid.setGridStyle(GridStyle.CROSSES);
    graphControl.getBackgroundGroup().addChild(grid, ICanvasObjectDescriptor.ALWAYS_DIRTY_INSTANCE);

    graphSnapContext.setNodeGridConstraintProvider(new GridConstraintProvider<>(gridInfo));
    graphSnapContext.setBendGridConstraintProvider(new GridConstraintProvider<>(gridInfo));
  }

  /**
   * Adds a new {@link javafx.scene.shape.Line additional snap line} to the {@link GraphControl} that spans between
   * <code>(fromX, fromY)</code> and <code>(toX, toY)</code>.
   * @param fromX the x-coordinate of the start location of the snap line
   * @param fromY the y-coordinate of the start location of the snap line
   * @param toX   the x-coordinate of the end location of the snap line
   * @param toY   the y-coordinate of the end location of the snap line
   */
  private void addFreeSnapLine(double fromX, double fromY, double toX, double toY) {
    // create line and add it to background group
    Line line = new Line(fromX, fromY, toX, toY);
    line.setStroke(Color.RED);
    graphControl.getBackgroundGroup().addChild(line, ICanvasObjectDescriptor.VISUAL);
    freeSnapLines.add(line);
  }

  /**
   * Initializes styles for the nodes and labels of the graph.
   */
  private void initializeGraphDefaults() {
    IGraph graph = graphControl.getGraph();
    DefaultLabelStyle labelStyle = new DefaultLabelStyle();
    labelStyle.setBackgroundPen(Pen.getBlack());

    graph.getNodeDefaults().getLabelDefaults().setStyle(labelStyle);
    graph.getNodeDefaults().getLabelDefaults().setLayoutParameter(FreeNodeLabelModel.INSTANCE.createParameter(
            new PointD(0.5, 0.0), new PointD(0, -10), new PointD(0.5, 1.0), PointD.ORIGIN, 0.0));

    graph.getEdgeDefaults().getLabelDefaults().setStyle(labelStyle);
    graph.getEdgeDefaults().getLabelDefaults().setLayoutParameter(
        new SmartEdgeLabelModel().createParameterFromSource(0, 0, 0.5));

    ShinyPlateNodeStyle style = new ShinyPlateNodeStyle();
    style.setPaint(Color.DARKORANGE);
    graph.getNodeDefaults().setStyle(style);
    graph.getNodeDefaults().setSize(new SizeD(50, 50));
  }

  /**
   * Called when the stage is shown and the {@link GraphControl} is already resized to its preferred size.
   */
  public void onLoaded() {
    initializeGraph();
  }

  /**
   * Loads a sample graph.
   */
  private void initializeGraph() {
    try {
      graphControl.importFromGraphML(getClass().getResource("resources/example.graphml"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Wraps a given {@link ISnapLineProvider} and adds additional {@link OrthogonalSnapLine}s for orthogonal labels of an {@link
   * IModelItem}. For each orthogonal label there are {@link OrthogonalSnapLine}s added for it's top,
   * bottom, left and right side.
   */
  private static class OrthogonalLabelSnapLineProviderWrapper implements ISnapLineProvider {
    private static final double EPS = 0.000001;

    private final ISnapLineProvider wrapped;

    /**
     * Creates a new instance that wraps the given <code>wrapped</code> snap line provider.
     *
     * @param wrapped the snap line provider that shall be wrapped
     */
    OrthogonalLabelSnapLineProviderWrapper(ISnapLineProvider wrapped) {
      this.wrapped = wrapped;
    }

    /**
     * Calls {@link ISnapLineProvider#addSnapLines(GraphSnapContext, CollectGraphSnapLinesEventArgs, IModelItem)}
     * of the wrapped provider and adds custom {@link SnapLine}s for the <code>item</code>.
     *
     * @param context The context which holds the settings for the snap lines.
     * @param args    The argument to use for adding snap lines.
     * @param item    The item to add snap lines for.
     */
    @Override
    public void addSnapLines(GraphSnapContext context, CollectGraphSnapLinesEventArgs args, IModelItem item) {
      wrapped.addSnapLines(context, args, item);

      // add snap lines for orthogonal labels
      if (item instanceof ILabelOwner) {
        ILabelOwner labeledItem = (ILabelOwner) item;
        for (ILabel label : labeledItem.getLabels()) {
          double upX = label.getLayout().getUpX();
          double upY = label.getLayout().getUpY();
          if (Math.abs(upX) < EPS || Math.abs(upY) < EPS) { // check if it's orthogonal
            // label is orthogonal
            RectD bounds = label.getLayout().getBounds();

            // add snap lines to the top, bottom, left and right border of the label
            PointD topCenter = PointD.add(bounds.getTopLeft(), new PointD(label.getLayout().getWidth() / 2, 0));
            OrthogonalSnapLine snapLine = new OrthogonalSnapLine(SnapLineOrientation.HORIZONTAL, SnapLineSnapTypes.BOTTOM,
                SnapLine.SNAP_LINE_FIXED_LINE_KEY, topCenter, bounds.getMinX() - 10, bounds.getMaxX() + 10, label, 100);
            args.addAdditionalSnapLine(snapLine);

            PointD bottomCenter = PointD.add(bounds.getBottomLeft(), new PointD(label.getLayout().getWidth() / 2, 0));
            snapLine = new OrthogonalSnapLine(SnapLineOrientation.HORIZONTAL, SnapLineSnapTypes.TOP,
                SnapLine.SNAP_LINE_FIXED_LINE_KEY, bottomCenter, bounds.getMinX() - 10, bounds.getMaxX() + 10, label,
                100);
            args.addAdditionalSnapLine(snapLine);

            PointD leftCenter = PointD.add(bounds.getTopLeft(), new PointD(0, label.getLayout().getHeight() / 2));
            snapLine = new OrthogonalSnapLine(SnapLineOrientation.VERTICAL, SnapLineSnapTypes.RIGHT,
                SnapLine.SNAP_LINE_FIXED_LINE_KEY, leftCenter, bounds.getMinY() - 10, bounds.getMaxY() + 10, label,
                100);
            args.addAdditionalSnapLine(snapLine);

            PointD rightCenter = PointD.add(bounds.getTopRight(), new PointD(0, label.getLayout().getHeight() / 2));
            snapLine = new OrthogonalSnapLine(SnapLineOrientation.VERTICAL, SnapLineSnapTypes.LEFT,
                SnapLine.SNAP_LINE_FIXED_LINE_KEY, rightCenter, bounds.getMinY() - 10, bounds.getMaxY() + 10, label,
                100);
            args.addAdditionalSnapLine(snapLine);
          }
        }
      }
    }
  }

  public static void main(String[] args) {
    launch(args);
  }

  /**
   * Customizes the grid snapping behaviour of {@link NodeSnapResultProvider} by providing {@link
   * SnapResult}s for each point of the node's shape path instead of the node's center.
   */
  private static class ShapeBasedGridNodeSnapResultProvider extends NodeSnapResultProvider {

    @Override
    public void collectGridSnapResults(GraphSnapContext context, CollectSnapResultsEventArgs args,
                                   RectD suggestedLayout, INode node) {
      // The node layout isn't updated, yet, so we have to calculate the delta
      // between the the new suggested layout and the current node.Layout
      PointD delta = PointD.subtract(suggestedLayout.getTopLeft(), node.getLayout().toRectD().getTopLeft());

      // get outline of the shape and iterate over its path point
      IShapeGeometry geometry = node.getStyle().getRenderer().getShapeGeometry(node, node.getStyle());
      GeneralPath outline = geometry.getOutline();
      if (outline != null) {
        GeneralPath.PathCursor cursor = outline.createCursor();
        while (cursor.moveNext()) {
          // ignore PathType.Close as we had the path point as first point
          // and cursor.CurrentEndPoint is always (0, 0) for PathType.Close
          if (cursor.getPathType() != PathType.CLOSE) {
            // adjust path point by the delta calculated above and add an according SnapResult
            PointD endPoint = PointD.add(cursor.getCurrentEndPoint(), delta);
            addGridSnapResultCore(context, args, endPoint, node, GridSnapTypes.GRID_POINTS, SnapPolicy.TO_NEAREST,
                SnapPolicy.TO_NEAREST);
          }
        }
      }
    }
  }
}
