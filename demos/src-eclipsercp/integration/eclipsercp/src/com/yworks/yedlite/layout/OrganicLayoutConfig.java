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
package com.yworks.yedlite.layout;

import com.yworks.yedlite.toolkit.optionhandler.ComponentType;
import com.yworks.yedlite.toolkit.optionhandler.ComponentTypes;
import com.yworks.yedlite.toolkit.optionhandler.EnumValueAnnotation;
import com.yworks.yedlite.toolkit.optionhandler.Label;
import com.yworks.yedlite.toolkit.optionhandler.MinMax;
import com.yworks.yedlite.toolkit.optionhandler.OptionGroupAnnotation;
import com.yworks.yfiles.algorithms.YDimension;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graphml.DefaultValue;
import com.yworks.yfiles.layout.ComponentArrangementStyles;
import com.yworks.yfiles.layout.ComponentLayout;
import com.yworks.yfiles.layout.HideGroupsStage;
import com.yworks.yfiles.layout.ILayoutStage;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.labeling.GenericLabeling;
import com.yworks.yfiles.layout.organic.ChainSubstructureStyle;
import com.yworks.yfiles.layout.organic.CycleSubstructureStyle;
import com.yworks.yfiles.layout.organic.GroupNodeMode;
import com.yworks.yfiles.layout.organic.OutputRestriction;
import com.yworks.yfiles.layout.organic.ParallelSubstructureStyle;
import com.yworks.yfiles.layout.organic.Scope;
import com.yworks.yfiles.layout.organic.OrganicLayout;
import com.yworks.yfiles.layout.organic.StarSubstructureStyle;
import com.yworks.yfiles.view.GraphControl;

/**
 * Configuration options for the layout algorithm of the same name.
 */
@Label("OrganicLayout")
public class OrganicLayoutConfig extends LayoutConfiguration {
  private ILayoutStage preStage;

  /**
   * Setup default values for various configuration parameters.
   */
  public OrganicLayoutConfig() {
    OrganicLayout layout = new OrganicLayout();
    setScopeItem(Scope.ALL);
    setPreferredEdgeLengthItem(layout.getPreferredEdgeLength());
    setNodeOverlapsAllowedItem(layout.isNodeOverlapsAllowed());
    setMinimumNodeDistanceItem(10);
    setNodeEdgeOverlapsAvoidedItem(layout.isAvoidingNodeEdgeOverlapsEnabled());
    setCompactnessFactorItem(layout.getCompactnessFactor());
    setUseAutoClusteringItem(layout.isNodeClusteringEnabled());
    setAutoClusteringQualityItem(layout.getClusteringQuality());

    setRestrictOutputItem(EnumOutputRestrictions.NONE);
    setRectCageUseViewItem(true);
    setCageXItem(0.0d);
    setCageYItem(0.0d);
    setCageWidthItem(1000.0d);
    setCageHeightItem(1000.0d);

    setArCageUseViewItem(true);
    setCageRatioItem(1.0d);

    setGroupLayoutPolicyItem(EnumGroupLayoutPolicy.LAYOUT_GROUPS);

    setQualityTimeRatioItem(layout.getQualityTimeRatio());
    setMaximumDurationItem((int)(layout.getMaximumDuration() / 1000));
    setActivateDeterministicModeItem(layout.isDeterministicModeEnabled());

    setConsiderNodeLabelsItem(layout.isNodeLabelConsiderationEnabled());
    setEdgeLabelingItem(false);
    setLabelPlacementAlongEdgeItem(LayoutConfiguration.EnumLabelPlacementAlongEdge.CENTERED);
    setLabelPlacementSideOfEdgeItem(LayoutConfiguration.EnumLabelPlacementSideOfEdge.ON_EDGE);
    setLabelPlacementOrientationItem(LayoutConfiguration.EnumLabelPlacementOrientation.HORIZONTAL);
    setLabelPlacementDistanceItem(10.0d);

    setCycleSubstructureStyleItem(CycleSubstructureStyle.NONE);
    setChainSubstructureStyleItem(ChainSubstructureStyle.NONE);
    setStarSubstructureStyleItem(StarSubstructureStyle.NONE);
    setParallelSubstructureStyleItem(ParallelSubstructureStyle.NONE);
  }

