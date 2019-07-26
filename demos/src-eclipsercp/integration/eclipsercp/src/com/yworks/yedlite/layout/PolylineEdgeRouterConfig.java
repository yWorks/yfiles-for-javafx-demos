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
package com.yworks.yedlite.layout;

import com.yworks.yfiles.graphml.DefaultValue;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.SequentialLayout;
import com.yworks.yfiles.layout.labeling.GenericLabeling;
import com.yworks.yfiles.layout.router.MonotonicPathRestriction;
import com.yworks.yfiles.layout.router.Scope;
import com.yworks.yfiles.layout.router.polyline.EdgeLayoutDescriptor;
import com.yworks.yfiles.layout.router.polyline.EdgeRouter;
import com.yworks.yfiles.layout.router.polyline.Grid;
import com.yworks.yfiles.layout.router.polyline.PenaltySettings;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yedlite.toolkit.optionhandler.ComponentType;
import com.yworks.yedlite.toolkit.optionhandler.ComponentTypes;
import com.yworks.yedlite.toolkit.optionhandler.EnumValueAnnotation;
import com.yworks.yedlite.toolkit.optionhandler.Label;
import com.yworks.yedlite.toolkit.optionhandler.MinMax;
import com.yworks.yedlite.toolkit.optionhandler.OptionGroupAnnotation;

