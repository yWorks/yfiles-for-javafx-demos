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

import com.yworks.yedlite.toolkit.optionhandler.ComponentType;
import com.yworks.yedlite.toolkit.optionhandler.ComponentTypes;
import com.yworks.yedlite.toolkit.optionhandler.EnumValueAnnotation;
import com.yworks.yedlite.toolkit.optionhandler.Label;
import com.yworks.yedlite.toolkit.optionhandler.MinMax;
import com.yworks.yedlite.toolkit.optionhandler.OptionGroupAnnotation;
import com.yworks.yfiles.algorithms.YPoint;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graphml.DefaultValue;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.LayoutOrientation;
import com.yworks.yfiles.layout.OrientationLayout;
import com.yworks.yfiles.layout.hierarchic.AsIsLayerer;
import com.yworks.yfiles.layout.hierarchic.BFSLayerer;
import com.yworks.yfiles.layout.hierarchic.ComponentArrangementPolicy;
import com.yworks.yfiles.layout.hierarchic.GroupAlignmentPolicy;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayoutCore;
import com.yworks.yfiles.layout.hierarchic.LayeringStrategy;
import com.yworks.yfiles.layout.hierarchic.LayoutMode;
import com.yworks.yfiles.layout.hierarchic.EdgeLayoutDescriptor;
import com.yworks.yfiles.layout.hierarchic.EdgeRoutingStyle;
import com.yworks.yfiles.layout.hierarchic.GroupCompactionPolicy;
import com.yworks.yfiles.layout.hierarchic.IIncrementalHintsFactory;
import com.yworks.yfiles.layout.hierarchic.NodeLabelMode;
import com.yworks.yfiles.layout.hierarchic.NodeLayoutDescriptor;
import com.yworks.yfiles.layout.hierarchic.PortAssignmentMode;
import com.yworks.yfiles.layout.hierarchic.RoutingStyle;
import com.yworks.yfiles.layout.hierarchic.SimplexNodePlacer;
import com.yworks.yfiles.layout.hierarchic.TopLevelGroupToSwimlaneStage;
import com.yworks.yfiles.layout.labeling.GenericLabeling;
import com.yworks.yfiles.view.GraphControl;

import java.util.function.Function;

/**
 * Configuration options for the layout algorithm of the same name.
 */
@Label("HierarchicLayout")
public class HierarchicLayoutConfig extends LayoutConfiguration {
  /**
   * Setup default values for various configuration parameters.
   */
  public HierarchicLayoutConfig() {

    SelectedElementsIncrementallyItem = false;
    UseDrawingAsSketchItem = false;

    OrientationItem = LayoutOrientation.TOP_TO_BOTTOM;
    LayoutComponentsSeparatelyItem = false;
    SymmetricPlacementItem = true;
    MaximumDurationItem = 5;

    setNodeToNodeDistanceItem(30.0d);
    setNodeToEdgeDistanceItem(15.0d);
    setEdgeToEdgeDistanceItem(15.0d);
    setMinimumLayerDistanceItem(10.0d);

    setEdgeRoutingItem(EdgeRoutingStyle.ORTHOGONAL);
    setBackloopRoutingItem(false);
    setAutomaticEdgeGroupingEnabledItem(false);
    setDirectGroupContentRoutingItem(false);
    setMinimumFirstSegmentLengthItem(10.0d);
    setMinimumLastSegmentLengthItem(15.0d);
    setMinimumEdgeLengthItem(20.0d);
    setMinimumEdgeDistanceItem(15.0d);
    setMinimumSlopeItem(0.25d);

    setRankingPolicyItem(LayeringStrategy.HIERARCHICAL_OPTIMAL);
    setLayerAlignmentItem(0);
    setComponentArrangementPolicyItem(ComponentArrangementPolicy.TOPMOST);
    setNodeCompactionItem(false);

    setScaleItem(1.0d);
    setHaloItem(0.0d);
    setMinimumSizeItem(0.0d);
    setMaximumSizeItem(1000.0d);

    setConsiderNodeLabelsItem(true);

    setEdgeLabelingItem(EnumEdgeLabeling.NONE);
    setCompactEdgeLabelPlacementItem(true);

    setLabelPlacementOrientationItem(LayoutConfiguration.EnumLabelPlacementOrientation.HORIZONTAL);
    setLabelPlacementAlongEdgeItem(LayoutConfiguration.EnumLabelPlacementAlongEdge.CENTERED);
    setLabelPlacementSideOfEdgeItem(LayoutConfiguration.EnumLabelPlacementSideOfEdge.ON_EDGE);
    setLabelPlacementDistanceItem(10.0d);

    setGroupLayeringStrategyItem(GroupLayeringStrategyOptions.LAYOUT_GROUPS);
    setGroupAlignmentItem(GroupAlignmentPolicy.TOP);
    setGroupEnableCompactionItem(false);
    setGroupHorizontalCompactionItem(GroupCompactionPolicy.NONE);

    setTreatRootGroupAsSwimlanesItem(false);
    setUseOrderFromSketchItem(false);
    setSwimlineSpacingItem(0.0d);

    setGridEnabledItem(false);
    setGridSpacingItem(5);
    setGridPortAssignmentItem(PortAssignmentMode.DEFAULT);
  }

