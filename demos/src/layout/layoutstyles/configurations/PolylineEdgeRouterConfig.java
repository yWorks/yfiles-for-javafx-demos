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
package layout.layoutstyles.configurations;

import com.yworks.yfiles.graph.IBend;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graphml.DefaultValue;
import com.yworks.yfiles.layout.CurveConnectionStyle;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.labeling.GenericLabeling;
import com.yworks.yfiles.layout.LayoutData;
import com.yworks.yfiles.layout.router.MonotonicPathRestriction;
import com.yworks.yfiles.layout.router.polyline.BusDescriptor;
import com.yworks.yfiles.layout.router.polyline.EdgeLayoutDescriptor;
import com.yworks.yfiles.layout.router.polyline.EdgeRouter;
import com.yworks.yfiles.layout.router.polyline.EdgeRoutingStyle;
import com.yworks.yfiles.layout.router.polyline.Grid;
import com.yworks.yfiles.layout.router.polyline.PenaltySettings;
import com.yworks.yfiles.layout.router.polyline.PolylineEdgeRouterData;
import com.yworks.yfiles.layout.router.Scope;
import com.yworks.yfiles.layout.SequentialLayout;
import com.yworks.yfiles.utils.Obfuscation;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.IGraphSelection;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import toolkit.optionhandler.ComponentType;
import toolkit.optionhandler.ComponentTypes;
import toolkit.optionhandler.EnumValueAnnotation;
import toolkit.optionhandler.Label;
import toolkit.optionhandler.MinMax;
import toolkit.optionhandler.OptionGroupAnnotation;
import java.util.function.Predicate;

