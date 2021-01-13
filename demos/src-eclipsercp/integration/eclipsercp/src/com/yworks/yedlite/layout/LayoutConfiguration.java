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
package com.yworks.yedlite.layout;

import com.yworks.yfiles.algorithms.AbortHandler;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.labelmodels.FreeEdgeLabelModel;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.LabelAngleReferences;
import com.yworks.yfiles.layout.LayoutEventArgs;
import com.yworks.yfiles.layout.LayoutExecutor;
import com.yworks.yfiles.layout.LabelPlacements;
import com.yworks.yfiles.layout.LayoutGraphAdapter;
import com.yworks.yfiles.layout.MinimumNodeSizeStage;
import com.yworks.yfiles.layout.PreferredPlacementDescriptor;
import com.yworks.yfiles.utils.IEventHandler;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.ISelectionModel;

import java.time.Duration;
import java.util.function.Function;

/**
 * Abstract base class for configurations that can be displayed in an option editor.
 * <p>
 * Subclasses at least have to implement the method {@link #createConfiguredLayout(GraphControl)} so the {@link #apply(GraphControl, IEventHandler)}
 * method can be called to run the returned layout algorithm and apply the layout result to the graph in the passed
 * {@link GraphControl}.
 * </p>
 */
public abstract class LayoutConfiguration {
  /**
   * A guard to prevent running multiple layout calculations at the same time.
   */
  private boolean layoutRunning;

  private AbortHandler abortHandler;

  /**
   * An optional {@code AbortHandler} that is used by the {@link LayoutExecutor} during
   * {@link #apply(GraphControl, IEventHandler)}.
   * @return The AbortHandler.
   * @see #setAbortHandler(AbortHandler)
   */
  public final AbortHandler getAbortHandler() {
    return this.abortHandler;
  }

  /**
   * An optional {@code AbortHandler} that is used by the {@link LayoutExecutor} during
   * {@link #apply(GraphControl, IEventHandler)}.
   * @param value The AbortHandler to set.
   * @see #getAbortHandler()
   */
  public final void setAbortHandler( AbortHandler value ) {
    this.abortHandler = value;
  }

  /**
   * Applies this configuration to the given {@link GraphControl}.
   * <p>
   * This is the main method of this class. Typically, it calls {@link #createConfiguredLayout(GraphControl)} to create and
   * configure a layout and the graph's {@link IGraph#getMapperRegistry() MapperRegistry} if necessary. Then, it calls {@link #runLayout(GraphControl, ILayoutAlgorithm, IEventHandler)}
   * to calculate a layout and apply it to the given {@code graphControl}. Finally, that method invokes {@link #postProcess(GraphControl)}
   * after the calculation.
   * </p>
   * @param graphControl The {@code GraphControl} to apply the configuration on.
   * @param doneHandler A callback that is called after the configuration is applied. Can be {@code null}
   */
  public void apply(GraphControl graphControl, IEventHandler doneHandler ) {
    ILayoutAlgorithm layout;
    try {
      layout = createConfiguredLayout(graphControl);
      if (layout == null) {
        return;
      }
      layout = new MinimumNodeSizeStage(layout);
    }catch (RuntimeException e) {
      if (doneHandler != null) {
        doneHandler.onEvent(this, new LayoutEventArgs(e));
      } else {
        throw new RuntimeException("An exception happened during the configuration of the layout: " + e.getMessage());
      }
      return;
    }
    runLayout(graphControl, layout, doneHandler);
  }

  /**
   * Creates and configures a layout algorithm and the graph's {@link IGraph#getMapperRegistry() MapperRegistry} if
   * necessary.
   * @param graphControl The {@link GraphControl} to apply the configuration on.
   * @return The configured layout.
   */
  protected abstract ILayoutAlgorithm createConfiguredLayout( GraphControl graphControl );