  @Override
  protected ILayoutAlgorithm createConfiguredLayout(final GraphControl graphControl ) {
    OrganicLayout layout = new OrganicLayout();
    layout.setPreferredEdgeLength(getPreferredEdgeLengthItem());
    layout.setNodeLabelConsiderationEnabled(isConsiderNodeLabelsItem());
    layout.setNodeOverlapsAllowed(isNodeOverlapsAllowedItem());
    layout.setMinimumNodeDistance(getMinimumNodeDistanceItem());
    layout.setScope(getScopeItem());
    layout.setCompactnessFactor(getCompactnessFactorItem());
    layout.setNodeSizeConsiderationEnabled(true);
    layout.setNodeClusteringEnabled(isUseAutoClusteringItem());
    layout.setClusteringQuality(getAutoClusteringQualityItem());
    layout.setAvoidingNodeEdgeOverlapsEnabled(isNodeEdgeOverlapsAvoidedItem());
    layout.setDeterministicModeEnabled(isActivateDeterministicModeItem());
    layout.setMaximumDuration(1000 * getMaximumDurationItem());
    layout.setQualityTimeRatio(getQualityTimeRatioItem());
    layout.setChainSubstructureStyle(getChainSubstructureStyleItem());
    layout.setCycleSubstructureStyle(getCycleSubstructureStyleItem());
    layout.setStarSubstructureStyle(getStarSubstructureStyleItem());
    layout.setParallelSubstructureStyle(getParallelSubstructureStyleItem());

    if (isEdgeLabelingItem()) {
      GenericLabeling genericLabeling = new GenericLabeling();
      genericLabeling.setEdgeLabelPlacementEnabled(true);
      genericLabeling.setNodeLabelPlacementEnabled(false);
      layout.setLabelingEnabled(true);
      layout.setLabeling(genericLabeling);
    }
    ((ComponentLayout)layout.getComponentLayout()).setStyle(ComponentArrangementStyles.MULTI_ROWS);

    configureOutputRestrictions(graphControl, layout);
    configureGrouping(graphControl, layout);

    graphControl.getGraph().getMapperRegistry().createFunctionMapper(INode.class, Boolean.class, OrganicLayout.AFFECTED_NODES_DPKEY, new IsNodeSelected(graphControl));

    addPreferredPlacementDescriptor(graphControl.getGraph(), getLabelPlacementAlongEdgeItem(), getLabelPlacementSideOfEdgeItem(), getLabelPlacementOrientationItem(), getLabelPlacementDistanceItem());

    return layout;
  }

  /**
   * Called after the layout animation is done.
   */
  @Override
  protected void postProcess( GraphControl graphControl ) {
    graphControl.getGraph().getMapperRegistry().removeMapper(OrganicLayout.AFFECTED_NODES_DPKEY);
    graphControl.getGraph().getMapperRegistry().removeMapper(OrganicLayout.GROUP_NODE_MODE_DPKEY);
  }

  private void configureGrouping( GraphControl graphControl, OrganicLayout layout ) {
    final IGraph graph = graphControl.getGraph();
    switch (getGroupLayoutPolicyItem()) {
      case IGNORE_GROUPS:
        preStage = new HideGroupsStage((ILayoutAlgorithm)null);
        layout.prependStage(preStage);
        break;
      case LAYOUT_GROUPS:
        //do nothing...
        break;
      case FIX_GROUP_BOUNDS:
        if (graph != null) {
          graph.getMapperRegistry().createFunctionMapper(
                  INode.class, Object.class, OrganicLayout.GROUP_NODE_MODE_DPKEY,
                  node -> graph.isGroupNode(node) ? GroupNodeMode.FIX_BOUNDS : GroupNodeMode.NORMAL);
        }
        break;
      case FIX_GROUP_CONTENTS:
        if (graph != null) {
          graph.getMapperRegistry().createFunctionMapper(
                  INode.class, Object.class, OrganicLayout.GROUP_NODE_MODE_DPKEY,
                  node -> graph.isGroupNode(node) ? GroupNodeMode.FIX_CONTENTS : GroupNodeMode.NORMAL);
        }
        break;
    }
  }