/**
 * Configuration options for the {@link EdgeRouter} algorithm.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
@Label("PolylineEdgeRouter")
public class PolylineEdgeRouterConfig extends LayoutConfiguration {
  /**
   * Setup default values for various configuration parameters.
   */
  public PolylineEdgeRouterConfig() {
    EdgeRouter router = new EdgeRouter();

    setScopeItem(router.getScope());
    setOptimizationStrategyItem(EnumStrategies.BALANCED);
    setMonotonicRestrictionItem(EnumMonotonyFlags.NONE);
    setReroutingEnabledItem(router.isReroutingEnabled());
    setMaximumDurationItem(30);

    EdgeLayoutDescriptor descriptor = router.getDefaultEdgeLayoutDescriptor();
    setMinimumEdgeToEdgeDistanceItem(descriptor.getMinimumEdgeToEdgeDistance());
    setMinimumNodeToEdgeDistanceItem(router.getMinimumNodeToEdgeDistance());
    setMinimumNodeCornerDistanceItem(descriptor.getMinimumNodeCornerDistance());
    setMinimumFirstSegmentLengthItem(descriptor.getMinimumFirstSegmentLength());
    setMinimumLastSegmentLengthItem(descriptor.getMinimumLastSegmentLength());

    Grid grid = router.getGrid();
    setGridEnabledItem(grid != null);
    setGridSpacingItem(grid != null ? grid.getSpacing() : 10);

    setEdgeRoutingStyleItem(EdgeRoutingStyle.ORTHOGONAL);
    setPreferredOctilinearSegmentLengthItem(router.getDefaultEdgeLayoutDescriptor().getPreferredOctilinearSegmentLength());
    setMaximumOctilinearSegmentRatioItem(router.getDefaultEdgeLayoutDescriptor().getMaximumOctilinearSegmentRatio());

    setConsideringNodeLabelsItem(router.isNodeLabelConsiderationEnabled());
    setConsideringEdgeLabelsItem(router.isEdgeLabelConsiderationEnabled());
    setEdgeLabelingEnabledItem(EnumEdgeLabeling.NONE);
    setLabelPlacementAlongEdgeItem(LayoutConfiguration.EnumLabelPlacementAlongEdge.CENTERED);
    setLabelPlacementSideOfEdgeItem(LayoutConfiguration.EnumLabelPlacementSideOfEdge.ON_EDGE);
    setLabelPlacementOrientationItem(LayoutConfiguration.EnumLabelPlacementOrientation.HORIZONTAL);
    setLabelPlacementDistanceItem(10);
  }

  @Override
  protected ILayoutAlgorithm createConfiguredLayout( GraphControl graphControl ) {
    EdgeRouter router = new EdgeRouter();

    router.setScope(getScopeItem());
    router.setMinimumNodeToEdgeDistance(getMinimumNodeToEdgeDistanceItem());

    if (isGridEnabledItem()) {
      router.setGrid(new Grid(0, 0, getGridSpacingItem()));
    } else {
      router.setGrid(null);
    }

    router.setNodeLabelConsiderationEnabled(isConsideringNodeLabelsItem());
    router.setEdgeLabelConsiderationEnabled(isConsideringEdgeLabelsItem());
    router.setReroutingEnabled(isReroutingEnabledItem());

    // Note that CreateConfiguredLayoutData replaces the settings on the DefaultEdgeLayoutDescriptor
    // by providing a custom one for each edge.
    router.getDefaultEdgeLayoutDescriptor().setRoutingStyle(getEdgeRoutingStyleItem());
    router.getDefaultEdgeLayoutDescriptor().setPreferredOctilinearSegmentLength(getPreferredOctilinearSegmentLengthItem());
    router.getDefaultEdgeLayoutDescriptor().setMaximumOctilinearSegmentRatio(getMaximumOctilinearSegmentRatioItem());
    router.getDefaultEdgeLayoutDescriptor().setSourceCurveConnectionStyle(getSourceConnectionStyleItem());
    router.getDefaultEdgeLayoutDescriptor().setTargetCurveConnectionStyle(getTargetConnectionStyleItem());

    router.setMaximumDuration(getMaximumDurationItem() * 1000);

    SequentialLayout layout = new SequentialLayout();
    layout.appendLayout(router);

    if (getEdgeLabelingEnabledItem() == EnumEdgeLabeling.NONE) {
      router.setIntegratedEdgeLabelingEnabled(false);
    } else if (getEdgeLabelingEnabledItem() == EnumEdgeLabeling.INTEGRATED) {
      router.setIntegratedEdgeLabelingEnabled(true);
    } else if (getEdgeLabelingEnabledItem() == EnumEdgeLabeling.GENERIC) {
      GenericLabeling genericLabeling = new GenericLabeling();
      genericLabeling.setEdgeLabelPlacementEnabled(true);
      genericLabeling.setNodeLabelPlacementEnabled(false);
      genericLabeling.setAmbiguityReductionEnabled(isReducingAmbiguityItem());
      layout.appendLayout(genericLabeling);
    }

    addPreferredPlacementDescriptor(graphControl.getGraph(), getLabelPlacementAlongEdgeItem(), getLabelPlacementSideOfEdgeItem(), getLabelPlacementOrientationItem(), getLabelPlacementDistanceItem());

    return layout;
  }

  @Override
  protected LayoutData createConfiguredLayoutData( GraphControl graphControl, ILayoutAlgorithm layout ) {
    PolylineEdgeRouterData layoutData = new PolylineEdgeRouterData();

    layoutData.setEdgeLayoutDescriptors(edge -> {
      EdgeLayoutDescriptor descriptor = new EdgeLayoutDescriptor();
      descriptor.setRoutingStyle(getEdgeRoutingStyleItem());
      descriptor.setPreferredOctilinearSegmentLength(getPreferredOctilinearSegmentLengthItem());
      descriptor.setMaximumOctilinearSegmentRatio(getMaximumOctilinearSegmentRatioItem());
      descriptor.setSourceCurveConnectionStyle(getSourceConnectionStyleItem());
      descriptor.setTargetCurveConnectionStyle(getTargetConnectionStyleItem());
      if (getOptimizationStrategyItem() == EnumStrategies.BALANCED) {
        descriptor.setPenaltySettings(PenaltySettings.OPTIMIZATION_BALANCED);
      } else if (getOptimizationStrategyItem() == EnumStrategies.MINIMIZE_BENDS) {
        descriptor.setPenaltySettings(PenaltySettings.OPTIMIZATION_EDGE_BENDS);
      } else if (getOptimizationStrategyItem() == EnumStrategies.MINIMIZE_EDGE_LENGTH) {
        descriptor.setPenaltySettings(PenaltySettings.OPTIMIZATION_EDGE_LENGTHS);
      } else {
        descriptor.setPenaltySettings(PenaltySettings.OPTIMIZATION_EDGE_CROSSINGS);
      }

      if (getMonotonicRestrictionItem() == EnumMonotonyFlags.HORIZONTAL) {
        descriptor.setMonotonicPathRestriction(MonotonicPathRestriction.HORIZONTAL);
      } else if (getMonotonicRestrictionItem() == EnumMonotonyFlags.VERTICAL) {
        descriptor.setMonotonicPathRestriction(MonotonicPathRestriction.VERTICAL);
      } else if (getMonotonicRestrictionItem() == EnumMonotonyFlags.BOTH) {
        descriptor.setMonotonicPathRestriction(MonotonicPathRestriction.BOTH);
      } else {
        descriptor.setMonotonicPathRestriction(MonotonicPathRestriction.NONE);
      }

      descriptor.setMinimumEdgeToEdgeDistance(getMinimumEdgeToEdgeDistanceItem());
      descriptor.setMinimumNodeCornerDistance(getMinimumNodeCornerDistanceItem());
      descriptor.setMinimumFirstSegmentLength(getMinimumFirstSegmentLengthItem());
      descriptor.setMinimumLastSegmentLength(getMinimumLastSegmentLengthItem());

      if (isUsingIntermediatePointsItem()) {
        ArrayList<Object> controlPoints = new ArrayList<>();
        for (IBend b : edge.getBends()) {
          controlPoints.add(b.getLocation().toYPoint());
        }
        descriptor.setIntermediateRoutingPoints(controlPoints);
      }

      return descriptor;
    });

    IGraphSelection selection = graphControl.getSelection();
    if (getScopeItem() == Scope.ROUTE_EDGES_AT_AFFECTED_NODES) {
      layoutData.setAffectedNodes(selection.getSelectedNodes());
    } else if (getScopeItem() == Scope.ROUTE_AFFECTED_EDGES) {
      layoutData.setAffectedEdges(selection.getSelectedEdges());
    } else {
      layoutData.setAffectedEdges(edge -> true);
      layoutData.setAffectedNodes(node -> true);
    }

    switch (getBusRoutingItem()) {
      case SINGLE_BUS:
        // All edges in a single bus
        layoutData.getBuses().add(new BusDescriptor()).setPredicate(new Predicate<IEdge>(){
          public boolean test( IEdge edge ) {
            return true;
          }
        });
        break;
      case BY_LABEL:
        HashMap<String, ArrayList<IEdge>> byLabel = new HashMap<String, ArrayList<IEdge>>();
        for (IEdge edge : graphControl.getGraph().getEdges()) {
          if (edge.getLabels().size() > 0) {
            String label = edge.getLabels().getItem(0).getText();
            ArrayList<IEdge> list = null;
            if (!byLabel.containsKey(label)) {
              list = new ArrayList<IEdge>();
              byLabel.put(label, list);
            }
            list.add(edge);
          }
        }
        for (ArrayList<IEdge> edges : byLabel.values()) {
          // Add a bus per label. Unlabeled edges don't get grouped into a bus
          layoutData.getBuses().add(new BusDescriptor()).setSource(edges);
        }
        break;
      case BY_COLOR:
        HashMap<String, ArrayList<IEdge>> byColor = new HashMap<String, ArrayList<IEdge>>();
        for (IEdge edge : graphControl.getGraph().getEdges()) {
          Paint brush = ((PolylineEdgeStyle)edge.getStyle()).getPen().getPaint();
          String brushKey = brush.toString();
          if (brushKey.compareTo(Color.BLACK.toString()) != 0) {
            ArrayList<IEdge> list = null;
            if (!byColor.containsKey(brushKey)) {
              list = new ArrayList<IEdge>();
              byColor.put(brushKey, list);
            }
            list.add(edge);
          }
        }
        for (ArrayList<IEdge> edges : byColor.values()) {
          // Add a bus per color. Black edges don't get grouped into a bus
          layoutData.getBuses().add(new BusDescriptor()).setSource(edges);
        }
        break;
    }

    return layoutData;
  }

  @Label("Description")
  @OptionGroupAnnotation(name = "RootGroup", position = 5)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object DescriptionGroup;

  @Label("General")
  @OptionGroupAnnotation(name = "RootGroup", position = 10)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object LayoutGroup;

  @Label("Minimum Distances")
  @OptionGroupAnnotation(name = "RootGroup", position = 20)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object DistancesGroup;

  @Label("Grid")
  @OptionGroupAnnotation(name = "RootGroup", position = 30)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object GridGroup;

  @Label("Routing Style")
  @OptionGroupAnnotation(name = "RootGroup", position = 40)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object RoutingStyleGroup;

  @Label("Labeling")
  @OptionGroupAnnotation(name = "RootGroup", position = 50)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object LabelingGroup;

  @Label("Node Settings")
  @OptionGroupAnnotation(name = "LabelingGroup", position = 10)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object NodePropertiesGroup;

  @Label("Edge Settings")
  @OptionGroupAnnotation(name = "LabelingGroup", position = 20)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object EdgePropertiesGroup;

  @Label("Preferred Edge Label Placement")
  @OptionGroupAnnotation(name = "LabelingGroup", position = 30)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object PreferredPlacementGroup;

  @OptionGroupAnnotation(name = "DescriptionGroup", position = 10)
  @ComponentType(ComponentTypes.FORMATTED_TEXT)
  public final String getDescriptionText() {
    return "<p>Polyline edge routing calculates polyline edge paths for a diagram's edges. " +
           "The positions of the nodes are not changed by this algorithm.</p>" +
           "<p>Edges will be routed orthogonally, that is each edge path consists of horizontal and vertical segments, or octilinear." +
           " Octilinear means that the slope of each segment of an edge path is a multiple of 45 degrees.</p>" +
           "<p>This type of edge routing is especially well suited for technical diagrams.</p>";
  }

  public enum EnumStrategies {
    BALANCED(0),

    MINIMIZE_BENDS(1),

    MINIMIZE_CROSSINGS(2),

    MINIMIZE_EDGE_LENGTH(3);

    private final int value;

    private EnumStrategies( final int value ) {
      this.value = value;
    }

    public int value() {
      return this.value;
    }

    public static final EnumStrategies fromOrdinal( int ordinal ) {
      for (EnumStrategies current : values()) {
        if (ordinal == current.value) return current;
      }
      throw new IllegalArgumentException("Enum has no value " + ordinal);
    }

  }

  public enum EnumMonotonyFlags {
    NONE(0),

    HORIZONTAL(1),

    VERTICAL(2),

    BOTH(3);

    private final int value;

    private EnumMonotonyFlags( final int value ) {
      this.value = value;
    }

    public int value() {
      return this.value;
    }

    public static final EnumMonotonyFlags fromOrdinal( int ordinal ) {
      for (EnumMonotonyFlags current : values()) {
        if (ordinal == current.value) return current;
      }
      throw new IllegalArgumentException("Enum has no value " + ordinal);
    }

  }

  public enum EnumBusRouting {
    NONE(0),

    SINGLE_BUS(1),

    BY_LABEL(2),

    BY_COLOR(3);

    private final int value;

    private EnumBusRouting( final int value ) {
      this.value = value;
    }

    public int value() {
      return this.value;
    }

    public static final EnumBusRouting fromOrdinal( int ordinal ) {
      for (EnumBusRouting current : values()) {
        if (ordinal == current.value) return current;
      }
      throw new IllegalArgumentException("Enum has no value " + ordinal);
    }

  }

  private Scope scopeItem = Scope.ROUTE_ALL_EDGES;

  @Label("Scope")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = Scope.class, stringValue = "ROUTE_ALL_EDGES")
  @EnumValueAnnotation(label = "All Edges", value = "ROUTE_ALL_EDGES")
  @EnumValueAnnotation(label = "Selected Edges", value = "ROUTE_AFFECTED_EDGES")
  @EnumValueAnnotation(label = "Edges at Selected Nodes", value = "ROUTE_EDGES_AT_AFFECTED_NODES")
  public final Scope getScopeItem() {
    return this.scopeItem;
  }

  @Label("Scope")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = Scope.class, stringValue = "ROUTE_ALL_EDGES")
  @EnumValueAnnotation(label = "All Edges", value = "ROUTE_ALL_EDGES")
  @EnumValueAnnotation(label = "Selected Edges", value = "ROUTE_AFFECTED_EDGES")
  @EnumValueAnnotation(label = "Edges at Selected Nodes", value = "ROUTE_EDGES_AT_AFFECTED_NODES")
  public final void setScopeItem( Scope value ) {
    this.scopeItem = value;
  }

  private EnumStrategies optimizationStrategyItem = EnumStrategies.BALANCED;

  @Label("Optimization Strategy")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 20)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumStrategies.class, stringValue = "BALANCED")
  @EnumValueAnnotation(label = "Balanced", value = "BALANCED")
  @EnumValueAnnotation(label = "Fewer Bends", value = "MINIMIZE_BENDS")
  @EnumValueAnnotation(label = "Fewer Crossings", value = "MINIMIZE_CROSSINGS")
  @EnumValueAnnotation(label = "Shorter Edges", value = "MINIMIZE_EDGE_LENGTH")
  public final EnumStrategies getOptimizationStrategyItem() {
    return this.optimizationStrategyItem;
  }

  @Label("Optimization Strategy")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 20)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumStrategies.class, stringValue = "BALANCED")
  @EnumValueAnnotation(label = "Balanced", value = "BALANCED")
  @EnumValueAnnotation(label = "Fewer Bends", value = "MINIMIZE_BENDS")
  @EnumValueAnnotation(label = "Fewer Crossings", value = "MINIMIZE_CROSSINGS")
  @EnumValueAnnotation(label = "Shorter Edges", value = "MINIMIZE_EDGE_LENGTH")
  public final void setOptimizationStrategyItem( EnumStrategies value ) {
    this.optimizationStrategyItem = value;
  }

  private EnumMonotonyFlags monotonicRestrictionItem = EnumMonotonyFlags.NONE;

  @Label("Monotonic Restriction")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 30)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumMonotonyFlags.class, stringValue = "NONE")
  @EnumValueAnnotation(label = "None", value = "NONE")
  @EnumValueAnnotation(label = "Horizontal", value = "HORIZONTAL")
  @EnumValueAnnotation(label = "Vertical", value = "VERTICAL")
  @EnumValueAnnotation(label = "Both", value = "BOTH")
  public final EnumMonotonyFlags getMonotonicRestrictionItem() {
    return this.monotonicRestrictionItem;
  }

  @Label("Monotonic Restriction")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 30)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumMonotonyFlags.class, stringValue = "NONE")
  @EnumValueAnnotation(label = "None", value = "NONE")
  @EnumValueAnnotation(label = "Horizontal", value = "HORIZONTAL")
  @EnumValueAnnotation(label = "Vertical", value = "VERTICAL")
  @EnumValueAnnotation(label = "Both", value = "BOTH")
  public final void setMonotonicRestrictionItem( EnumMonotonyFlags value ) {
    this.monotonicRestrictionItem = value;
  }

  private boolean reroutingEnabledItem;

  @Label("Reroute Crossing Edges")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 60)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isReroutingEnabledItem() {
    return this.reroutingEnabledItem;
  }

  @Label("Reroute Crossing Edges")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 60)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setReroutingEnabledItem( boolean value ) {
    this.reroutingEnabledItem = value;
  }

  private boolean usingIntermediatePointsItem;

  @Label("Keep Bends as Intermediate Points")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 65)
  public final boolean isUsingIntermediatePointsItem() {
    return this.usingIntermediatePointsItem;
  }

  @Label("Keep Bends as Intermediate Points")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 65)
  public final void setUsingIntermediatePointsItem( boolean value ) {
    this.usingIntermediatePointsItem = value;
  }

  private int maximumDurationItem;

  @Label("Maximum Duration")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 70)
  @DefaultValue(intValue = 30, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 0, max = 150)
  @ComponentType(ComponentTypes.SLIDER)
  public final int getMaximumDurationItem() {
    return this.maximumDurationItem;
  }

  @Label("Maximum Duration")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 70)
  @DefaultValue(intValue = 30, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 0, max = 150)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMaximumDurationItem( int value ) {
    this.maximumDurationItem = value;
  }

  private double minimumEdgeToEdgeDistanceItem;

  @Label("Edge to Edge")
  @OptionGroupAnnotation(name = "DistancesGroup", position = 10)
  @DefaultValue(doubleValue = 3.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getMinimumEdgeToEdgeDistanceItem() {
    return this.minimumEdgeToEdgeDistanceItem;
  }

  @Label("Edge to Edge")
  @OptionGroupAnnotation(name = "DistancesGroup", position = 10)
  @DefaultValue(doubleValue = 3.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumEdgeToEdgeDistanceItem( double value ) {
    this.minimumEdgeToEdgeDistanceItem = value;
  }

  private double minimumNodeToEdgeDistanceItem;

  @Label("Node to Edge")
  @OptionGroupAnnotation(name = "DistancesGroup", position = 20)
  @DefaultValue(doubleValue = 10.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getMinimumNodeToEdgeDistanceItem() {
    return this.minimumNodeToEdgeDistanceItem;
  }

  @Label("Node to Edge")
  @OptionGroupAnnotation(name = "DistancesGroup", position = 20)
  @DefaultValue(doubleValue = 10.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumNodeToEdgeDistanceItem( double value ) {
    this.minimumNodeToEdgeDistanceItem = value;
  }

  private double minimumNodeCornerDistanceItem;

  @Label("Port to Node Corner")
  @OptionGroupAnnotation(name = "DistancesGroup", position = 30)
  @DefaultValue(doubleValue = 3.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getMinimumNodeCornerDistanceItem() {
    return this.minimumNodeCornerDistanceItem;
  }

  @Label("Port to Node Corner")
  @OptionGroupAnnotation(name = "DistancesGroup", position = 30)
  @DefaultValue(doubleValue = 3.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumNodeCornerDistanceItem( double value ) {
    this.minimumNodeCornerDistanceItem = value;
  }

  private double minimumFirstSegmentLengthItem;

  @Label("First Segment Length")
  @OptionGroupAnnotation(name = "DistancesGroup", position = 40)
  @DefaultValue(doubleValue = 5.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getMinimumFirstSegmentLengthItem() {
    return this.minimumFirstSegmentLengthItem;
  }

  @Label("First Segment Length")
  @OptionGroupAnnotation(name = "DistancesGroup", position = 40)
  @DefaultValue(doubleValue = 5.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumFirstSegmentLengthItem( double value ) {
    this.minimumFirstSegmentLengthItem = value;
  }

  public final boolean isMinimumFirstSegmentLengthItemDisabled() {
    return getEdgeRoutingStyleItem() == EdgeRoutingStyle.CURVED && getSourceConnectionStyleItem() == CurveConnectionStyle.ORGANIC;
  }

  private double minimumLastSegmentLengthItem;

  @Label("Last Segment Length")
  @OptionGroupAnnotation(name = "DistancesGroup", position = 50)
  @DefaultValue(doubleValue = 10.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getMinimumLastSegmentLengthItem() {
    return this.minimumLastSegmentLengthItem;
  }

  @Label("Last Segment Length")
  @OptionGroupAnnotation(name = "DistancesGroup", position = 50)
  @DefaultValue(doubleValue = 10.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumLastSegmentLengthItem( double value ) {
    this.minimumLastSegmentLengthItem = value;
  }

  public final boolean isMinimumLastSegmentLengthItemDisabled() {
    return getEdgeRoutingStyleItem() == EdgeRoutingStyle.CURVED && getTargetConnectionStyleItem() == CurveConnectionStyle.ORGANIC;
  }

  private boolean gridEnabledItem;

  @Label("Route on Grid")
  @OptionGroupAnnotation(name = "GridGroup", position = 10)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isGridEnabledItem() {
    return this.gridEnabledItem;
  }

  @Label("Route on Grid")
  @OptionGroupAnnotation(name = "GridGroup", position = 10)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setGridEnabledItem( boolean value ) {
    this.gridEnabledItem = value;
  }

  private double gridSpacingItem;

  @Label("Grid Spacing")
  @OptionGroupAnnotation(name = "GridGroup", position = 20)
  @DefaultValue(doubleValue = 10.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 2, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getGridSpacingItem() {
    return this.gridSpacingItem;
  }

  @Label("Grid Spacing")
  @OptionGroupAnnotation(name = "GridGroup", position = 20)
  @DefaultValue(doubleValue = 10.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 2, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setGridSpacingItem( double value ) {
    this.gridSpacingItem = value;
  }

  public final boolean isGridSpacingItemDisabled() {
    return isGridEnabledItem() == false;
  }

  private EdgeRoutingStyle edgeRoutingStyleItem = EdgeRoutingStyle.ORTHOGONAL;

  @Label("Routing Style")
  @OptionGroupAnnotation(name = "RoutingStyleGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EdgeRoutingStyle.class, stringValue = "ORTHOGONAL")
  @EnumValueAnnotation(label = "Orthogonal", value = "ORTHOGONAL")
  @EnumValueAnnotation(label = "Octilinear", value = "OCTILINEAR")
  @EnumValueAnnotation(label = "Curved", value = "CURVED")
  public final EdgeRoutingStyle getEdgeRoutingStyleItem() {
    return this.edgeRoutingStyleItem;
  }

  @Label("Routing Style")
  @OptionGroupAnnotation(name = "RoutingStyleGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EdgeRoutingStyle.class, stringValue = "ORTHOGONAL")
  @EnumValueAnnotation(label = "Orthogonal", value = "ORTHOGONAL")
  @EnumValueAnnotation(label = "Octilinear", value = "OCTILINEAR")
  @EnumValueAnnotation(label = "Curved", value = "CURVED")
  public final void setEdgeRoutingStyleItem( EdgeRoutingStyle value ) {
    this.edgeRoutingStyleItem = value;
  }

  private double preferredOctilinearSegmentLengthItem;

  @Label("Preferred Octilinear Corner Length")
  @OptionGroupAnnotation(name = "RoutingStyleGroup", position = 20)
  @DefaultValue(doubleValue = 30.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 5, max = 500)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getPreferredOctilinearSegmentLengthItem() {
    return this.preferredOctilinearSegmentLengthItem;
  }

  @Label("Preferred Octilinear Corner Length")
  @OptionGroupAnnotation(name = "RoutingStyleGroup", position = 20)
  @DefaultValue(doubleValue = 30.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 5, max = 500)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setPreferredOctilinearSegmentLengthItem( double value ) {
    this.preferredOctilinearSegmentLengthItem = value;
  }

  public final boolean isPreferredOctilinearSegmentLengthItemDisabled() {
    return getEdgeRoutingStyleItem() != EdgeRoutingStyle.OCTILINEAR;
  }

  private double maximumOctilinearSegmentRatioItem;

  @Label("Maximum Octilinear Segment Length Ratio")
  @OptionGroupAnnotation(name = "RoutingStyleGroup", position = 25)
  @DefaultValue(doubleValue = 0.3d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 0.5, step = 0.05)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getMaximumOctilinearSegmentRatioItem() {
    return this.maximumOctilinearSegmentRatioItem;
  }

  @Label("Maximum Octilinear Segment Length Ratio")
  @OptionGroupAnnotation(name = "RoutingStyleGroup", position = 25)
  @DefaultValue(doubleValue = 0.3d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 0.5, step = 0.05)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMaximumOctilinearSegmentRatioItem( double value ) {
    this.maximumOctilinearSegmentRatioItem = value;
  }

  public final boolean isMaximumOctilinearSegmentRatioItemDisabled() {
    return getEdgeRoutingStyleItem() != EdgeRoutingStyle.OCTILINEAR;
  }

  private CurveConnectionStyle sourceConnectionStyleItem = CurveConnectionStyle.KEEP_PORT;

  @Label("Curved connection at source")
  @OptionGroupAnnotation(name = "RoutingStyleGroup", position = 30)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = CurveConnectionStyle.class, stringValue = "KEEP_PORT")
  @EnumValueAnnotation(label = "Straight", value = "KEEP_PORT")
  @EnumValueAnnotation(label = "Organic", value = "ORGANIC")
  public final CurveConnectionStyle getSourceConnectionStyleItem() {
    return this.sourceConnectionStyleItem;
  }

  @Label("Curved connection at source")
  @OptionGroupAnnotation(name = "RoutingStyleGroup", position = 30)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = CurveConnectionStyle.class, stringValue = "KEEP_PORT")
  @EnumValueAnnotation(label = "Straight", value = "KEEP_PORT")
  @EnumValueAnnotation(label = "Organic", value = "ORGANIC")
  public final void setSourceConnectionStyleItem( CurveConnectionStyle value ) {
    this.sourceConnectionStyleItem = value;
  }

  public final boolean isSourceConnectionStyleItemDisabled() {
    return getEdgeRoutingStyleItem() != EdgeRoutingStyle.CURVED;
  }

  private CurveConnectionStyle targetConnectionStyleItem = CurveConnectionStyle.KEEP_PORT;

  @Label("Curved connection at target")
  @OptionGroupAnnotation(name = "RoutingStyleGroup", position = 40)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = CurveConnectionStyle.class, stringValue = "KEEP_PORT")
  @EnumValueAnnotation(label = "Straight", value = "KEEP_PORT")
  @EnumValueAnnotation(label = "Organic", value = "ORGANIC")
  public final CurveConnectionStyle getTargetConnectionStyleItem() {
    return this.targetConnectionStyleItem;
  }

  @Label("Curved connection at target")
  @OptionGroupAnnotation(name = "RoutingStyleGroup", position = 40)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = CurveConnectionStyle.class, stringValue = "KEEP_PORT")
  @EnumValueAnnotation(label = "Straight", value = "KEEP_PORT")
  @EnumValueAnnotation(label = "Organic", value = "ORGANIC")
  public final void setTargetConnectionStyleItem( CurveConnectionStyle value ) {
    this.targetConnectionStyleItem = value;
  }

  public final boolean isTargetConnectionStyleItemDisabled() {
    return getEdgeRoutingStyleItem() != EdgeRoutingStyle.CURVED;
  }

  private EnumBusRouting busRoutingItem = EnumBusRouting.NONE;

  @Label("Bus routing")
  @OptionGroupAnnotation(name = "RoutingStyleGroup", position = 50)
  @EnumValueAnnotation(label = "No Buses", value = "NONE")
  @EnumValueAnnotation(label = "Single Bus", value = "SINGLE_BUS")
  @EnumValueAnnotation(label = "By Edge Color", value = "BY_COLOR")
  @EnumValueAnnotation(label = "By Edge Label", value = "BY_LABEL")
  public final EnumBusRouting getBusRoutingItem() {
    return this.busRoutingItem;
  }

  @Label("Bus routing")
  @OptionGroupAnnotation(name = "RoutingStyleGroup", position = 50)
  @EnumValueAnnotation(label = "No Buses", value = "NONE")
  @EnumValueAnnotation(label = "Single Bus", value = "SINGLE_BUS")
  @EnumValueAnnotation(label = "By Edge Color", value = "BY_COLOR")
  @EnumValueAnnotation(label = "By Edge Label", value = "BY_LABEL")
  public final void setBusRoutingItem( EnumBusRouting value ) {
    this.busRoutingItem = value;
  }

  private boolean consideringNodeLabelsItem;

  @Label("Consider Node Labels")
  @OptionGroupAnnotation(name = "NodePropertiesGroup", position = 10)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isConsideringNodeLabelsItem() {
    return this.consideringNodeLabelsItem;
  }

  @Label("Consider Node Labels")
  @OptionGroupAnnotation(name = "NodePropertiesGroup", position = 10)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setConsideringNodeLabelsItem( boolean value ) {
    this.consideringNodeLabelsItem = value;
  }

  private boolean consideringEdgeLabelsItem;

  @Label("Consider Fixed Edges' Labels")
  @OptionGroupAnnotation(name = "EdgePropertiesGroup", position = 10)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isConsideringEdgeLabelsItem() {
    return this.consideringEdgeLabelsItem;
  }

  @Label("Consider Fixed Edges' Labels")
  @OptionGroupAnnotation(name = "EdgePropertiesGroup", position = 10)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setConsideringEdgeLabelsItem( boolean value ) {
    this.consideringEdgeLabelsItem = value;
  }

  public enum EnumEdgeLabeling {
    NONE(0),

    INTEGRATED(1),

    GENERIC(2);

    private final int value;

    private EnumEdgeLabeling( final int value ) {
      this.value = value;
    }

    public int value() {
      return this.value;
    }

    public static final EnumEdgeLabeling fromOrdinal( int ordinal ) {
      for (EnumEdgeLabeling current : values()) {
        if (ordinal == current.value) return current;
      }
      throw new IllegalArgumentException("Enum has no value " + ordinal);
    }

  }

  private EnumEdgeLabeling edgeLabelingEnabledItem = EnumEdgeLabeling.NONE;

  @Label("Edge Labeling")
  @OptionGroupAnnotation(name = "EdgePropertiesGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumEdgeLabeling.class, stringValue = "NONE")
  @EnumValueAnnotation(label = "None", value = "NONE")
  @EnumValueAnnotation(label = "Integrated", value = "INTEGRATED")
  @EnumValueAnnotation(label = "Generic", value = "GENERIC")
  public final EnumEdgeLabeling getEdgeLabelingEnabledItem() {
    return this.edgeLabelingEnabledItem;
  }

  @Label("Edge Labeling")
  @OptionGroupAnnotation(name = "EdgePropertiesGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumEdgeLabeling.class, stringValue = "NONE")
  @EnumValueAnnotation(label = "None", value = "NONE")
  @EnumValueAnnotation(label = "Integrated", value = "INTEGRATED")
  @EnumValueAnnotation(label = "Generic", value = "GENERIC")
  public final void setEdgeLabelingEnabledItem( EnumEdgeLabeling value ) {
    this.edgeLabelingEnabledItem = value;
  }

  private LayoutConfiguration.EnumLabelPlacementOrientation labelPlacementOrientationItem = LayoutConfiguration.EnumLabelPlacementOrientation.PARALLEL;

  @Label("Orientation")
  @OptionGroupAnnotation(name = "PreferredPlacementGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LayoutConfiguration.EnumLabelPlacementOrientation.class, stringValue = "HORIZONTAL")
  @EnumValueAnnotation(label = "Parallel", value = "PARALLEL")
  @EnumValueAnnotation(label = "Orthogonal", value = "ORTHOGONAL")
  @EnumValueAnnotation(label = "Horizontal", value = "HORIZONTAL")
  @EnumValueAnnotation(label = "Vertical", value = "VERTICAL")
  public final LayoutConfiguration.EnumLabelPlacementOrientation getLabelPlacementOrientationItem() {
    return this.labelPlacementOrientationItem;
  }

  @Label("Orientation")
  @OptionGroupAnnotation(name = "PreferredPlacementGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LayoutConfiguration.EnumLabelPlacementOrientation.class, stringValue = "HORIZONTAL")
  @EnumValueAnnotation(label = "Parallel", value = "PARALLEL")
  @EnumValueAnnotation(label = "Orthogonal", value = "ORTHOGONAL")
  @EnumValueAnnotation(label = "Horizontal", value = "HORIZONTAL")
  @EnumValueAnnotation(label = "Vertical", value = "VERTICAL")
  public final void setLabelPlacementOrientationItem( LayoutConfiguration.EnumLabelPlacementOrientation value ) {
    this.labelPlacementOrientationItem = value;
  }

  public final boolean isLabelPlacementOrientationItemDisabled() {
    return getEdgeLabelingEnabledItem() == EnumEdgeLabeling.NONE;
  }

  private boolean reducingAmbiguityItem;

  @Label("Reduce Ambiguity")
  @OptionGroupAnnotation(name = "EdgePropertiesGroup", position = 30)
  public final boolean isReducingAmbiguityItem() {
    return this.reducingAmbiguityItem;
  }

  @Label("Reduce Ambiguity")
  @OptionGroupAnnotation(name = "EdgePropertiesGroup", position = 30)
  public final void setReducingAmbiguityItem( boolean value ) {
    this.reducingAmbiguityItem = value;
  }

  public final boolean isReducingAmbiguityItemDisabled() {
    return getEdgeLabelingEnabledItem() != EnumEdgeLabeling.GENERIC;
  }

  private LayoutConfiguration.EnumLabelPlacementAlongEdge labelPlacementAlongEdgeItem = LayoutConfiguration.EnumLabelPlacementAlongEdge.ANYWHERE;

  @Label("Along Edge")
  @OptionGroupAnnotation(name = "PreferredPlacementGroup", position = 20)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LayoutConfiguration.EnumLabelPlacementAlongEdge.class, stringValue = "CENTERED")
  @EnumValueAnnotation(label = "Anywhere", value = "ANYWHERE")
  @EnumValueAnnotation(label = "At Source", value = "AT_SOURCE")
  @EnumValueAnnotation(label = "At Target", value = "AT_TARGET")
  @EnumValueAnnotation(label = "Centered", value = "CENTERED")
  public final LayoutConfiguration.EnumLabelPlacementAlongEdge getLabelPlacementAlongEdgeItem() {
    return this.labelPlacementAlongEdgeItem;
  }

  @Label("Along Edge")
  @OptionGroupAnnotation(name = "PreferredPlacementGroup", position = 20)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LayoutConfiguration.EnumLabelPlacementAlongEdge.class, stringValue = "CENTERED")
  @EnumValueAnnotation(label = "Anywhere", value = "ANYWHERE")
  @EnumValueAnnotation(label = "At Source", value = "AT_SOURCE")
  @EnumValueAnnotation(label = "At Target", value = "AT_TARGET")
  @EnumValueAnnotation(label = "Centered", value = "CENTERED")
  public final void setLabelPlacementAlongEdgeItem( LayoutConfiguration.EnumLabelPlacementAlongEdge value ) {
    this.labelPlacementAlongEdgeItem = value;
  }

  public final boolean isLabelPlacementAlongEdgeItemDisabled() {
    return getEdgeLabelingEnabledItem() == EnumEdgeLabeling.NONE;
  }

  private LayoutConfiguration.EnumLabelPlacementSideOfEdge labelPlacementSideOfEdgeItem = LayoutConfiguration.EnumLabelPlacementSideOfEdge.ANYWHERE;

  @Label("Side of Edge")
  @OptionGroupAnnotation(name = "PreferredPlacementGroup", position = 30)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LayoutConfiguration.EnumLabelPlacementSideOfEdge.class, stringValue = "ON_EDGE")
  @EnumValueAnnotation(label = "Anywhere", value = "ANYWHERE")
  @EnumValueAnnotation(label = "On Edge", value = "ON_EDGE")
  @EnumValueAnnotation(label = "Left", value = "LEFT")
  @EnumValueAnnotation(label = "Right", value = "RIGHT")
  @EnumValueAnnotation(label = "Left or Right", value = "LEFT_OR_RIGHT")
  public final LayoutConfiguration.EnumLabelPlacementSideOfEdge getLabelPlacementSideOfEdgeItem() {
    return this.labelPlacementSideOfEdgeItem;
  }

  @Label("Side of Edge")
  @OptionGroupAnnotation(name = "PreferredPlacementGroup", position = 30)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LayoutConfiguration.EnumLabelPlacementSideOfEdge.class, stringValue = "ON_EDGE")
  @EnumValueAnnotation(label = "Anywhere", value = "ANYWHERE")
  @EnumValueAnnotation(label = "On Edge", value = "ON_EDGE")
  @EnumValueAnnotation(label = "Left", value = "LEFT")
  @EnumValueAnnotation(label = "Right", value = "RIGHT")
  @EnumValueAnnotation(label = "Left or Right", value = "LEFT_OR_RIGHT")
  public final void setLabelPlacementSideOfEdgeItem( LayoutConfiguration.EnumLabelPlacementSideOfEdge value ) {
    this.labelPlacementSideOfEdgeItem = value;
  }

  public final boolean isLabelPlacementSideOfEdgeItemDisabled() {
    return getEdgeLabelingEnabledItem() == EnumEdgeLabeling.NONE;
  }

  private double labelPlacementDistanceItem;

  @Label("Distance")
  @OptionGroupAnnotation(name = "PreferredPlacementGroup", position = 40)
  @DefaultValue(doubleValue = 10.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.0d, max = 40.0d)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getLabelPlacementDistanceItem() {
    return this.labelPlacementDistanceItem;
  }

  @Label("Distance")
  @OptionGroupAnnotation(name = "PreferredPlacementGroup", position = 40)
  @DefaultValue(doubleValue = 10.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.0d, max = 40.0d)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setLabelPlacementDistanceItem( double value ) {
    this.labelPlacementDistanceItem = value;
  }

  public final boolean isLabelPlacementDistanceItemDisabled() {
    return getEdgeLabelingEnabledItem() == EnumEdgeLabeling.NONE || getLabelPlacementSideOfEdgeItem() == LayoutConfiguration.EnumLabelPlacementSideOfEdge.ON_EDGE;
  }

}
