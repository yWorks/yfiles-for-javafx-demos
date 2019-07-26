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

import com.yworks.yedlite.toolkit.optionhandler.ComponentType;
import com.yworks.yedlite.toolkit.optionhandler.ComponentTypes;
import com.yworks.yedlite.toolkit.optionhandler.EnumValueAnnotation;
import com.yworks.yedlite.toolkit.optionhandler.Label;
import com.yworks.yedlite.toolkit.optionhandler.MinMax;
import com.yworks.yedlite.toolkit.optionhandler.OptionGroupAnnotation;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graphml.DefaultValue;
import com.yworks.yfiles.layout.ComponentArrangementStyles;
import com.yworks.yfiles.layout.ComponentLayout;
import com.yworks.yfiles.layout.FixGroupLayoutStage;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.InterEdgeRoutingStyle;
import com.yworks.yfiles.layout.orthogonal.LayoutStyle;
import com.yworks.yfiles.layout.orthogonal.OrthogonalLayout;
import com.yworks.yfiles.view.GraphControl;

/**
 * Configuration options for the layout algorithm of the same name.
 */
@Label("OrthogonalLayout")
public class OrthogonalLayoutConfig extends LayoutConfiguration {
  /**
   * Setup default values for various configuration parameters.
   */
  public OrthogonalLayoutConfig() {
    setStyleItem(LayoutStyle.NORMAL);
    setGridSpacingItem(15);
    setEdgeLengthReductionItem(true);
    setFromSketchModeItem(false);
    setCrossingReductionItem(true);
    setPerceivedBendsPostprocessingItem(true);
    setRandomizationItem(true);
    setFaceMaximizationItem(false);

    setConsiderNodeLabelsItem(false);
    setEdgeLabelingItem(EnumEdgeLabeling.NONE);
    setLabelPlacementAlongEdgeItem(LayoutConfiguration.EnumLabelPlacementAlongEdge.CENTERED);
    setLabelPlacementSideOfEdgeItem(LayoutConfiguration.EnumLabelPlacementSideOfEdge.ON_EDGE);
    setLabelPlacementOrientationItem(LayoutConfiguration.EnumLabelPlacementOrientation.HORIZONTAL);
    setLabelPlacementDistanceItem(10.0d);

    setMinimumFirstSegmentLengthItem(15.0d);
    setMinimumSegmentLengthItem(15.0d);
    setMinimumLastSegmentLengthItem(15.0d);
    setConsiderDirectionOfSelectedEdges(false);

    setGroupLayoutPolicyItem(EnumGroupPolicy.LAYOUT_GROUPS);
  }