  @Override
  protected ILayoutAlgorithm createConfiguredLayout( GraphControl graphControl ) {
    HierarchicLayout layout = new HierarchicLayout();
    IGraph graph = graphControl.getGraph();

    //  mark incremental elements if required
    boolean fromSketch = UseDrawingAsSketchItem;
    boolean incrementalLayout = SelectedElementsIncrementallyItem;
    boolean selectedElements = graphControl.getSelection().getSelectedEdges().size() != 0 || graphControl.getSelection().getSelectedNodes().size() != 0;

    if (incrementalLayout && selectedElements) {
      // configure the mode
      layout.setLayoutMode(LayoutMode.INCREMENTAL);
      final IIncrementalHintsFactory ihf = layout.createIncrementalHintsFactory();
      graph.getMapperRegistry().createFunctionMapper(IModelItem.class, Object.class, HierarchicLayout.INCREMENTAL_HINTS_DPKEY, new Function<IModelItem, Object>(){
        public Object apply( IModelItem item ) {
          //Return the correct hint type for each model item that appears in one of these sets
          if (item instanceof INode && graphControl.getSelection().isSelected(item)) {
            return ihf.createLayerIncrementallyHint(item);
          }
          if (item instanceof IEdge && graphControl.getSelection().isSelected(item)) {
            return ihf.createSequenceIncrementallyHint(item);
          }
          return null;
        }
      });
    } else if (fromSketch) {
      layout.setLayoutMode(LayoutMode.INCREMENTAL);
    } else {
      layout.setLayoutMode(LayoutMode.FROM_SCRATCH);
    }


    // cast to implementation simplex
    ((SimplexNodePlacer)layout.getNodePlacer()).setBarycenterModeEnabled(SymmetricPlacementItem);


    layout.setComponentLayoutEnabled(LayoutComponentsSeparatelyItem);


    layout.setMinimumLayerDistance(getMinimumLayerDistanceItem());
    layout.setNodeToEdgeDistance(getNodeToEdgeDistanceItem());
    layout.setNodeToNodeDistance(getNodeToNodeDistanceItem());
    layout.setEdgeToEdgeDistance(getEdgeToEdgeDistanceItem());

    final NodeLayoutDescriptor nld = layout.getNodeLayoutDescriptor();
    EdgeLayoutDescriptor eld = layout.getEdgeLayoutDescriptor();

    layout.setAutomaticEdgeGroupingEnabled(isAutomaticEdgeGroupingEnabledItem());

    eld.setRoutingStyle(new RoutingStyle(getEdgeRoutingItem()));

    eld.setMinimumFirstSegmentLength(getMinimumFirstSegmentLengthItem());
    eld.setMinimumLastSegmentLength(getMinimumLastSegmentLengthItem());

    eld.setMinimumDistance(getMinimumEdgeDistanceItem());
    eld.setMinimumLength(getMinimumEdgeLengthItem());

    eld.setMinimumSlope(getMinimumSlopeItem());

    eld.setDirectGroupContentEdgeRoutingEnabled(isDirectGroupContentRoutingItem());

    nld.setMinimumDistance(Math.min(layout.getNodeToNodeDistance(), layout.getNodeToEdgeDistance()));
    nld.setMinimumLayerHeight(0);
    nld.setLayerAlignment(getLayerAlignmentItem());


    OrientationLayout ol = (OrientationLayout)layout.getOrientationLayout();
    ol.setOrientation(OrientationItem);

    if (getEdgeLabelingItem() != EnumEdgeLabeling.NONE) {
      if (getEdgeLabelingItem() == EnumEdgeLabeling.GENERIC) {
        GenericLabeling la = new GenericLabeling();
        la.setNodeLabelPlacementEnabled(false);
        la.setEdgeLabelPlacementEnabled(true);
        la.setAutoFlippingEnabled(true);
        layout.prependStage(la);
      } else if (getEdgeLabelingItem() == EnumEdgeLabeling.INTEGRATED) {
        layout.setIntegratedEdgeLabelingEnabled(true);
        if (layout.getNodePlacer() instanceof SimplexNodePlacer) {
          ((SimplexNodePlacer)layout.getNodePlacer()).setLabelCompactionEnabled(isCompactEdgeLabelPlacementItem());
        }
      }
    } else {
      layout.setIntegratedEdgeLabelingEnabled(false);
    }

    if (isConsiderNodeLabelsItem()) {
      layout.setNodeLabelConsiderationEnabled(true);
      layout.getNodeLayoutDescriptor().setNodeLabelMode(NodeLabelMode.CONSIDER_FOR_DRAWING);
    } else {
      layout.setNodeLabelConsiderationEnabled(false);
    }

    layout.setFromScratchLayeringStrategy(getRankingPolicyItem());
    layout.setComponentArrangementPolicy(getComponentArrangementPolicyItem());
    ((SimplexNodePlacer)layout.getNodePlacer()).setNodeCompactionEnabled(isNodeCompactionItem());

    //configure AsIsLayerer
    Object layerer = (layout.getLayoutMode() == LayoutMode.FROM_SCRATCH) ? layout.getFromScratchLayerer() : layout.getFixedElementsLayerer();
    if (layerer instanceof AsIsLayerer) {
      AsIsLayerer ail = (AsIsLayerer)layerer;
      ail.setNodeHalo(getHaloItem());
      ail.setNodeScalingFactor(getScaleItem());
      ail.setMinimumNodeSize(getMinimumSizeItem());
      ail.setMaximumNodeSize(getMaximumSizeItem());
    }

    //configure grouping
    ((SimplexNodePlacer)layout.getNodePlacer()).setGroupCompactionStrategy(getGroupHorizontalCompactionItem());

    if (!fromSketch && getGroupLayeringStrategyItem() == GroupLayeringStrategyOptions.LAYOUT_GROUPS) {
      GroupAlignmentPolicy alignmentPolicy = getGroupAlignmentItem();
      layout.setGroupAlignmentPolicy(alignmentPolicy);
      layout.setGroupCompactionEnabled(isGroupEnableCompactionItem());
      layout.setRecursiveGroupLayeringEnabled(true);
    } else {
      layout.setRecursiveGroupLayeringEnabled(false);
    }

    if (isTreatRootGroupAsSwimlanesItem()) {
      TopLevelGroupToSwimlaneStage stage = new TopLevelGroupToSwimlaneStage();
      stage.setSwimlanesFromSketchOrderingEnabled(isUseOrderFromSketchItem());
      stage.setSpacing(getSwimlineSpacingItem());
      layout.appendStage(stage);
    }

    layout.setBackLoopRoutingEnabled(isBackloopRoutingItem());
    layout.setMaximumDuration(MaximumDurationItem * 1000);


    if (getRankingPolicyItem() == LayeringStrategy.BFS) {
      graphControl.getGraph().getMapperRegistry().createFunctionMapper(IModelItem.class, Boolean.class, BFSLayerer.CORE_NODES_DPKEY, new Function<IModelItem, Boolean>(){
        public Boolean apply( IModelItem item ) {
          return (item instanceof INode && graphControl.getSelection().isSelected(item));
        }
      });
    }
    addPreferredPlacementDescriptor(graphControl.getGraph(), getLabelPlacementAlongEdgeItem(), getLabelPlacementSideOfEdgeItem(), getLabelPlacementOrientationItem(), getLabelPlacementDistanceItem());


    if (isGridEnabledItem()) {
      layout.setGridSpacing(getGridSpacingItem());
      graph.getMapperRegistry().createFunctionMapper(IModelItem.class, NodeLayoutDescriptor.class, HierarchicLayoutCore.NODE_LAYOUT_DESCRIPTOR_DPKEY, new Function<IModelItem, NodeLayoutDescriptor>(){
        public NodeLayoutDescriptor apply( IModelItem item ) {
          if (item instanceof INode) {
            INode node = (INode)item;
            NodeLayoutDescriptor descriptor = new NodeLayoutDescriptor();
            descriptor.setLayerAlignment(nld.getLayerAlignment());
            descriptor.setMinimumDistance(nld.getMinimumDistance());
            descriptor.setMinimumLayerHeight(nld.getMinimumLayerHeight());
            descriptor.setNodeLabelMode(nld.getNodeLabelMode());
            //anchor nodes on grid according to their alignment within the layer
            descriptor.setGridReference((new YPoint(0.0, (nld.getLayerAlignment() - 0.5) * node.getLayout().getHeight())));
            descriptor.setPortAssignment(getGridPortAssignmentItem());
            return descriptor;
          }
          return null;
        }
      });
    }

    return layout;
  }