  /**
   * Performs an animated layout on the {@link GraphControl}'s graph with the given {@link ILayoutAlgorithm}.
   * <p>
   * This method should be called from {@link #apply(GraphControl, IEventHandler)} to do the actual layout calculation. In
   * addition to calling morphLayout, this method sets a guard that prevents running multiple layout calculations at the same
   * time and runs the method {@link #postProcess(GraphControl)}. Finally, it invokes the given , if it is not
   * {@code null}.
   * </p>
   */
  protected final void runLayout( final GraphControl graphControl, ILayoutAlgorithm layout, final IEventHandler doneHandler ) {
    if (layoutRunning) {
      return;
    }

    //configure the LayoutExecutor and start it
    AbortingLayoutExecutor layoutExecutor = new AbortingLayoutExecutor(graphControl, layout, getAbortHandler());
    layoutExecutor.setDuration(Duration.ofSeconds(1));
    layoutExecutor.setContentRectUpdatingEnabled(true);
    layoutExecutor.setViewportAnimationEnabled(true);
    layoutExecutor.addLayoutFinishedListener(new IEventHandler<LayoutEventArgs>() {
      @Override
      public void onEvent(Object source, LayoutEventArgs args) {
        layoutRunning = false;
        postProcess(graphControl);
        doneHandler.onEvent(source, args);
      }
    });
    layoutExecutor.start();
  }

  /**
   * Called by {@link #runLayout(GraphControl, ILayoutAlgorithm, IEventHandler)} after the layout animation is done. This
   * method is typically overridden to remove mappers from the mapper registry of the graph.
   */
  protected void postProcess( GraphControl graphControl ) {
  }

  /**
   * Adds a mapper with a {@link PreferredPlacementDescriptor} that matches the given settings to the mapper registry of the
   * given graph. In addition, sets the label model of all edge labels to free since that model can realizes any label
   * placement calculated by a layout algorithm.
   */
  public static final void addPreferredPlacementDescriptor( IGraph graph, EnumLabelPlacementAlongEdge placeAlongEdge, EnumLabelPlacementSideOfEdge sideOfEdge, EnumLabelPlacementOrientation orientation, double distanceToEdge ) {

    FreeEdgeLabelModel model = new FreeEdgeLabelModel();
    PreferredPlacementDescriptor descriptor = createPreferredPlacementDescriptor(placeAlongEdge, sideOfEdge, orientation, distanceToEdge);

    graph.getMapperRegistry().createConstantMapper(ILabel.class, PreferredPlacementDescriptor.class, LayoutGraphAdapter.EDGE_LABEL_LAYOUT_PREFERRED_PLACEMENT_DESCRIPTOR_DPKEY, descriptor);

    for (ILabel label : graph.getEdgeLabels()) {
      graph.setLabelLayoutParameter(label, model.findBestParameter(label, model, label.getLayout()));
    }
  }

  /**
   * Creates a new {@link PreferredPlacementDescriptor} that matches the given settings.
   */
  public static final PreferredPlacementDescriptor createPreferredPlacementDescriptor( EnumLabelPlacementAlongEdge placeAlongEdge, EnumLabelPlacementSideOfEdge sideOfEdge, EnumLabelPlacementOrientation orientation, double distanceToEdge ) {
    PreferredPlacementDescriptor descriptor = new PreferredPlacementDescriptor();

    switch (sideOfEdge) {
      case ANYWHERE:
        descriptor.setSideOfEdge(LabelPlacements.ANYWHERE);
        break;
      case ON_EDGE:
        descriptor.setSideOfEdge(LabelPlacements.ON_EDGE);
        break;
      case LEFT:
        descriptor.setSideOfEdge(LabelPlacements.LEFT_OF_EDGE);
        break;
      case RIGHT:
        descriptor.setSideOfEdge(LabelPlacements.RIGHT_OF_EDGE);
        break;
      case LEFT_OR_RIGHT:
        descriptor.setSideOfEdge(LabelPlacements.LEFT_OF_EDGE.or(LabelPlacements.RIGHT_OF_EDGE));
        break;
    }

    switch (placeAlongEdge) {
      case ANYWHERE:
        descriptor.setPlaceAlongEdge(LabelPlacements.ANYWHERE);
        break;
      case AT_SOURCE_PORT:
        descriptor.setPlaceAlongEdge(LabelPlacements.AT_SOURCE_PORT);
        break;
      case AT_TARGET_PORT:
        descriptor.setPlaceAlongEdge(LabelPlacements.AT_TARGET_PORT);
        break;
      case AT_SOURCE:
        descriptor.setPlaceAlongEdge(LabelPlacements.AT_SOURCE);
        break;
      case AT_TARGET:
        descriptor.setPlaceAlongEdge(LabelPlacements.AT_TARGET);
        break;
      case CENTERED:
        descriptor.setPlaceAlongEdge(LabelPlacements.AT_CENTER);
        break;
    }

    switch (orientation) {
      case PARALLEL:
        descriptor.setAngle(0.0d);
        descriptor.setAngleReference(LabelAngleReferences.RELATIVE_TO_EDGE_FLOW);
        break;
      case ORTHOGONAL:
        descriptor.setAngle(Math.PI / 2);
        descriptor.setAngleReference(LabelAngleReferences.RELATIVE_TO_EDGE_FLOW);
        break;
      case HORIZONTAL:
        descriptor.setAngle(0.0d);
        descriptor.setAngleReference(LabelAngleReferences.ABSOLUTE);
        break;
      case VERTICAL:
        descriptor.setAngle(90.0d);
        descriptor.setAngleReference(LabelAngleReferences.ABSOLUTE);
        break;
    }

    descriptor.setDistanceToEdge(distanceToEdge);
    return descriptor;
  }