  @Override
  protected ILayoutAlgorithm createConfiguredLayout( GraphControl graphControl ) {
    OrthogonalLayout layout = new OrthogonalLayout();
    layout.setGridSpacing(getGridSpacingItem());
    layout.setPerceivedBendOptimizationEnabled(isPerceivedBendsPostprocessingItem());
    layout.getEdgeLayoutDescriptor().setMinimumFirstSegmentLength(getMinimumFirstSegmentLengthItem());
    layout.getEdgeLayoutDescriptor().setMinimumLastSegmentLength(getMinimumLastSegmentLengthItem());
    layout.getEdgeLayoutDescriptor().setMinimumSegmentLength(getMinimumSegmentLengthItem());
    layout.setCrossingReductionEnabled(isCrossingReductionItem());
    layout.setEdgeLengthReductionEnabled(isEdgeLengthReductionItem());
    layout.setRandomizationEnabled(isRandomizationItem());
    ((ComponentLayout)layout.getComponentLayout()).setStyle(ComponentArrangementStyles.MULTI_ROWS);

    if (getGroupLayoutPolicyItem() == EnumGroupPolicy.IGNORE_GROUPS || !containsGroupNodes(graphControl)) {
      layout.setHideGroupsStageEnabled(true);
      layout.setLayoutStyle(getStyleItem());
      layout.setFaceMaximizationEnabled(isFaceMaximizationItem());
      layout.setFromSketchModeEnabled(isFromSketchModeItem());
    } else if (getGroupLayoutPolicyItem() == EnumGroupPolicy.FIX_GROUPS) {
      FixGroupLayoutStage fgl = new FixGroupLayoutStage((ILayoutAlgorithm)null);
      fgl.setInterEdgeRoutingStyle(InterEdgeRoutingStyle.ORTHOGONAL);
      layout.prependStage(fgl);
      layout.setFaceMaximizationEnabled(isFaceMaximizationItem());
      layout.setFromSketchModeEnabled(isFromSketchModeItem());
    }

    //set edge labeling options
    boolean normalStyle = (layout.getLayoutStyle() == LayoutStyle.NORMAL);
    layout.setIntegratedEdgeLabelingEnabled((getEdgeLabelingItem() == EnumEdgeLabeling.INTEGRATED && normalStyle));
    layout.setNodeLabelConsiderationEnabled(isConsiderNodeLabelsItem() && normalStyle);

    if (getEdgeLabelingItem() == EnumEdgeLabeling.GENERIC || (getEdgeLabelingItem() == EnumEdgeLabeling.INTEGRATED && normalStyle)) {
      layout.setLabelingEnabled(true);
    } else if (!isConsiderNodeLabelsItem() || !normalStyle) {
      layout.setLabelingEnabled(false);
    }
    addPreferredPlacementDescriptor(graphControl.getGraph(), getLabelPlacementAlongEdgeItem(), getLabelPlacementSideOfEdgeItem(), getLabelPlacementOrientationItem(), getLabelPlacementDistanceItem());

    if (isConsiderDirectionOfSelectedEdges() && graphControl.getSelection().getSelectedEdges().size() > 0) {
      graphControl.getGraph().getMapperRegistry().createFunctionMapper(IEdge.class, Boolean.class, OrthogonalLayout.DIRECTED_EDGE_DPKEY, new IsEdgeSelected(graphControl));
    }

    return layout;
  }

  private static boolean containsGroupNodes( GraphControl graphControl ) {
    IGraph graph = graphControl.getGraph();
    return graph != null && graph.getGroupingSupport().hasGroupNodes();
  }

  /**
   * Called after the layout animation is done.
   */
  @Override
  protected void postProcess( GraphControl graphControl ) {
    graphControl.getGraph().getMapperRegistry().removeMapper(OrthogonalLayout.DIRECTED_EDGE_DPKEY);
  }

  @Label("Layout")
  @OptionGroupAnnotation(name = "RootGroup", position = 10)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object LayoutGroup;

  @Label("Labeling")
  @OptionGroupAnnotation(name = "RootGroup", position = 20)
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

  @Label("Edges")
  @OptionGroupAnnotation(name = "RootGroup", position = 30)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object EdgesGroup;

  @Label("Grouping")
  @OptionGroupAnnotation(name = "RootGroup", position = 40)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object GroupingGroup;

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

  public enum EnumGroupPolicy {
    LAYOUT_GROUPS(0),

    FIX_GROUPS(1),

    IGNORE_GROUPS(2);

    private final int value;

    private EnumGroupPolicy( final int value ) {
      this.value = value;
    }

    public int value() {
      return this.value;
    }

    public static final EnumGroupPolicy fromOrdinal( int ordinal ) {
      for (EnumGroupPolicy current : values()) {
        if (ordinal == current.value) return current;
      }
      throw new IllegalArgumentException("Enum has no value " + ordinal);
    }
  }

  private LayoutStyle styleItem = null;