  /**
   * Called after the layout animation is done.
   */
  @Override
  protected void postProcess( GraphControl graphControl ) {
    graphControl.getGraph().getMapperRegistry().removeMapper(HierarchicLayout.INCREMENTAL_HINTS_DPKEY);
    graphControl.getGraph().getMapperRegistry().removeMapper(BFSLayerer.CORE_NODES_DPKEY);
    graphControl.getGraph().getMapperRegistry().removeMapper(HierarchicLayoutCore.NODE_LAYOUT_DESCRIPTOR_DPKEY);
  }

  @Label("General")
  @OptionGroupAnnotation(name = "RootGroup", position = 10)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object GeneralGroup;

  @Label("Interactive Settings")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 10)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object InteractionGroup;

  @OptionGroupAnnotation(name = "GeneralGroup", position = 60)
  @Label("Minimum Distances")
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object DistanceGroup;

  @Label("Edges")
  @OptionGroupAnnotation(name = "RootGroup", position = 20)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object EdgeSettingsGroup;

  @Label("Layers")
  @OptionGroupAnnotation(name = "RootGroup", position = 30)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object RankGroup;

  @Label("Labeling")
  @OptionGroupAnnotation(name = "RootGroup", position = 40)
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

  @Label("Grouping")
  @OptionGroupAnnotation(name = "RootGroup", position = 50)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object GroupingGroup;

  @Label("Swimlanes")
  @OptionGroupAnnotation(name = "RootGroup", position = 60)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object SwimlanesGroup;

  @Label("Grid")
  @OptionGroupAnnotation(name = "RootGroup", position = 70)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object GridGroup;

  @OptionGroupAnnotation(name = "DescriptionGroup", position = 10)
  @ComponentType(ComponentTypes.HTML_BLOCK)
  public final String getDescriptionText() {
    return "<p style='margin-top:0'>The hierarchical layout style highlights the main direction or flow of a directed graph. The layout algorithm places " + "the nodes of a graph in hierarchically arranged layers such that the (majority of) its edges follows the overall " + "orientation, for example, top-to-bottom.</p>" + "<p>This style is tailored for application domains in which it is crucial to clearly visualize the dependency relations between " + "entities. In particular, if such relations form a chain of dependencies between entities, this " + "layout style nicely exhibits them. Generally, whenever the direction of information flow matters, the hierarchical " + "layout style is an invaluable tool.</p>" + "<p>Suitable application domains of this layout style include, for example:</p>" + "<ul>" + "<li>Workflow visualization</li>" + "<li>Software engineering like call graph visualization or activity diagrams</li>" + "<li>Process modeling</li>" + "<li>Database modeling and Entity-Relationship diagrams</li>" + "<li>Bioinformatics, for example biochemical pathways</li>" + "<li>Network management</li>" + "<li>Decision diagrams</li>" + "</ul>";
  }

  @Label("Selected Elements Incrementally")
  @OptionGroupAnnotation(name = "InteractionGroup", position = 10)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public boolean SelectedElementsIncrementallyItem;

  @Label("Use Drawing as Sketch")
  @OptionGroupAnnotation(name = "InteractionGroup", position = 20)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public boolean UseDrawingAsSketchItem;

  @Label("Orientation")
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LayoutOrientation.class, stringValue = "TOP_TO_BOTTOM")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 20)
  @EnumValueAnnotation(label = "Top to Bottom", value = "TOP_TO_BOTTOM")
  @EnumValueAnnotation(label = "Left to Right", value = "LEFT_TO_RIGHT")
  @EnumValueAnnotation(label = "Bottom to Top", value = "BOTTOM_TO_TOP")
  @EnumValueAnnotation(label = "Right to Left", value = "RIGHT_TO_LEFT")
  public LayoutOrientation OrientationItem;

  @Label("Layout Components Separately")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 30)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public boolean LayoutComponentsSeparatelyItem;

  @Label("Symmetric Placement")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 40)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public boolean SymmetricPlacementItem;

  @Label("Maximum Duration")
  @MinMax(min = 0, max = 150)
  @DefaultValue(intValue = 5, valueType = DefaultValue.ValueType.INT_TYPE)
  @OptionGroupAnnotation(name = "GeneralGroup", position = 50)
  @ComponentType(ComponentTypes.SLIDER)
  public int MaximumDurationItem;

  private double nodeToNodeDistanceItem;

  @Label("Node to Node Distance")
  @MinMax(min = 0, max = 100)
  @DefaultValue(doubleValue = 30.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @OptionGroupAnnotation(name = "DistanceGroup", position = 10)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getNodeToNodeDistanceItem() {
    return this.nodeToNodeDistanceItem;
  }

  @Label("Node to Node Distance")
  @MinMax(min = 0, max = 100)
  @DefaultValue(doubleValue = 30.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @OptionGroupAnnotation(name = "DistanceGroup", position = 10)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setNodeToNodeDistanceItem( double value ) {
    this.nodeToNodeDistanceItem = value;
  }

  private double nodeToEdgeDistanceItem;

  @Label("Node to Edge Distance")
  @MinMax(min = 0, max = 100)
  @DefaultValue(doubleValue = 15.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @OptionGroupAnnotation(name = "DistanceGroup", position = 20)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getNodeToEdgeDistanceItem() {
    return this.nodeToEdgeDistanceItem;
  }

  @Label("Node to Edge Distance")
  @MinMax(min = 0, max = 100)
  @DefaultValue(doubleValue = 15.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @OptionGroupAnnotation(name = "DistanceGroup", position = 20)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setNodeToEdgeDistanceItem( double value ) {
    this.nodeToEdgeDistanceItem = value;
  }

  private double edgeToEdgeDistanceItem;

  @Label("Edge to Edge Distance")
  @MinMax(min = 0, max = 100)
  @DefaultValue(doubleValue = 15.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @OptionGroupAnnotation(name = "DistanceGroup", position = 30)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getEdgeToEdgeDistanceItem() {
    return this.edgeToEdgeDistanceItem;
  }

  @Label("Edge to Edge Distance")
  @MinMax(min = 0, max = 100)
  @DefaultValue(doubleValue = 15.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @OptionGroupAnnotation(name = "DistanceGroup", position = 30)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setEdgeToEdgeDistanceItem( double value ) {
    this.edgeToEdgeDistanceItem = value;
  }

  private double minimumLayerDistanceItem;

  @Label("Layer to Layer Distance")
  @MinMax(min = 0, max = 100)
  @DefaultValue(doubleValue = 10.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @OptionGroupAnnotation(name = "DistanceGroup", position = 40)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getMinimumLayerDistanceItem() {
    return this.minimumLayerDistanceItem;
  }

  @Label("Layer to Layer Distance")
  @MinMax(min = 0, max = 100)
  @DefaultValue(doubleValue = 10.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @OptionGroupAnnotation(name = "DistanceGroup", position = 40)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumLayerDistanceItem( double value ) {
    this.minimumLayerDistanceItem = value;
  }

  private EdgeRoutingStyle edgeRoutingItem = null;

  @Label("Edge Routing")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EdgeRoutingStyle.class, stringValue = "ORTHOGONAL")
  @EnumValueAnnotation(label = "Octilinear", value = "OCTILINEAR")
  @EnumValueAnnotation(label = "Orthogonal", value = "ORTHOGONAL")
  @EnumValueAnnotation(label = "Polyline", value = "POLYLINE")
  public final EdgeRoutingStyle getEdgeRoutingItem() {
    return this.edgeRoutingItem;
  }

  @Label("Edge Routing")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EdgeRoutingStyle.class, stringValue = "ORTHOGONAL")
  @EnumValueAnnotation(label = "Octilinear", value = "OCTILINEAR")
  @EnumValueAnnotation(label = "Orthogonal", value = "ORTHOGONAL")
  @EnumValueAnnotation(label = "Polyline", value = "POLYLINE")
  public final void setEdgeRoutingItem( EdgeRoutingStyle value ) {
    this.edgeRoutingItem = value;
  }

  private boolean backloopRoutingItem;

  @Label("Backloop Routing")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 20)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isBackloopRoutingItem() {
    return this.backloopRoutingItem;
  }

  @Label("Backloop Routing")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 20)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setBackloopRoutingItem( boolean value ) {
    this.backloopRoutingItem = value;
  }

  private boolean automaticEdgeGroupingEnabledItem;

  @Label("Automatic Edge Grouping")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 30)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isAutomaticEdgeGroupingEnabledItem() {
    return this.automaticEdgeGroupingEnabledItem;
  }

  @Label("Automatic Edge Grouping")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 30)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setAutomaticEdgeGroupingEnabledItem( boolean value ) {
    this.automaticEdgeGroupingEnabledItem = value;
  }

  private boolean directGroupContentRoutingItem;

  @Label("Direct Group Content Routing")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 40)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isDirectGroupContentRoutingItem() {
    return this.directGroupContentRoutingItem;
  }

  @Label("Direct Group Content Routing")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 40)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setDirectGroupContentRoutingItem( boolean value ) {
    this.directGroupContentRoutingItem = value;
  }

  private double minimumFirstSegmentLengthItem;

  @Label("Minimum First Segment Length")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 50)
  @DefaultValue(doubleValue = 10.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getMinimumFirstSegmentLengthItem() {
    return this.minimumFirstSegmentLengthItem;
  }

  @Label("Minimum First Segment Length")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 50)
  @DefaultValue(doubleValue = 10.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumFirstSegmentLengthItem( double value ) {
    this.minimumFirstSegmentLengthItem = value;
  }

  private double minimumLastSegmentLengthItem;

  @Label("Minimum Last Segment Length")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 60)
  @DefaultValue(doubleValue = 15.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getMinimumLastSegmentLengthItem() {
    return this.minimumLastSegmentLengthItem;
  }

  @Label("Minimum Last Segment Length")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 60)
  @DefaultValue(doubleValue = 15.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumLastSegmentLengthItem( double value ) {
    this.minimumLastSegmentLengthItem = value;
  }

  private double minimumEdgeLengthItem;

  @Label("Minimum Edge Length")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 70)
  @DefaultValue(doubleValue = 20.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getMinimumEdgeLengthItem() {
    return this.minimumEdgeLengthItem;
  }

  @Label("Minimum Edge Length")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 70)
  @DefaultValue(doubleValue = 20.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumEdgeLengthItem( double value ) {
    this.minimumEdgeLengthItem = value;
  }

  private double minimumEdgeDistanceItem;

  @Label("Minimum Edge Distance")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 80)
  @DefaultValue(doubleValue = 15.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getMinimumEdgeDistanceItem() {
    return this.minimumEdgeDistanceItem;
  }

  @Label("Minimum Edge Distance")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 80)
  @DefaultValue(doubleValue = 15.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumEdgeDistanceItem( double value ) {
    this.minimumEdgeDistanceItem = value;
  }

  private double minimumSlopeItem;

  @MinMax(min = 0.0d, max = 5.0d, step = 0.01d)
  @Label("Minimum Slope")
  @DefaultValue(doubleValue = 0.25d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 90)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getMinimumSlopeItem() {
    return this.minimumSlopeItem;
  }

  @MinMax(min = 0.0d, max = 5.0d, step = 0.01d)
  @Label("Minimum Slope")
  @DefaultValue(doubleValue = 0.25d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 90)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumSlopeItem( double value ) {
    this.minimumSlopeItem = value;
  }

  public final boolean isShouldDisableMinimumSlopeItem() {
    return getEdgeRoutingItem() != EdgeRoutingStyle.POLYLINE;
  }

  private LayeringStrategy rankingPolicyItem = null;

  @Label("Layer Assignment Policy")
  @OptionGroupAnnotation(name = "RankGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LayeringStrategy.class, stringValue = "HIERARCHICAL_OPTIMAL")
  @EnumValueAnnotation(label = "Hierarchical - Optimal", value = "HIERARCHICAL_OPTIMAL")
  @EnumValueAnnotation(label = "Hierarchical - Tight Tree Heuristic", value = "HIERARCHICAL_TIGHT_TREE")
  @EnumValueAnnotation(label = "BFS Layering", value = "BFS")
  @EnumValueAnnotation(label = "From Sketch", value = "FROM_SKETCH")
  @EnumValueAnnotation(label = "Hierarchical - Topmost", value = "HIERARCHICAL_TOPMOST")
  public final LayeringStrategy getRankingPolicyItem() {
    return this.rankingPolicyItem;
  }

  @Label("Layer Assignment Policy")
  @OptionGroupAnnotation(name = "RankGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LayeringStrategy.class, stringValue = "HIERARCHICAL_OPTIMAL")
  @EnumValueAnnotation(label = "Hierarchical - Optimal", value = "HIERARCHICAL_OPTIMAL")
  @EnumValueAnnotation(label = "Hierarchical - Tight Tree Heuristic", value = "HIERARCHICAL_TIGHT_TREE")
  @EnumValueAnnotation(label = "BFS Layering", value = "BFS")
  @EnumValueAnnotation(label = "From Sketch", value = "FROM_SKETCH")
  @EnumValueAnnotation(label = "Hierarchical - Topmost", value = "HIERARCHICAL_TOPMOST")
  public final void setRankingPolicyItem( LayeringStrategy value ) {
    this.rankingPolicyItem = value;
  }

  private double layerAlignmentItem;

  @Label("Alignment within Layer")
  @OptionGroupAnnotation(name = "RankGroup", position = 20)
  @DefaultValue(doubleValue = 0.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @EnumValueAnnotation(label = "Top Border of Nodes", value = "0")
  @EnumValueAnnotation(label = "Center of Nodes", value = "0.5")
  @EnumValueAnnotation(label = "Bottom Border of Nodes", value = "1")
  public final double getLayerAlignmentItem() {
    return this.layerAlignmentItem;
  }

  @Label("Alignment within Layer")
  @OptionGroupAnnotation(name = "RankGroup", position = 20)
  @DefaultValue(doubleValue = 0.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @EnumValueAnnotation(label = "Top Border of Nodes", value = "0")
  @EnumValueAnnotation(label = "Center of Nodes", value = "0.5")
  @EnumValueAnnotation(label = "Bottom Border of Nodes", value = "1")
  public final void setLayerAlignmentItem( double value ) {
    this.layerAlignmentItem = value;
  }

  private ComponentArrangementPolicy componentArrangementPolicyItem = null;

  @Label("Component Arrangement")
  @OptionGroupAnnotation(name = "RankGroup", position = 30)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = ComponentArrangementPolicy.class, stringValue = "TOPMOST")
  @EnumValueAnnotation(label = "Topmost", value = "TOPMOST")
  @EnumValueAnnotation(label = "Compact", value = "COMPACT")
  public final ComponentArrangementPolicy getComponentArrangementPolicyItem() {
    return this.componentArrangementPolicyItem;
  }

  @Label("Component Arrangement")
  @OptionGroupAnnotation(name = "RankGroup", position = 30)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = ComponentArrangementPolicy.class, stringValue = "TOPMOST")
  @EnumValueAnnotation(label = "Topmost", value = "TOPMOST")
  @EnumValueAnnotation(label = "Compact", value = "COMPACT")
  public final void setComponentArrangementPolicyItem( ComponentArrangementPolicy value ) {
    this.componentArrangementPolicyItem = value;
  }

  private boolean nodeCompactionItem;

  @OptionGroupAnnotation(name = "RankGroup", position = 40)
  @Label("Stacked Placement")
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isNodeCompactionItem() {
    return this.nodeCompactionItem;
  }

  @OptionGroupAnnotation(name = "RankGroup", position = 40)
  @Label("Stacked Placement")
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setNodeCompactionItem( boolean value ) {
    this.nodeCompactionItem = value;
  }

  @OptionGroupAnnotation(name = "RankGroup", position = 50)
  @Label("From Sketch Layer Assignment")
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object SketchGroup;

  private double scaleItem;

  @OptionGroupAnnotation(name = "SketchGroup", position = 10)
  @MinMax(min = 0.0d, max = 5.0d, step = 0.01d)
  @DefaultValue(doubleValue = 1.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @Label("Scale")
  @ComponentType(ComponentTypes.SLIDER)
  public final double getScaleItem() {
    return this.scaleItem;
  }

  @OptionGroupAnnotation(name = "SketchGroup", position = 10)
  @MinMax(min = 0.0d, max = 5.0d, step = 0.01d)
  @DefaultValue(doubleValue = 1.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @Label("Scale")
  @ComponentType(ComponentTypes.SLIDER)
  public final void setScaleItem( double value ) {
    this.scaleItem = value;
  }

  public final boolean isShouldDisableScaleItem() {
    return this.getRankingPolicyItem() != LayeringStrategy.FROM_SKETCH;
  }

  private double haloItem;

  @OptionGroupAnnotation(name = "SketchGroup", position = 20)
  @Label("Halo")
  @DefaultValue(doubleValue = 0.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getHaloItem() {
    return this.haloItem;
  }

  @OptionGroupAnnotation(name = "SketchGroup", position = 20)
  @Label("Halo")
  @DefaultValue(doubleValue = 0.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setHaloItem( double value ) {
    this.haloItem = value;
  }

  public final boolean isShouldDisableHaloItem() {
    return this.getRankingPolicyItem() != LayeringStrategy.FROM_SKETCH;
  }

  private double minimumSizeItem;

  @OptionGroupAnnotation(name = "SketchGroup", position = 30)
  @Label("Minimum Size")
  @DefaultValue(doubleValue = 0.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 1000)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getMinimumSizeItem() {
    return this.minimumSizeItem;
  }

  @OptionGroupAnnotation(name = "SketchGroup", position = 30)
  @Label("Minimum Size")
  @DefaultValue(doubleValue = 0.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 1000)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumSizeItem( double value ) {
    this.minimumSizeItem = value;
  }

  public final boolean isShouldDisableMinimumSizeItem() {
    return this.getRankingPolicyItem() != LayeringStrategy.FROM_SKETCH;
  }

  private double maximumSizeItem;

  @OptionGroupAnnotation(name = "SketchGroup", position = 40)
  @Label("Maximum Size")
  @DefaultValue(doubleValue = 1000.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 1000)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getMaximumSizeItem() {
    return this.maximumSizeItem;
  }

  @OptionGroupAnnotation(name = "SketchGroup", position = 40)
  @Label("Maximum Size")
  @DefaultValue(doubleValue = 1000.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 1000)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMaximumSizeItem( double value ) {
    this.maximumSizeItem = value;
  }

  public final boolean isShouldDisableMaximumSizeItem() {
    return this.getRankingPolicyItem() != LayeringStrategy.FROM_SKETCH;
  }

  private boolean considerNodeLabelsItem;

  @OptionGroupAnnotation(name = "NodePropertiesGroup", position = 10)
  @Label("Consider Node Labels")
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isConsiderNodeLabelsItem() {
    return this.considerNodeLabelsItem;
  }

  @OptionGroupAnnotation(name = "NodePropertiesGroup", position = 10)
  @Label("Consider Node Labels")
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setConsiderNodeLabelsItem( boolean value ) {
    this.considerNodeLabelsItem = value;
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

  private boolean compactEdgeLabelPlacementItem;

  @Label("Compact Placement")
  @OptionGroupAnnotation(name = "EdgePropertiesGroup", position = 30)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isCompactEdgeLabelPlacementItem() {
    return this.compactEdgeLabelPlacementItem;
  }

  @Label("Compact Placement")
  @OptionGroupAnnotation(name = "EdgePropertiesGroup", position = 30)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setCompactEdgeLabelPlacementItem( boolean value ) {
    this.compactEdgeLabelPlacementItem = value;
  }

  public final boolean isShouldDisableCompactEdgeLabelPlacementItem() {
    return getEdgeLabelingItem() != EnumEdgeLabeling.INTEGRATED;
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
  @EnumValueAnnotation(label = "At Source Port", value = "AT_SOURCE_PORT")
  @EnumValueAnnotation(label = "At Target Port", value = "AT_TARGET_PORT")
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
  @EnumValueAnnotation(label = "At Source Port", value = "AT_SOURCE_PORT")
  @EnumValueAnnotation(label = "At Target Port", value = "AT_TARGET_PORT")
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

  public enum GroupLayeringStrategyOptions {
    LAYOUT_GROUPS(0),

    IGNORE_GROUPS(1);

    private final int value;

    private GroupLayeringStrategyOptions( final int value ) {
      this.value = value;
    }

    public int value() {
      return this.value;
    }

    public static final GroupLayeringStrategyOptions fromOrdinal( int ordinal ) {
      for (GroupLayeringStrategyOptions current : values()) {
        if (ordinal == current.value) return current;
      }
      throw new IllegalArgumentException("Enum has no value " + ordinal);
    }
  }

  private GroupLayeringStrategyOptions groupLayeringStrategyItem = GroupLayeringStrategyOptions.LAYOUT_GROUPS;

  @OptionGroupAnnotation(name = "GroupingGroup", position = 10)
  @Label("Layering Strategy")
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = GroupLayeringStrategyOptions.class, stringValue = "LAYOUT_GROUPS")
  @EnumValueAnnotation(label = "Layout Groups", value = "LAYOUT_GROUPS")
  @EnumValueAnnotation(label = "Ignore Groups", value = "IGNORE_GROUPS")
  public final GroupLayeringStrategyOptions getGroupLayeringStrategyItem() {
    return this.groupLayeringStrategyItem;
  }

  @OptionGroupAnnotation(name = "GroupingGroup", position = 10)
  @Label("Layering Strategy")
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = GroupLayeringStrategyOptions.class, stringValue = "LAYOUT_GROUPS")
  @EnumValueAnnotation(label = "Layout Groups", value = "LAYOUT_GROUPS")
  @EnumValueAnnotation(label = "Ignore Groups", value = "IGNORE_GROUPS")
  public final void setGroupLayeringStrategyItem( GroupLayeringStrategyOptions value ) {
    this.groupLayeringStrategyItem = value;
  }

  public final boolean isShouldDisableGroupLayeringStrategyItem() {
    return UseDrawingAsSketchItem;
  }

  private GroupAlignmentPolicy groupAlignmentItem = null;

  @OptionGroupAnnotation(name = "GroupingGroup", position = 20)
  @Label("Vertical Alignment")
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = GroupAlignmentPolicy.class, stringValue = "TOP")
  @EnumValueAnnotation(label = "Top", value = "TOP")
  @EnumValueAnnotation(label = "Center", value = "CENTER")
  @EnumValueAnnotation(label = "Bottom", value = "BOTTOM")
  public final GroupAlignmentPolicy getGroupAlignmentItem() {
    return this.groupAlignmentItem;
  }

  @OptionGroupAnnotation(name = "GroupingGroup", position = 20)
  @Label("Vertical Alignment")
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = GroupAlignmentPolicy.class, stringValue = "TOP")
  @EnumValueAnnotation(label = "Top", value = "TOP")
  @EnumValueAnnotation(label = "Center", value = "CENTER")
  @EnumValueAnnotation(label = "Bottom", value = "BOTTOM")
  public final void setGroupAlignmentItem( GroupAlignmentPolicy value ) {
    this.groupAlignmentItem = value;
  }

  public final boolean isShouldDisableGroupAlignmentItem() {
    return getGroupLayeringStrategyItem() != GroupLayeringStrategyOptions.LAYOUT_GROUPS || isGroupEnableCompactionItem();
  }

  private boolean groupEnableCompactionItem;

  @OptionGroupAnnotation(name = "GroupingGroup", position = 30)
  @Label("Compact Layers")
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isGroupEnableCompactionItem() {
    return this.groupEnableCompactionItem;
  }

  @OptionGroupAnnotation(name = "GroupingGroup", position = 30)
  @Label("Compact Layers")
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setGroupEnableCompactionItem( boolean value ) {
    this.groupEnableCompactionItem = value;
  }

  public final boolean isShouldDisableGroupEnableCompactionItem() {
    return getGroupLayeringStrategyItem() != GroupLayeringStrategyOptions.LAYOUT_GROUPS || UseDrawingAsSketchItem;
  }

  private GroupCompactionPolicy groupHorizontalCompactionItem = GroupCompactionPolicy.NONE;

  @OptionGroupAnnotation(name = "GroupingGroup", position = 40)
  @Label("Horizontal Group Compaction")
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = GroupCompactionPolicy.class, stringValue = "NONE")
  @EnumValueAnnotation(label = "Weak", value = "NONE")
  @EnumValueAnnotation(label = "Strong", value = "MAXIMAL")
  public final GroupCompactionPolicy getGroupHorizontalCompactionItem() {
    return this.groupHorizontalCompactionItem;
  }

  @OptionGroupAnnotation(name = "GroupingGroup", position = 40)
  @Label("Horizontal Group Compaction")
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = GroupCompactionPolicy.class, stringValue = "NONE")
  @EnumValueAnnotation(label = "Weak", value = "NONE")
  @EnumValueAnnotation(label = "Strong", value = "MAXIMAL")
  public final void setGroupHorizontalCompactionItem( GroupCompactionPolicy value ) {
    this.groupHorizontalCompactionItem = value;
  }

  private boolean treatRootGroupAsSwimlanesItem;

  @OptionGroupAnnotation(name = "SwimlanesGroup", position = 10)
  @Label("Treat Groups as Swimlanes")
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isTreatRootGroupAsSwimlanesItem() {
    return this.treatRootGroupAsSwimlanesItem;
  }

  @OptionGroupAnnotation(name = "SwimlanesGroup", position = 10)
  @Label("Treat Groups as Swimlanes")
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setTreatRootGroupAsSwimlanesItem( boolean value ) {
    this.treatRootGroupAsSwimlanesItem = value;
  }

  private boolean useOrderFromSketchItem;

  @OptionGroupAnnotation(name = "SwimlanesGroup", position = 20)
  @Label("Use Sketch for Lane Order")
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isUseOrderFromSketchItem() {
    return this.useOrderFromSketchItem;
  }

  @OptionGroupAnnotation(name = "SwimlanesGroup", position = 20)
  @Label("Use Sketch for Lane Order")
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setUseOrderFromSketchItem( boolean value ) {
    this.useOrderFromSketchItem = value;
  }

  private double swimlineSpacingItem;

  @OptionGroupAnnotation(name = "SwimlanesGroup", position = 30)
  @Label("Lane Spacing")
  @DefaultValue(doubleValue = 0.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getSwimlineSpacingItem() {
    return this.swimlineSpacingItem;
  }

  @OptionGroupAnnotation(name = "SwimlanesGroup", position = 30)
  @Label("Lane Spacing")
  @DefaultValue(doubleValue = 0.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setSwimlineSpacingItem( double value ) {
    this.swimlineSpacingItem = value;
  }

  public final boolean isShouldDisableUseOrderFromSketchItem() {
    return !isTreatRootGroupAsSwimlanesItem();
  }

  public final boolean isShouldDisableSwimlineSpacingItem() {
    return !isTreatRootGroupAsSwimlanesItem();
  }

  private boolean gridEnabledItem;

  @OptionGroupAnnotation(name = "GridGroup", position = 10)
  @Label("Grid")
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isGridEnabledItem() {
    return this.gridEnabledItem;
  }

  @OptionGroupAnnotation(name = "GridGroup", position = 10)
  @Label("Grid")
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setGridEnabledItem( boolean value ) {
    this.gridEnabledItem = value;
  }

  private double gridSpacingItem;

  @OptionGroupAnnotation(name = "GridGroup", position = 20)
  @Label("Grid Spacing")
  @DefaultValue(doubleValue = 5.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getGridSpacingItem() {
    return this.gridSpacingItem;
  }

  @OptionGroupAnnotation(name = "GridGroup", position = 20)
  @Label("Grid Spacing")
  @DefaultValue(doubleValue = 5.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setGridSpacingItem( double value ) {
    this.gridSpacingItem = value;
  }

  private PortAssignmentMode gridPortAssignmentItem = null;

  @OptionGroupAnnotation(name = "GridGroup", position = 30)
  @Label("Grid Port Style")
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = PortAssignmentMode.class, stringValue = "DEFAULT")
  @EnumValueAnnotation(label = "Default", value = "DEFAULT")
  @EnumValueAnnotation(label = "On Grid", value = "ON_GRID")
  @EnumValueAnnotation(label = "On Subgrid", value = "ON_SUBGRID")
  public final PortAssignmentMode getGridPortAssignmentItem() {
    return this.gridPortAssignmentItem;
  }

  @OptionGroupAnnotation(name = "GridGroup", position = 30)
  @Label("Grid Port Style")
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = PortAssignmentMode.class, stringValue = "DEFAULT")
  @EnumValueAnnotation(label = "Default", value = "DEFAULT")
  @EnumValueAnnotation(label = "On Grid", value = "ON_GRID")
  @EnumValueAnnotation(label = "On Subgrid", value = "ON_SUBGRID")
  public final void setGridPortAssignmentItem( PortAssignmentMode value ) {
    this.gridPortAssignmentItem = value;
  }

}