  /**
   * Specifies constants for the preferred placement along an edge used by layout configurations.
   */
  public enum EnumLabelPlacementAlongEdge {
    ANYWHERE(0),

    AT_SOURCE_PORT(1),

    AT_TARGET_PORT(2),

    AT_SOURCE(3),

    AT_TARGET(4),

    CENTERED(5);

    private final int value;

    private EnumLabelPlacementAlongEdge( final int value ) {
      this.value = value;
    }

    public int value() {
      return this.value;
    }

    public static final EnumLabelPlacementAlongEdge fromOrdinal( int ordinal ) {
      for (EnumLabelPlacementAlongEdge current : values()) {
        if (ordinal == current.value) return current;
      }
      throw new IllegalArgumentException("Enum has no value " + ordinal);
    }
  }

  /**
   * Specifies constants for the preferred placement at a side of an edge used by layout configurations.
   */
  public enum EnumLabelPlacementSideOfEdge {
    ANYWHERE(0),

    ON_EDGE(1),

    LEFT(2),

    RIGHT(3),

    LEFT_OR_RIGHT(4);

    private final int value;

    private EnumLabelPlacementSideOfEdge( final int value ) {
      this.value = value;
    }

    public int value() {
      return this.value;
    }

    public static final EnumLabelPlacementSideOfEdge fromOrdinal( int ordinal ) {
      for (EnumLabelPlacementSideOfEdge current : values()) {
        if (ordinal == current.value) return current;
      }
      throw new IllegalArgumentException("Enum has no value " + ordinal);
    }
  }

  /**
   * Specifies constants for the orientation of an edge label used by layout configurations.
   */
  public enum EnumLabelPlacementOrientation {
    PARALLEL(0),

    ORTHOGONAL(1),

    HORIZONTAL(2),

    VERTICAL(3);

    private final int value;

    private EnumLabelPlacementOrientation( final int value ) {
      this.value = value;
    }

    public int value() {
      return this.value;
    }

    public static final EnumLabelPlacementOrientation fromOrdinal( int ordinal ) {
      for (EnumLabelPlacementOrientation current : values()) {
        if (ordinal == current.value) return current;
      }
      throw new IllegalArgumentException("Enum has no value " + ordinal);
    }
  }

  /**
   * A {@link LayoutExecutor} using a given {@link #getAbortHandler() AbortHandler}.
   */
  private static class AbortingLayoutExecutor extends LayoutExecutor {
    private final AbortHandler abortHandler;

    public AbortingLayoutExecutor( GraphControl control, ILayoutAlgorithm layout, AbortHandler abortHandler ) {
      super(control, layout);
      this.abortHandler = abortHandler;
    }

    @Override
    protected AbortHandler createAbortHandler() {
      return abortHandler != null ? abortHandler : super.createAbortHandler();
    }
  }

  public static class IsEdgeSelected implements Function<IEdge, Boolean> {
    final ISelectionModel<IModelItem> selectionModel;

    public IsEdgeSelected( GraphControl component ) {
      this.selectionModel = component.getSelection();
    }

    public Boolean apply( IEdge edge ) {
      return selectionModel.isSelected(edge);
    }
  }

  public static class IsNodeSelected implements Function<INode, Boolean> {
    final ISelectionModel<IModelItem> selectionModel;

    public IsNodeSelected( GraphControl component ) {
      this.selectionModel = component.getSelection();
    }

    public Boolean apply( INode node ) {
      return selectionModel.isSelected(node);
    }
  }
}