  private void configureOutputRestrictions( GraphControl graphControl, OrganicLayout layout ) {
    boolean viewInfoIsAvailable = false;
    double[] visibleRect = getVisibleRectangle(graphControl);
    double x = 0, y = 0, w = 0, h = 0;
    if (visibleRect != null) {
      viewInfoIsAvailable = true;
      x = visibleRect[0];
      y = visibleRect[1];
      w = visibleRect[2];
      h = visibleRect[3];
    }
    switch (getRestrictOutputItem()) {
      case NONE:
        layout.setComponentLayoutEnabled(true);
        layout.setOutputRestriction(OutputRestriction.NONE);
        break;
      case OUTPUT_CAGE:
        if (!viewInfoIsAvailable || !isRectCageUseViewItem()) {
          x = getCageXItem();
          y = getCageYItem();
          w = getCageWidthItem();
          h = getCageHeightItem();
        }
        layout.setOutputRestriction(OutputRestriction.createRectangularCageRestriction(x, y, w, h));
        layout.setComponentLayoutEnabled(false);
        break;
      case OUTPUT_AR:
        double ratio;
        if (viewInfoIsAvailable && isArCageUseViewItem()) {
          ratio = w / h;
        } else {
          ratio = getCageRatioItem();
        }
        layout.setOutputRestriction(OutputRestriction.createAspectRatioRestriction(ratio));
        layout.setComponentLayoutEnabled(true);
        ((ComponentLayout)layout.getComponentLayout()).setPreferredSize(new YDimension(ratio * 100, 100));
        break;
      case OUTPUT_ELLIPTICAL_CAGE:
        if (!viewInfoIsAvailable || !isRectCageUseViewItem()) {
          x = getCageXItem();
          y = getCageYItem();
          w = getCageWidthItem();
          h = getCageHeightItem();
        }
        layout.setOutputRestriction(OutputRestriction.createEllipticalCageRestriction(x, y, w, h));
        layout.setComponentLayoutEnabled(false);
        break;
    }
  }

  private static double[] getVisibleRectangle( GraphControl graphControl ) {
    double[] visibleRect = new double[4];
    if (graphControl != null) {
      RectD viewPort = graphControl.getViewport();
      visibleRect[0] = viewPort.x;
      visibleRect[1] = viewPort.y;
      visibleRect[2] = viewPort.width;
      visibleRect[3] = viewPort.height;
      return visibleRect;
    }
    return null;
  }

  @Label("General")
  @OptionGroupAnnotation(name = "RootGroup", position = 10)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object VisualGroup;

  @Label("Restrictions")
  @OptionGroupAnnotation(name = "RootGroup", position = 20)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object RestrictionsGroup;

  @Label("Bounds")
  @OptionGroupAnnotation(name = "RestrictionsGroup", position = 20)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object CageGroup;

  @Label("Aspect Ratio")
  @OptionGroupAnnotation(name = "RestrictionsGroup", position = 30)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object ARGroup;

  @Label("Grouping")
  @OptionGroupAnnotation(name = "RootGroup", position = 30)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object GroupingGroup;

  @Label("Algorithm")
  @OptionGroupAnnotation(name = "RootGroup", position = 40)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object AlgorithmGroup;

  @Label("Labeling")
  @OptionGroupAnnotation(name = "RootGroup", position = 50)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object LabelingGroup;

  @Label("Substructure Layout")
  @OptionGroupAnnotation(name = "RootGroup", position = 60)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object SubstructureLayoutGroup;

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

  public enum EnumOutputRestrictions {
    NONE(0),

    OUTPUT_CAGE(1),

    OUTPUT_AR(2),

    OUTPUT_ELLIPTICAL_CAGE(3);

    private final int value;

    private EnumOutputRestrictions( final int value ) {
      this.value = value;
    }

    public int value() {
      return this.value;
    }

    public static final EnumOutputRestrictions fromOrdinal( int ordinal ) {
      for (EnumOutputRestrictions current : values()) {
        if (ordinal == current.value) return current;
      }
      throw new IllegalArgumentException("Enum has no value " + ordinal);
    }
  }