  @Label("Style")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LayoutStyle.class, stringValue = "NORMAL")
  @EnumValueAnnotation(label = "Normal", value = "NORMAL")
  @EnumValueAnnotation(label = "Uniform Node Sizes", value = "UNIFORM")
  @EnumValueAnnotation(label = "Node Boxes", value = "BOX")
  @EnumValueAnnotation(label = "Mixed", value = "MIXED")
  @EnumValueAnnotation(label = "Node Boxes (Fixed Node Size)", value = "FIXED_BOX")
  @EnumValueAnnotation(label = "Mixed (Fixed Node Size)", value = "FIXED_MIXED")
  public final LayoutStyle getStyleItem() {
    return this.styleItem;
  }

  @Label("Style")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LayoutStyle.class, stringValue = "NORMAL")
  @EnumValueAnnotation(label = "Normal", value = "NORMAL")
  @EnumValueAnnotation(label = "Uniform Node Sizes", value = "UNIFORM")
  @EnumValueAnnotation(label = "Node Boxes", value = "BOX")
  @EnumValueAnnotation(label = "Mixed", value = "MIXED")
  @EnumValueAnnotation(label = "Node Boxes (Fixed Node Size)", value = "FIXED_BOX")
  @EnumValueAnnotation(label = "Mixed (Fixed Node Size)", value = "FIXED_MIXED")
  public final void setStyleItem( LayoutStyle value ) {
    this.styleItem = value;
  }

  private int gridSpacingItem;

  @Label("Grid Spacing")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 20)
  @DefaultValue(intValue = 15, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 1, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final int getGridSpacingItem() {
    return this.gridSpacingItem;
  }

  @Label("Grid Spacing")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 20)
  @DefaultValue(intValue = 15, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 1, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setGridSpacingItem( int value ) {
    this.gridSpacingItem = value;
  }

  private boolean edgeLengthReductionItem;

  @Label("Edge Length Reduction")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 30)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isEdgeLengthReductionItem() {
    return this.edgeLengthReductionItem;
  }

  @Label("Edge Length Reduction")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 30)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setEdgeLengthReductionItem( boolean value ) {
    this.edgeLengthReductionItem = value;
  }

  private boolean fromSketchModeItem;

  @Label("Use Drawing as Sketch")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 40)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isFromSketchModeItem() {
    return this.fromSketchModeItem;
  }

  @Label("Use Drawing as Sketch")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 40)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setFromSketchModeItem( boolean value ) {
    this.fromSketchModeItem = value;
  }

  private boolean crossingReductionItem;

  @Label("Crossing Reduction")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 50)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isCrossingReductionItem() {
    return this.crossingReductionItem;
  }

  @Label("Crossing Reduction")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 50)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setCrossingReductionItem( boolean value ) {
    this.crossingReductionItem = value;
  }

  private boolean perceivedBendsPostprocessingItem;

  @Label("Minimize Perceived Bends")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 60)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isPerceivedBendsPostprocessingItem() {
    return this.perceivedBendsPostprocessingItem;
  }

  @Label("Minimize Perceived Bends")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 60)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setPerceivedBendsPostprocessingItem( boolean value ) {
    this.perceivedBendsPostprocessingItem = value;
  }

  private boolean randomizationItem;

  @Label("Randomization")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 70)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isRandomizationItem() {
    return this.randomizationItem;
  }

  @Label("Randomization")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 70)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setRandomizationItem( boolean value ) {
    this.randomizationItem = value;
  }

  private boolean faceMaximizationItem;

  @Label("Face Maximization")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 80)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isFaceMaximizationItem() {
    return this.faceMaximizationItem;
  }

  @Label("Face Maximization")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 80)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setFaceMaximizationItem( boolean value ) {
    this.faceMaximizationItem = value;
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

  private EnumEdgeLabeling edgeLabelingItem = EnumEdgeLabeling.NONE;

  @Label("Edge Labeling")
  @OptionGroupAnnotation(name = "EdgePropertiesGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumEdgeLabeling.class, stringValue = "NONE")
  @EnumValueAnnotation(label = "None", value = "NONE")
  @EnumValueAnnotation(label = "Integrated", value = "INTEGRATED")
  @EnumValueAnnotation(label = "Generic", value = "GENERIC")
  public final EnumEdgeLabeling getEdgeLabelingItem() {
    return this.edgeLabelingItem;
  }

  @Label("Edge Labeling")
  @OptionGroupAnnotation(name = "EdgePropertiesGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumEdgeLabeling.class, stringValue = "NONE")
  @EnumValueAnnotation(label = "None", value = "NONE")
  @EnumValueAnnotation(label = "Integrated", value = "INTEGRATED")
  @EnumValueAnnotation(label = "Generic", value = "GENERIC")
  public final void setEdgeLabelingItem( EnumEdgeLabeling value ) {
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
    return getEdgeLabelingItem() == EnumEdgeLabeling.NONE;
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
    return getEdgeLabelingItem() == EnumEdgeLabeling.NONE;
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
    return getEdgeLabelingItem() == EnumEdgeLabeling.NONE;
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
    return getEdgeLabelingItem() == EnumEdgeLabeling.NONE || getLabelPlacementSideOfEdgeItem() == LayoutConfiguration.EnumLabelPlacementSideOfEdge.ON_EDGE;
  }

  private double minimumFirstSegmentLengthItem;

  @Label("Minimum First Segment Length")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 10)
  @DefaultValue(doubleValue = 15.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 1, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getMinimumFirstSegmentLengthItem() {
    return this.minimumFirstSegmentLengthItem;
  }

  @Label("Minimum First Segment Length")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 10)
  @DefaultValue(doubleValue = 15.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 1, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumFirstSegmentLengthItem( double value ) {
    this.minimumFirstSegmentLengthItem = value;
  }

  private double minimumSegmentLengthItem;

  @Label("Minimum Segment Length")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 20)
  @DefaultValue(doubleValue = 15.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 1, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getMinimumSegmentLengthItem() {
    return this.minimumSegmentLengthItem;
  }

  @Label("Minimum Segment Length")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 20)
  @DefaultValue(doubleValue = 15.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 1, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumSegmentLengthItem( double value ) {
    this.minimumSegmentLengthItem = value;
  }

  private double minimumLastSegmentLengthItem;

  @Label("Minimum Last Segment Length")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 30)
  @DefaultValue(doubleValue = 15.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 1, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getMinimumLastSegmentLengthItem() {
    return this.minimumLastSegmentLengthItem;
  }

  @Label("Minimum Last Segment Length")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 30)
  @DefaultValue(doubleValue = 15.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 1, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumLastSegmentLengthItem( double value ) {
    this.minimumLastSegmentLengthItem = value;
  }

  private boolean considerDirectionOfSelectedEdges;

  @Label("Consider Direction of Selected Edges")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 40)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isConsiderDirectionOfSelectedEdges() {
    return this.considerDirectionOfSelectedEdges;
  }

  @Label("Consider Direction of Selected Edges")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 40)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setConsiderDirectionOfSelectedEdges( boolean value ) {
    this.considerDirectionOfSelectedEdges = value;
  }

  private EnumGroupPolicy groupLayoutPolicyItem = EnumGroupPolicy.LAYOUT_GROUPS;

  @Label("Group Layout Policy")
  @OptionGroupAnnotation(name = "GroupingGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumGroupPolicy.class, stringValue = "LAYOUT_GROUPS")
  @EnumValueAnnotation(label = "Layout Groups", value = "LAYOUT_GROUPS")
  @EnumValueAnnotation(label = "Fix Contents of Groups", value = "FIX_GROUPS")
  @EnumValueAnnotation(label = "Ignore Groups", value = "IGNORE_GROUPS")
  public final EnumGroupPolicy getGroupLayoutPolicyItem() {
    return this.groupLayoutPolicyItem;
  }

  @Label("Group Layout Policy")
  @OptionGroupAnnotation(name = "GroupingGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumGroupPolicy.class, stringValue = "LAYOUT_GROUPS")
  @EnumValueAnnotation(label = "Layout Groups", value = "LAYOUT_GROUPS")
  @EnumValueAnnotation(label = "Fix Contents of Groups", value = "FIX_GROUPS")
  @EnumValueAnnotation(label = "Ignore Groups", value = "IGNORE_GROUPS")
  public final void setGroupLayoutPolicyItem( EnumGroupPolicy value ) {
    this.groupLayoutPolicyItem = value;
  }

  public final boolean isShouldDisableCrossingPostprocessingItem() {
    return isFromSketchModeItem();
  }

  public final boolean isShouldDisablePerceivedBendsPostprocessingItem() {
    return isFromSketchModeItem();
  }

  public final boolean isShouldDisableStyleItem() {
    return isFromSketchModeItem();
  }

  public final boolean isShouldDisableUseRandomizationItem() {
    return isFromSketchModeItem();
  }
}