/**
 * Configuration options for the {@link EdgeRouter} algorithm.
 */
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
    setEnableReroutingItem(router.isReroutingEnabled());
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

    setEnablePolylineRoutingItem(true);
    setPreferredPolylineSegmentLengthItem(router.getPreferredPolylineSegmentLength());

    setConsiderNodeLabelsItem(router.isNodeLabelConsiderationEnabled());
    setConsiderEdgeLabelsItem(router.isEdgeLabelConsiderationEnabled());
    setEdgeLabelingItem(false);
    setLabelPlacementAlongEdgeItem(LayoutConfiguration.EnumLabelPlacementAlongEdge.CENTERED);
    setLabelPlacementSideOfEdgeItem(LayoutConfiguration.EnumLabelPlacementSideOfEdge.ON_EDGE);
    setLabelPlacementOrientationItem(LayoutConfiguration.EnumLabelPlacementOrientation.HORIZONTAL);
    setLabelPlacementDistanceItem(10.0d);
  }

  @Override
  protected ILayoutAlgorithm createConfiguredLayout(GraphControl graphControl ) {
    EdgeRouter router = new EdgeRouter();
    EdgeLayoutDescriptor descriptor = router.getDefaultEdgeLayoutDescriptor();

    router.setScope(getScopeItem());

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
    router.setMinimumNodeToEdgeDistance(getMinimumNodeToEdgeDistanceItem());
    descriptor.setMinimumNodeCornerDistance(getMinimumNodeCornerDistanceItem());
    descriptor.setMinimumFirstSegmentLength(getMinimumFirstSegmentLengthItem());
    descriptor.setMinimumLastSegmentLength(getMinimumLastSegmentLengthItem());

    if (isGridEnabledItem()) {
      router.setGrid(new Grid(0, 0, getGridSpacingItem()));
    } else {
      router.setGrid(null);
    }

    router.setNodeLabelConsiderationEnabled(isConsiderNodeLabelsItem());
    router.setEdgeLabelConsiderationEnabled(isConsiderEdgeLabelsItem());
    router.setReroutingEnabled(isEnableReroutingItem());

    router.setPolylineRoutingEnabled(isEnablePolylineRoutingItem());
    router.setPreferredPolylineSegmentLength(getPreferredPolylineSegmentLengthItem());
    router.setMaximumDuration(getMaximumDurationItem() * 1000);

    SequentialLayout layout = new SequentialLayout();
    layout.appendLayout(router);

    if (isEdgeLabelingItem()) {
      GenericLabeling genericLabeling = new GenericLabeling();
      genericLabeling.setEdgeLabelPlacementEnabled(true);
      genericLabeling.setNodeLabelPlacementEnabled(false);
      layout.appendLayout(genericLabeling);
    }

    addPreferredPlacementDescriptor(graphControl.getGraph(), getLabelPlacementAlongEdgeItem(), getLabelPlacementSideOfEdgeItem(), getLabelPlacementOrientationItem(), getLabelPlacementDistanceItem());

    return layout;
  }

  @Label("Layout")
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

  @Label("Octilinear Routing")
  @OptionGroupAnnotation(name = "RootGroup", position = 40)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object PolylineGroup;

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

  private Scope scopeItem = null;

  @Label("Scope")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = Scope.class, stringValue = "ROUTE_ALL_EDGES")
  @EnumValueAnnotation(label = "All Edges", value = "ROUTE_ALL_EDGES")
  @EnumValueAnnotation(label = "Affected Edges", value = "ROUTE_AFFECTED_EDGES")
  @EnumValueAnnotation(label = "Edges at Affected Nodes", value = "ROUTE_EDGES_AT_AFFECTED_NODES")
  public final Scope getScopeItem() {
    return this.scopeItem;
  }

  @Label("Scope")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = Scope.class, stringValue = "ROUTE_ALL_EDGES")
  @EnumValueAnnotation(label = "All Edges", value = "ROUTE_ALL_EDGES")
  @EnumValueAnnotation(label = "Affected Edges", value = "ROUTE_AFFECTED_EDGES")
  @EnumValueAnnotation(label = "Edges at Affected Nodes", value = "ROUTE_EDGES_AT_AFFECTED_NODES")
  public final void setScopeItem( Scope value ) {
    this.scopeItem = value;
  }

  private EnumStrategies optimizationStrategyItem = EnumStrategies.BALANCED;

  @Label("Optimization Strategy")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 20)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumStrategies.class, stringValue = "BALANCED")
  @EnumValueAnnotation(label = "Balanced", value = "BALANCED")
  @EnumValueAnnotation(label = "Less Bends", value = "MINIMIZE_BENDS")
  @EnumValueAnnotation(label = "Less Crossings", value = "MINIMIZE_CROSSINGS")
  @EnumValueAnnotation(label = "Shorter Edges", value = "MINIMIZE_EDGE_LENGTH")
  public final EnumStrategies getOptimizationStrategyItem() {
    return this.optimizationStrategyItem;
  }

  @Label("Optimization Strategy")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 20)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumStrategies.class, stringValue = "BALANCED")
  @EnumValueAnnotation(label = "Balanced", value = "BALANCED")
  @EnumValueAnnotation(label = "Less Bends", value = "MINIMIZE_BENDS")
  @EnumValueAnnotation(label = "Less Crossings", value = "MINIMIZE_CROSSINGS")
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

  private boolean enableReroutingItem;

  @Label("Reroute Crossing Edges")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 60)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isEnableReroutingItem() {
    return this.enableReroutingItem;
  }

  @Label("Reroute Crossing Edges")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 60)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setEnableReroutingItem( boolean value ) {
    this.enableReroutingItem = value;
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

  public final boolean isShouldDisableGridSpacingItem() {
    return isGridEnabledItem() == false;
  }

  private boolean enablePolylineRoutingItem;

  @Label("Octilinear Routing")
  @OptionGroupAnnotation(name = "PolylineGroup", position = 10)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isEnablePolylineRoutingItem() {
    return this.enablePolylineRoutingItem;
  }

  @Label("Octilinear Routing")
  @OptionGroupAnnotation(name = "PolylineGroup", position = 10)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setEnablePolylineRoutingItem( boolean value ) {
    this.enablePolylineRoutingItem = value;
  }

  private double preferredPolylineSegmentLengthItem;

  @Label("Preferred Polyline Segment Length")
  @OptionGroupAnnotation(name = "PolylineGroup", position = 20)
  @DefaultValue(doubleValue = 30.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 5, max = 500)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getPreferredPolylineSegmentLengthItem() {
    return this.preferredPolylineSegmentLengthItem;
  }

  @Label("Preferred Polyline Segment Length")
  @OptionGroupAnnotation(name = "PolylineGroup", position = 20)
  @DefaultValue(doubleValue = 30.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 5, max = 500)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setPreferredPolylineSegmentLengthItem( double value ) {
    this.preferredPolylineSegmentLengthItem = value;
  }

  public final boolean isShouldDisablePreferredPolylineSegmentLengthItem() {
    return isEnablePolylineRoutingItem() == false;
  }

  private boolean considerNodeLabelsItem;

  @Label("Consider Node Labels")
  @OptionGroupAnnotation(name = "NodePropertiesGroup", position = 10)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isConsiderNodeLabelsItem() {
    return this.considerNodeLabelsItem;
  }

  @Label("Consider Node Labels")
  @OptionGroupAnnotation(name = "NodePropertiesGroup", position = 10)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setConsiderNodeLabelsItem( boolean value ) {
    this.considerNodeLabelsItem = value;
  }

  private boolean considerEdgeLabelsItem;

  @Label("Consider Fixed Edges' Labels")
  @OptionGroupAnnotation(name = "EdgePropertiesGroup", position = 10)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isConsiderEdgeLabelsItem() {
    return this.considerEdgeLabelsItem;
  }

  @Label("Consider Fixed Edges' Labels")
  @OptionGroupAnnotation(name = "EdgePropertiesGroup", position = 10)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setConsiderEdgeLabelsItem( boolean value ) {
    this.considerEdgeLabelsItem = value;
  }

  private boolean edgeLabelingItem;

  @Label("Edge Labeling")
  @OptionGroupAnnotation(name = "EdgePropertiesGroup", position = 20)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isEdgeLabelingItem() {
    return this.edgeLabelingItem;
  }

  @Label("Edge Labeling")
  @OptionGroupAnnotation(name = "EdgePropertiesGroup", position = 20)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setEdgeLabelingItem( boolean value ) {
    this.edgeLabelingItem = value;
  }

  private LayoutConfiguration.EnumLabelPlacementOrientation labelPlacementOrientationItem = EnumLabelPlacementOrientation.PARALLEL;

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

  public final boolean isShouldDisableLabelPlacementOrientationItem() {
    return !isEdgeLabelingItem();
  }

  private LayoutConfiguration.EnumLabelPlacementAlongEdge labelPlacementAlongEdgeItem = EnumLabelPlacementAlongEdge.ANYWHERE;

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

  public final boolean isShouldDisableLabelPlacementAlongEdgeItem() {
    return !isEdgeLabelingItem();
  }

  private LayoutConfiguration.EnumLabelPlacementSideOfEdge labelPlacementSideOfEdgeItem = EnumLabelPlacementSideOfEdge.ANYWHERE;

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

  public final boolean isShouldDisableLabelPlacementSideOfEdgeItem() {
    return !isEdgeLabelingItem();
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

  public final boolean isShouldDisableLabelPlacementDistanceItem() {
    return !isEdgeLabelingItem() || getLabelPlacementSideOfEdgeItem() == LayoutConfiguration.EnumLabelPlacementSideOfEdge.ON_EDGE;
  }
}