  public enum EnumGroupLayoutPolicy {
    LAYOUT_GROUPS(0),

    FIX_GROUP_BOUNDS(1),

    FIX_GROUP_CONTENTS(2),

    IGNORE_GROUPS(3);

    private final int value;

    private EnumGroupLayoutPolicy( final int value ) {
      this.value = value;
    }

    public int value() {
      return this.value;
    }

    public static final EnumGroupLayoutPolicy fromOrdinal( int ordinal ) {
      for (EnumGroupLayoutPolicy current : values()) {
        if (ordinal == current.value) return current;
      }
      throw new IllegalArgumentException("Enum has no value " + ordinal);
    }
  }

  private Scope scopeItem = null;

  @Label("Scope")
  @OptionGroupAnnotation(name = "VisualGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = Scope.class, stringValue = "ALL")
  @EnumValueAnnotation(label = "All", value = "ALL")
  @EnumValueAnnotation(label = "Mainly Selection", value = "MAINLY_SUBSET")
  @EnumValueAnnotation(label = "Selection", value = "SUBSET")
  public final Scope getScopeItem() {
    return this.scopeItem;
  }

  @Label("Scope")
  @OptionGroupAnnotation(name = "VisualGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = Scope.class, stringValue = "ALL")
  @EnumValueAnnotation(label = "All", value = "ALL")
  @EnumValueAnnotation(label = "Mainly Selection", value = "MAINLY_SUBSET")
  @EnumValueAnnotation(label = "Selection", value = "SUBSET")
  public final void setScopeItem( Scope value ) {
    this.scopeItem = value;
  }

  private double preferredEdgeLengthItem;

  @Label("Preferred Edge Length")
  @OptionGroupAnnotation(name = "VisualGroup", position = 20)
  @DefaultValue(doubleValue = 40.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 5, max = 500)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getPreferredEdgeLengthItem() {
    return this.preferredEdgeLengthItem;
  }

  @Label("Preferred Edge Length")
  @OptionGroupAnnotation(name = "VisualGroup", position = 20)
  @DefaultValue(doubleValue = 40.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 5, max = 500)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setPreferredEdgeLengthItem( double value ) {
    this.preferredEdgeLengthItem = value;
  }

  private boolean nodeOverlapsAllowedItem;

  @Label("Allow Overlapping Nodes")
  @OptionGroupAnnotation(name = "VisualGroup", position = 40)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isNodeOverlapsAllowedItem() {
    return this.nodeOverlapsAllowedItem;
  }

  @Label("Allow Overlapping Nodes")
  @OptionGroupAnnotation(name = "VisualGroup", position = 40)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setNodeOverlapsAllowedItem( boolean value ) {
    this.nodeOverlapsAllowedItem = value;
  }

  public final boolean isShouldDisableAllowNodeOverlapsItem() {
    return isConsiderNodeLabelsItem();
  }

  private int minimumNodeDistanceItem;

  @Label("Minimum Node Distance")
  @OptionGroupAnnotation(name = "VisualGroup", position = 30)
  @DefaultValue(intValue = 10, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final int getMinimumNodeDistanceItem() {
    return this.minimumNodeDistanceItem;
  }

  @Label("Minimum Node Distance")
  @OptionGroupAnnotation(name = "VisualGroup", position = 30)
  @DefaultValue(intValue = 10, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumNodeDistanceItem( int value ) {
    this.minimumNodeDistanceItem = value;
  }

  public final boolean isShouldDisableMinimumNodeDistanceItem() {
    return isNodeOverlapsAllowedItem() && !isConsiderNodeLabelsItem();
  }

  private boolean nodeEdgeOverlapsAvoidedItem;

  @Label("Avoid Node/Edge Overlaps")
  @OptionGroupAnnotation(name = "VisualGroup", position = 60)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isNodeEdgeOverlapsAvoidedItem() {
    return this.nodeEdgeOverlapsAvoidedItem;
  }

  @Label("Avoid Node/Edge Overlaps")
  @OptionGroupAnnotation(name = "VisualGroup", position = 60)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setNodeEdgeOverlapsAvoidedItem( boolean value ) {
    this.nodeEdgeOverlapsAvoidedItem = value;
  }

  private double compactnessFactorItem;

  @Label("Compactness Factor")
  @OptionGroupAnnotation(name = "VisualGroup", position = 70)
  @DefaultValue(doubleValue = 0.5d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.0d, max = 1.0d, step = 0.01d)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getCompactnessFactorItem() {
    return this.compactnessFactorItem;
  }

  @Label("Compactness Factor")
  @OptionGroupAnnotation(name = "VisualGroup", position = 70)
  @DefaultValue(doubleValue = 0.5d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.0d, max = 1.0d, step = 0.01d)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setCompactnessFactorItem( double value ) {
    this.compactnessFactorItem = value;
  }

  private boolean useAutoClusteringItem;

  @Label("Use Natural Clustering")
  @OptionGroupAnnotation(name = "VisualGroup", position = 80)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isUseAutoClusteringItem() {
    return this.useAutoClusteringItem;
  }

  @Label("Use Natural Clustering")
  @OptionGroupAnnotation(name = "VisualGroup", position = 80)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setUseAutoClusteringItem( boolean value ) {
    this.useAutoClusteringItem = value;
  }

  public final boolean isShouldDisableAutoClusteringQualityItem() {
    return isUseAutoClusteringItem() == false;
  }

  private double autoClusteringQualityItem;

  @Label("Natural Clustering Quality")
  @OptionGroupAnnotation(name = "VisualGroup", position = 90)
  @DefaultValue(doubleValue = 1.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.0d, max = 1.0d, step = 0.01d)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getAutoClusteringQualityItem() {
    return this.autoClusteringQualityItem;
  }

  @Label("Natural Clustering Quality")
  @OptionGroupAnnotation(name = "VisualGroup", position = 90)
  @DefaultValue(doubleValue = 1.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.0d, max = 1.0d, step = 0.01d)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setAutoClusteringQualityItem( double value ) {
    this.autoClusteringQualityItem = value;
  }

  private EnumOutputRestrictions restrictOutputItem = EnumOutputRestrictions.NONE;

  @Label("Output Area")
  @OptionGroupAnnotation(name = "RestrictionsGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumOutputRestrictions.class, stringValue = "NONE")
  @EnumValueAnnotation(label = "Unrestricted", value = "NONE")
  @EnumValueAnnotation(label = "Rectangular", value = "OUTPUT_CAGE")
  @EnumValueAnnotation(label = "Aspect Ratio", value = "OUTPUT_AR")
  @EnumValueAnnotation(label = "Elliptical", value = "OUTPUT_ELLIPTICAL_CAGE")
  public final EnumOutputRestrictions getRestrictOutputItem() {
    return this.restrictOutputItem;
  }

  @Label("Output Area")
  @OptionGroupAnnotation(name = "RestrictionsGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumOutputRestrictions.class, stringValue = "NONE")
  @EnumValueAnnotation(label = "Unrestricted", value = "NONE")
  @EnumValueAnnotation(label = "Rectangular", value = "OUTPUT_CAGE")
  @EnumValueAnnotation(label = "Aspect Ratio", value = "OUTPUT_AR")
  @EnumValueAnnotation(label = "Elliptical", value = "OUTPUT_ELLIPTICAL_CAGE")
  public final void setRestrictOutputItem( EnumOutputRestrictions value ) {
    this.restrictOutputItem = value;
  }

  public final boolean isShouldDisableCageGroup() {
    return getRestrictOutputItem() != EnumOutputRestrictions.OUTPUT_CAGE && getRestrictOutputItem() != EnumOutputRestrictions.OUTPUT_ELLIPTICAL_CAGE;
  }

  private boolean rectCageUseViewItem;

  @Label("Use Visible Area")
  @OptionGroupAnnotation(name = "CageGroup", position = 10)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isRectCageUseViewItem() {
    return this.rectCageUseViewItem;
  }

  @Label("Use Visible Area")
  @OptionGroupAnnotation(name = "CageGroup", position = 10)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setRectCageUseViewItem( boolean value ) {
    this.rectCageUseViewItem = value;
  }

  private double cageXItem;

  @Label("Top Left X")
  @OptionGroupAnnotation(name = "CageGroup", position = 20)
  @DefaultValue(doubleValue = 0.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  public final double getCageXItem() {
    return this.cageXItem;
  }

  @Label("Top Left X")
  @OptionGroupAnnotation(name = "CageGroup", position = 20)
  @DefaultValue(doubleValue = 0.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  public final void setCageXItem( double value ) {
    this.cageXItem = value;
  }

  public final boolean isShouldDisableCageXItem() {
    return isRectCageUseViewItem();
  }

  private double cageYItem;

  @Label("Top Left Y")
  @OptionGroupAnnotation(name = "CageGroup", position = 30)
  @DefaultValue(doubleValue = 0.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  public final double getCageYItem() {
    return this.cageYItem;
  }

  @Label("Top Left Y")
  @OptionGroupAnnotation(name = "CageGroup", position = 30)
  @DefaultValue(doubleValue = 0.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  public final void setCageYItem( double value ) {
    this.cageYItem = value;
  }

  public final boolean isShouldDisableCageYItem() {
    return isRectCageUseViewItem();
  }

  private double cageWidthItem;

  @Label("Width")
  @OptionGroupAnnotation(name = "CageGroup", position = 40)
  @DefaultValue(doubleValue = 1000.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 1)
  public final double getCageWidthItem() {
    return this.cageWidthItem;
  }

  @Label("Width")
  @OptionGroupAnnotation(name = "CageGroup", position = 40)
  @DefaultValue(doubleValue = 1000.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 1)
  public final void setCageWidthItem( double value ) {
    this.cageWidthItem = value;
  }

  public final boolean isShouldDisableCageWidthItem() {
    return isRectCageUseViewItem();
  }

  private double cageHeightItem;

  @Label("Height")
  @OptionGroupAnnotation(name = "CageGroup", position = 50)
  @DefaultValue(doubleValue = 1000.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 1)
  public final double getCageHeightItem() {
    return this.cageHeightItem;
  }

  @Label("Height")
  @OptionGroupAnnotation(name = "CageGroup", position = 50)
  @DefaultValue(doubleValue = 1000.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 1)
  public final void setCageHeightItem( double value ) {
    this.cageHeightItem = value;
  }

  public final boolean isShouldDisableCageHeightItem() {
    return isRectCageUseViewItem();
  }

  private boolean arCageUseViewItem;

  @Label("Use Ratio of View")
  @OptionGroupAnnotation(name = "ARGroup", position = 10)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isArCageUseViewItem() {
    return this.arCageUseViewItem;
  }

  @Label("Use Ratio of View")
  @OptionGroupAnnotation(name = "ARGroup", position = 10)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setArCageUseViewItem( boolean value ) {
    this.arCageUseViewItem = value;
  }

  private double cageRatioItem;

  @Label("Aspect Ratio")
  @OptionGroupAnnotation(name = "ARGroup", position = 20)
  @DefaultValue(doubleValue = 1.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.2d, max = 5.0d, step = 0.01d)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getCageRatioItem() {
    return this.cageRatioItem;
  }

  @Label("Aspect Ratio")
  @OptionGroupAnnotation(name = "ARGroup", position = 20)
  @DefaultValue(doubleValue = 1.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.2d, max = 5.0d, step = 0.01d)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setCageRatioItem( double value ) {
    this.cageRatioItem = value;
  }

  public final boolean isShouldDisableCageRatioItem() {
    return isArCageUseViewItem();
  }

  private EnumGroupLayoutPolicy groupLayoutPolicyItem = EnumGroupLayoutPolicy.LAYOUT_GROUPS;

  @Label("Group Layout Policy")
  @OptionGroupAnnotation(name = "GroupingGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumGroupLayoutPolicy.class, stringValue = "LAYOUT_GROUPS")
  @EnumValueAnnotation(label = "Layout Groups", value = "LAYOUT_GROUPS")
  @EnumValueAnnotation(label = "Fix Bounds of Groups", value = "FIX_GROUP_BOUNDS")
  @EnumValueAnnotation(label = "Fix Contents of Groups", value = "FIX_GROUP_CONTENTS")
  @EnumValueAnnotation(label = "Ignore Groups", value = "IGNORE_GROUPS")
  public final EnumGroupLayoutPolicy getGroupLayoutPolicyItem() {
    return this.groupLayoutPolicyItem;
  }

  @Label("Group Layout Policy")
  @OptionGroupAnnotation(name = "GroupingGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumGroupLayoutPolicy.class, stringValue = "LAYOUT_GROUPS")
  @EnumValueAnnotation(label = "Layout Groups", value = "LAYOUT_GROUPS")
  @EnumValueAnnotation(label = "Fix Bounds of Groups", value = "FIX_GROUP_BOUNDS")
  @EnumValueAnnotation(label = "Fix Contents of Groups", value = "FIX_GROUP_CONTENTS")
  @EnumValueAnnotation(label = "Ignore Groups", value = "IGNORE_GROUPS")
  public final void setGroupLayoutPolicyItem( EnumGroupLayoutPolicy value ) {
    this.groupLayoutPolicyItem = value;
  }

  private double qualityTimeRatioItem;

  @Label("Quality/Time Ratio")
  @OptionGroupAnnotation(name = "AlgorithmGroup", position = 10)
  @DefaultValue(doubleValue = 0.6d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.0d, max = 1.0d, step = 0.01d)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getQualityTimeRatioItem() {
    return this.qualityTimeRatioItem;
  }

  @Label("Quality/Time Ratio")
  @OptionGroupAnnotation(name = "AlgorithmGroup", position = 10)
  @DefaultValue(doubleValue = 0.6d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.0d, max = 1.0d, step = 0.01d)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setQualityTimeRatioItem( double value ) {
    this.qualityTimeRatioItem = value;
  }

  private int maximumDurationItem;

  @Label("Maximum Duration (sec)")
  @OptionGroupAnnotation(name = "AlgorithmGroup", position = 20)
  @DefaultValue(intValue = 30, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 0, max = 150)
  @ComponentType(ComponentTypes.SLIDER)
  public final int getMaximumDurationItem() {
    return this.maximumDurationItem;
  }

  @Label("Maximum Duration (sec)")
  @OptionGroupAnnotation(name = "AlgorithmGroup", position = 20)
  @DefaultValue(intValue = 30, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 0, max = 150)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMaximumDurationItem( int value ) {
    this.maximumDurationItem = value;
  }

  private boolean activateDeterministicModeItem;

  @Label("Deterministic Mode")
  @OptionGroupAnnotation(name = "AlgorithmGroup", position = 30)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isActivateDeterministicModeItem() {
    return this.activateDeterministicModeItem;
  }

  @Label("Deterministic Mode")
  @OptionGroupAnnotation(name = "AlgorithmGroup", position = 30)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setActivateDeterministicModeItem( boolean value ) {
    this.activateDeterministicModeItem = value;
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

  private boolean edgeLabelingItem;

  @Label("Edge Labeling")
  @OptionGroupAnnotation(name = "EdgePropertiesGroup", position = 10)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isEdgeLabelingItem() {
    return this.edgeLabelingItem;
  }

  @Label("Edge Labeling")
  @OptionGroupAnnotation(name = "EdgePropertiesGroup", position = 10)
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

  private CycleSubstructureStyle cycleSubstructureStyleItem = null;

  @Label("Cycles")
  @OptionGroupAnnotation(name = "SubstructureLayoutGroup", position = 0)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = CycleSubstructureStyle.class, stringValue = "NONE")
  @EnumValueAnnotation(label = "Ignore", value = "NONE")
  @EnumValueAnnotation(label = "Circular", value = "CIRCULAR")
  public final CycleSubstructureStyle getCycleSubstructureStyleItem() {
    return this.cycleSubstructureStyleItem;
  }

  @Label("Cycles")
  @OptionGroupAnnotation(name = "SubstructureLayoutGroup", position = 0)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = CycleSubstructureStyle.class, stringValue = "NONE")
  @EnumValueAnnotation(label = "Ignore", value = "NONE")
  @EnumValueAnnotation(label = "Circular", value = "CIRCULAR")
  public final void setCycleSubstructureStyleItem( CycleSubstructureStyle value ) {
    this.cycleSubstructureStyleItem = value;
  }

  private ChainSubstructureStyle chainSubstructureStyleItem = null;

  @Label("Chains")
  @OptionGroupAnnotation(name = "SubstructureLayoutGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = ChainSubstructureStyle.class, stringValue = "NONE")
  @EnumValueAnnotation(label = "Ignore", value = "NONE")
  @EnumValueAnnotation(label = "Straight-Line",value = "STRAIGHT_LINE")
  @EnumValueAnnotation(label = "Rectangular", value = "RECTANGULAR")
  public final ChainSubstructureStyle getChainSubstructureStyleItem() {
    return this.chainSubstructureStyleItem;
  }

  @Label("Chains")
  @OptionGroupAnnotation(name = "SubstructureLayoutGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = ChainSubstructureStyle.class, stringValue = "NONE")
  @EnumValueAnnotation(label = "Ignore", value = "NONE")
  @EnumValueAnnotation(label = "Straight-Line",value = "STRAIGHT_LINE")
  @EnumValueAnnotation(label = "Rectangular", value = "RECTANGULAR")
  public final void setChainSubstructureStyleItem( ChainSubstructureStyle value ) {
    this.chainSubstructureStyleItem = value;
  }

  private StarSubstructureStyle starSubstructureStyleItem = null;

  @Label("Stars")
  @OptionGroupAnnotation(name = "SubstructureLayoutGroup", position = 20)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = StarSubstructureStyle.class, stringValue = "NONE")
  @EnumValueAnnotation(label = "Ignore", value = "NONE")
  @EnumValueAnnotation(label = "Circular", value = "CIRCULAR")
  @EnumValueAnnotation(label = "Radial", value = "RADIAL")
  @EnumValueAnnotation(label = "Separated Radial",value = "SEPARATED_RADIAL")
  public final StarSubstructureStyle getStarSubstructureStyleItem() {
    return this.starSubstructureStyleItem;
  }

  @Label("Stars")
  @OptionGroupAnnotation(name = "SubstructureLayoutGroup", position = 20)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = StarSubstructureStyle.class, stringValue = "NONE")
  @EnumValueAnnotation(label = "Ignore", value = "NONE")
  @EnumValueAnnotation(label = "Circular", value = "CIRCULAR")
  @EnumValueAnnotation(label = "Radial", value = "RADIAL")
  @EnumValueAnnotation(label = "Separated Radial",value = "SEPARATED_RADIAL")
  public final void setStarSubstructureStyleItem( StarSubstructureStyle value ) {
    this.starSubstructureStyleItem = value;
  }

  private ParallelSubstructureStyle parallelSubstructureStyleItem = null;

  @Label("Parallel Structures")
  @OptionGroupAnnotation(name = "SubstructureLayoutGroup", position = 30)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = ParallelSubstructureStyle.class, stringValue = "NONE")
  @EnumValueAnnotation(label = "Ignore", value = "NONE")
  @EnumValueAnnotation(label = "Rectangular", value = "RECTANGULAR")
  @EnumValueAnnotation(label = "Radial", value = "RADIAL")
  @EnumValueAnnotation(label = "Straight-Line",value = "STRAIGHT_LINE")
  public final ParallelSubstructureStyle getParallelSubstructureStyleItem() {
    return this.parallelSubstructureStyleItem;
  }

  @Label("Parallel Structures")
  @OptionGroupAnnotation(name = "SubstructureLayoutGroup", position = 30)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = ParallelSubstructureStyle.class, stringValue = "NONE")
  @EnumValueAnnotation(label = "Ignore", value = "NONE")
  @EnumValueAnnotation(label = "Rectangular", value = "RECTANGULAR")
  @EnumValueAnnotation(label = "Radial", value = "RADIAL")
  @EnumValueAnnotation(label = "Straight-Line",value = "STRAIGHT_LINE")
  public final void setParallelSubstructureStyleItem( ParallelSubstructureStyle value ) {
    this.parallelSubstructureStyleItem = value;
  }

}
